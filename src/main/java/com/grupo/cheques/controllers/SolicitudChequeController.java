package com.grupo.cheques.controllers;

import com.grupo.cheques.models.Proveedor;
import com.grupo.cheques.models.SolicitudCheque;
import com.grupo.cheques.repositories.ProveedorRepository;
import com.grupo.cheques.repositories.SolicitudChequeRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/cheques")
@CrossOrigin(origins = "*")
public class SolicitudChequeController {

    @Autowired
    private SolicitudChequeRepository solicitudRepository;

    @Autowired
    private ProveedorRepository proveedorRepository;

    // 1. REGISTRAR SOLICITUD (Nace como Pendiente)
    @PostMapping("/solicitudes")
    public ResponseEntity<?> registrarSolicitud(@RequestBody SolicitudCheque solicitud) {
        Proveedor prov = proveedorRepository.findById(solicitud.getProveedor().getId()).orElse(null);
        if(prov == null) return ResponseEntity.badRequest().body("Proveedor no existe");

        solicitud.setProveedor(prov);
        solicitud.setCuentaContableProveedor(prov.getCuentaContable());
        solicitud.setFechaRegistro(LocalDate.now());
        solicitud.setEstado("Pendiente"); // REGLA DE NEGOCIO PPT

        return ResponseEntity.ok(solicitudRepository.save(solicitud));
    }

    // 2. CONSULTAS Y REPORTES (Filtra por estado si se envía, si no trae todas)
    @GetMapping("/solicitudes")
    public List<SolicitudCheque> consultarSolicitudes(@RequestParam(required = false) String estado) {
        if (estado != null && !estado.isEmpty()) {
            return solicitudRepository.findByEstado(estado);
        }
        return solicitudRepository.findAll();
    }

    // 3. GENERAR O ANULAR CHEQUE (Procesamiento)
    @PostMapping("/procesar")
    public ResponseEntity<?> procesarCheque(@RequestBody ProcesarRequest request) {
        return solicitudRepository.findById(request.getNumeroSolicitud())
                .map(solicitud -> {
                    if (request.getAccion().equals("GENERAR")) {
                        solicitud.setEstado("Cheque Generado");
                        solicitud.setNumeroCheque(request.getNumeroCheque());
                    } else if (request.getAccion().equals("ANULAR")) {
                        solicitud.setEstado("Solicitud Anulada");
                    }
                    return ResponseEntity.ok(solicitudRepository.save(solicitud));
                }).orElse(ResponseEntity.notFound().build());
    }
}

// Clase de apoyo para recibir la decisión del frontend
@Data
class ProcesarRequest {
    private Long numeroSolicitud;
    private String accion; // "GENERAR" o "ANULAR"
    private String numeroCheque;
}