package com.grupo.cheques.controllers;

import com.grupo.cheques.models.SolicitudCheque;
import com.grupo.cheques.repositories.SolicitudChequeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/integracion") // Ruta limpia y nueva
@CrossOrigin(origins = "*")
public class IntegracionContableController {

    @Autowired
    private SolicitudChequeRepository solicitudRepository;

    @PostMapping("/simular-asiento")
    public ResponseEntity<?> simularAsiento() {
        // 1. Buscamos solo los cheques que realmente se generaron (se pagaron)
        List<SolicitudCheque> chequesPagados = solicitudRepository.findByEstado("Cheque Generado");

        // 2. Sumamos los montos
        double totalMes = chequesPagados.stream().mapToDouble(SolicitudCheque::getMonto).sum();

        // 3. Preparamos el "Paquete" simulado que verás en pantalla
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Datos listos para enviar por Web Service");
        respuesta.put("cantidadCheques", chequesPagados.size());
        respuesta.put("totalSuma", totalMes);
        respuesta.put("cuentaAfectada", "CXP-BANCO");

        return ResponseEntity.ok(respuesta);
    }
}