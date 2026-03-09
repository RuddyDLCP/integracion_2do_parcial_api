package com.grupo.cheques.services;

import com.grupo.cheques.models.SolicitudCheque;
import com.grupo.cheques.repositories.SolicitudChequeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChequeService {

    @Autowired
    private SolicitudChequeRepository solicitudRepository;

    @Transactional
    public SolicitudCheque procesarSolicitud(Long numeroSolicitud, String accion, String numeroChequeGenerado) {
        SolicitudCheque solicitud = solicitudRepository.findById(numeroSolicitud)
            .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        if ("GENERAR".equalsIgnoreCase(accion)) {
            // Se agrega al registro el número de cheque y cambia estado [cite: 37]
            solicitud.setNumeroCheque(numeroChequeGenerado);
            solicitud.setEstado("Cheque Generado");
        } else if ("ANULAR".equalsIgnoreCase(accion)) {
            // Cambia estado a Solicitud Anulada [cite: 38]
            solicitud.setEstado("Solicitud Anulada");
        }

        return solicitudRepository.save(solicitud);
    }

    // Registro de Asiento Contable [cite: 39]
    public void enviarAsientoContabilidad(int mes, int anio) {
        // 1. Obtener todas las solicitudes "Cheque Generado" del mes
        // 2. Agrupar y sumar los montos por cuenta contable [cite: 40]
        
        // 3. Integración: Llamar al Servicio Web expuesto por Contabilidad 
        // (Aquí utilizaremos RestTemplate o WebClient para el cliente web)
    }
}