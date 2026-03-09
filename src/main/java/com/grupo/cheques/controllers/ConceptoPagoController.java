package com.grupo.cheques.controllers;

import com.grupo.cheques.models.ConceptoPago;
import com.grupo.cheques.repositories.ConceptoPagoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conceptos")
@CrossOrigin(origins = "*")
public class ConceptoPagoController {

    @Autowired
    private ConceptoPagoRepository conceptoRepository;

    @GetMapping
    public List<ConceptoPago> listarTodos() {
        return conceptoRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody ConceptoPago concepto) {
        // VALIDACIÓN: Evitar descripción duplicada
        if (conceptoRepository.existsByDescripcionIgnoreCase(concepto.getDescripcion())) {
            return ResponseEntity.badRequest().body("Ya existe un Concepto de Pago con esta descripción.");
        }
        return ResponseEntity.ok(conceptoRepository.save(concepto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody ConceptoPago detalles) {
        // VALIDACIÓN: Evitar descripción duplicada en OTRO registro al editar
        if (conceptoRepository.existsByDescripcionIgnoreCaseAndIdNot(detalles.getDescripcion(), id)) {
            return ResponseEntity.badRequest().body("Esta descripción ya está siendo usada por otro concepto.");
        }

        return conceptoRepository.findById(id)
                .map(concepto -> {
                    concepto.setDescripcion(detalles.getDescripcion());
                    concepto.setEstado(detalles.getEstado());
                    return ResponseEntity.ok(conceptoRepository.save(concepto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (conceptoRepository.existsById(id)) {
            conceptoRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}