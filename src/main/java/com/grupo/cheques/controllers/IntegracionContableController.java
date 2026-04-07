package com.grupo.cheques.controllers;

import com.grupo.cheques.services.IntegracionContabilidadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/integracion")
@CrossOrigin(origins = "*")
public class IntegracionContableController {

    @Autowired
    private IntegracionContabilidadService integracionContabilidadService;

    /**
     * Construye el asiento del periodo y lo envía a Contabilidad si {@code integracion.contabilidad.url} está definida;
     * si no, devuelve la misma vista previa que la simulación.
     */
    @PostMapping("/enviar-asiento")
    public ResponseEntity<?> enviarAsiento(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        try {
            YearMonth periodo = resolverPeriodo(mes, anio);
            return ResponseEntity.ok(integracionContabilidadService.enviarAsiento(periodo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Solo vista previa del asiento (no llama al servicio externo).
     */
    @PostMapping("/simular-asiento")
    public ResponseEntity<?> simularAsiento(
            @RequestParam(required = false) Integer mes,
            @RequestParam(required = false) Integer anio) {
        try {
            YearMonth periodo = resolverPeriodo(mes, anio);
            return ResponseEntity.ok(integracionContabilidadService.soloVistaPrevia(periodo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private static YearMonth resolverPeriodo(Integer mes, Integer anio) {
        if (mes == null && anio == null) {
            return YearMonth.now();
        }
        if (mes == null || anio == null) {
            throw new IllegalArgumentException("Debe enviar ambos parámetros mes y anio, o ninguno (periodo actual).");
        }
        if (mes < 1 || mes > 12) {
            throw new IllegalArgumentException("El mes debe estar entre 1 y 12.");
        }
        if (anio < 2000 || anio > 2100) {
            throw new IllegalArgumentException("El año no es válido.");
        }
        return YearMonth.of(anio, mes);
    }
}
