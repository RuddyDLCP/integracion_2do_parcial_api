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
            solicitud.setNumeroCheque(numeroChequeGenerado);
            solicitud.setEstado("Cheque Generado");
        } else if ("ANULAR".equalsIgnoreCase(accion)) {
            // Cambia estado a Solicitud Anulada [cite: 38]
            solicitud.setEstado("Solicitud Anulada");
        }

        return solicitudRepository.save(solicitud);
    }
}