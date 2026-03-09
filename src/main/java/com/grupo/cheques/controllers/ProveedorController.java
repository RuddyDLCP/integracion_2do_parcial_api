package com.grupo.cheques.controllers;

import com.grupo.cheques.models.Proveedor;
import com.grupo.cheques.repositories.ProveedorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
@CrossOrigin(origins = "*")
public class ProveedorController {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @GetMapping
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> guardar(@RequestBody Proveedor proveedor) {
        // VALIDACIONES DE DUPLICADOS
        if (proveedorRepository.existsByIdentificacion(proveedor.getIdentificacion())) {
            return ResponseEntity.badRequest().body("Error: Esta Cédula/RNC ya está registrada en el sistema.");
        }
        if (proveedorRepository.existsByNombreIgnoreCase(proveedor.getNombre())) {
            return ResponseEntity.badRequest().body("Error: Ya existe un proveedor con este Nombre exacto.");
        }
        return ResponseEntity.ok(proveedorRepository.save(proveedor));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Proveedor detalles) {
        // VALIDACIONES AL EDITAR
        if (proveedorRepository.existsByIdentificacionAndIdNot(detalles.getIdentificacion(), id)) {
            return ResponseEntity.badRequest().body("Error: La Cédula/RNC le pertenece a otro proveedor.");
        }
        if (proveedorRepository.existsByNombreIgnoreCaseAndIdNot(detalles.getNombre(), id)) {
            return ResponseEntity.badRequest().body("Error: El Nombre ya le pertenece a otro proveedor.");
        }

        return proveedorRepository.findById(id)
                .map(prov -> {
                    prov.setNombre(detalles.getNombre());
                    prov.setTipoPersona(detalles.getTipoPersona());
                    prov.setIdentificacion(detalles.getIdentificacion());
                    prov.setBalance(detalles.getBalance());
                    prov.setCuentaContable(detalles.getCuentaContable());
                    prov.setEstado(detalles.getEstado());
                    return ResponseEntity.ok(proveedorRepository.save(prov));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (proveedorRepository.existsById(id)) {
            proveedorRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}