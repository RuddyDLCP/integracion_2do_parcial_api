package com.grupo.cheques.services;

import com.grupo.cheques.config.ContabilidadProperties;
import com.grupo.cheques.dto.integracion.AsientoContablePayload;
import com.grupo.cheques.dto.integracion.AsientoDetalleDto;
import com.grupo.cheques.dto.integracion.AuxiliarRefDto;
import com.grupo.cheques.dto.integracion.CuentaRefDto;
import com.grupo.cheques.dto.integracion.EnviarAsientoResponse;
import com.grupo.cheques.models.SolicitudCheque;
import com.grupo.cheques.repositories.SolicitudChequeRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class IntegracionContabilidadService {

    private static final String ESTADO_CHEQUE_GENERADO = "Cheque Generado";
    private static final double TOLERANCIA_CUADRE = 0.02;

    private final SolicitudChequeRepository solicitudRepository;
    private final ContabilidadProperties properties;
    private final RestClient contabilidadRestClient;

    public IntegracionContabilidadService(
            SolicitudChequeRepository solicitudRepository,
            ContabilidadProperties properties,
            @Qualifier("contabilidadRestClient") RestClient contabilidadRestClient) {
        this.solicitudRepository = solicitudRepository;
        this.properties = properties;
        this.contabilidadRestClient = contabilidadRestClient;
    }

    public EnviarAsientoResponse soloVistaPrevia(YearMonth periodo) {
        return construirRespuesta(periodo, true);
    }

    public EnviarAsientoResponse enviarAsiento(YearMonth periodo) {
        if (!properties.hasUrl()) {
            return construirRespuesta(periodo, true);
        }
        BuiltPayload built = construirPayloadInterno(periodo);
        AsientoContablePayload payload = built.payload();
        String periodoStr = built.periodoStr();
        int cantidad = built.cantidad();
        double total = built.total();

        RestClient.RequestBodySpec request = contabilidadRestClient.post()
                .uri(URI.create(properties.getUrl()))
                .contentType(MediaType.APPLICATION_JSON);
        if (properties.hasApiKey()) {
            request = request.header(properties.getApiKeyHeader(), properties.getApiKey());
        }

        try {
            ResponseEntity<String> entity = request.body(payload).retrieve().toEntity(String.class);
            boolean ok = entity.getStatusCode().is2xxSuccessful();
            String body = entity.getBody() != null ? entity.getBody() : "";
            String msg = ok
                    ? "Asiento enviado a Contabilidad correctamente."
                    : "Contabilidad respondió con código " + entity.getStatusCode().value();
            return EnviarAsientoResponse.enviado(
                    periodoStr,
                    cantidad,
                    total,
                    payload,
                    ok,
                    entity.getStatusCode().value(),
                    body,
                    msg);
        } catch (RestClientResponseException e) {
            String body = e.getResponseBodyAsString();
            return EnviarAsientoResponse.enviado(
                    periodoStr,
                    cantidad,
                    total,
                    payload,
                    false,
                    e.getStatusCode().value(),
                    body,
                    "Error al llamar a Contabilidad: HTTP " + e.getStatusCode().value());
        } catch (RestClientException e) {
            return EnviarAsientoResponse.enviado(
                    periodoStr,
                    cantidad,
                    total,
                    payload,
                    false,
                    null,
                    e.getMessage(),
                    "Error de conexión con Contabilidad: " + e.getMessage());
        }
    }

    private EnviarAsientoResponse construirRespuesta(YearMonth periodo, boolean simulacionForzada) {
        BuiltPayload built = construirPayloadInterno(periodo);
        String msg = "Vista previa del asiento generada correctamente.";
        return EnviarAsientoResponse.preview(
                built.periodoStr(),
                built.cantidad(),
                built.total(),
                built.payload(),
                msg);
    }

    private BuiltPayload construirPayloadInterno(YearMonth yearMonth) {
        LocalDate inicio = yearMonth.atDay(1);
        LocalDate fin = yearMonth.atEndOfMonth();
        List<SolicitudCheque> solicitudes = solicitudRepository.findParaAsientoContable(
                ESTADO_CHEQUE_GENERADO,
                inicio,
                fin);

        if (solicitudes.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay cheques en estado «Cheque Generado» en el periodo " + yearMonth + ".");
        }

        long cuentaDebito = resolverCuentaDebito(solicitudes);
        long cuentaCredito = resolverCuentaCredito(solicitudes);
        int auxiliarId = properties.getAuxiliarId();

        validarAuxiliarYCuentas(auxiliarId, cuentaDebito, cuentaCredito);

        double total = 0.0;
        for (SolicitudCheque s : solicitudes) {
            double m = s.getMonto() != null ? s.getMonto() : 0.0;
            total += m;
        }

        double totalRedondeado = redondearMonto(total);
        if (totalRedondeado <= 0) {
            throw new IllegalArgumentException("El total del asiento debe ser mayor que cero.");
        }

        List<AsientoDetalleDto> detalles = new ArrayList<>();
        detalles.add(new AsientoDetalleDto(
                new CuentaRefDto(cuentaDebito),
                "Debito",
                totalRedondeado));
        detalles.add(new AsientoDetalleDto(
                new CuentaRefDto(cuentaCredito),
                "Credito",
                totalRedondeado));

        if (detalles.size() < 2) {
            throw new IllegalArgumentException("Se requiere al menos una línea en débito y una en crédito.");
        }

        String descripcion = String.format(
                properties.getDescripcionPlantilla(),
                yearMonth.getYear(),
                yearMonth.getMonthValue());

        AsientoContablePayload payload = new AsientoContablePayload(
                descripcion,
                new AuxiliarRefDto((long) auxiliarId),
                yearMonth.atEndOfMonth().toString(),
                totalRedondeado,
                detalles);

        String periodoStr = String.format("%04d-%02d", yearMonth.getYear(), yearMonth.getMonthValue());
        return new BuiltPayload(payload, periodoStr, solicitudes.size(), totalRedondeado);
    }

    private long resolverCuentaDebito(List<SolicitudCheque> solicitudes) {
        Set<Long> ids = new HashSet<>();
        for (SolicitudCheque s : solicitudes) {
            Long parsed = parseCuentaId(s.getCuentaContableProveedor());
            if (parsed != null) {
                ids.add(parsed);
            }
        }
        if (ids.size() == 1) {
            return ids.iterator().next();
        }
        return properties.getCuentaDebitoDefault();
    }

    private long resolverCuentaCredito(List<SolicitudCheque> solicitudes) {
        Set<Long> ids = new HashSet<>();
        for (SolicitudCheque s : solicitudes) {
            Long parsed = parseCuentaId(s.getCuentaContableBanco());
            if (parsed != null) {
                ids.add(parsed);
            }
        }
        if (ids.size() == 1) {
            return ids.iterator().next();
        }
        return properties.getCuentaCreditoDefault();
    }

    private Long parseCuentaId(String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(raw.trim());
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private void validarAuxiliarYCuentas(int auxiliarId, long cuentaDebito, long cuentaCredito) {
        if (!properties.hasUrl()) {
            return;
        }
        URI base = URI.create(properties.getUrl());
        String prefix = base.getScheme() + "://" + base.getHost() + (base.getPort() > 0 ? ":" + base.getPort() : "");
        try {
            contabilidadRestClient.get().uri(prefix + "/api/auxiliares/" + auxiliarId).retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("El auxiliar " + auxiliarId + " no existe en Contabilidad.");
        }
        try {
            contabilidadRestClient.get().uri(prefix + "/api/cuentas-contables/" + cuentaDebito).retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("La cuenta débito " + cuentaDebito + " no existe en Contabilidad.");
        }
        try {
            contabilidadRestClient.get().uri(prefix + "/api/cuentas-contables/" + cuentaCredito).retrieve().toBodilessEntity();
        } catch (RestClientException e) {
            throw new IllegalArgumentException("La cuenta crédito " + cuentaCredito + " no existe en Contabilidad.");
        }
    }

    private static double redondearMonto(double v) {
        return Math.round(v * 100.0) / 100.0;
    }

    private record BuiltPayload(
            AsientoContablePayload payload,
            String periodoStr,
            int cantidad,
            double total
    ) {
    }
}
