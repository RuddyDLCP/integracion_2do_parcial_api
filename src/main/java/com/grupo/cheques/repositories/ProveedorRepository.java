package com.grupo.cheques.repositories;

import com.grupo.cheques.models.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByIdentificacion(String identificacion);
    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByIdentificacionAndIdNot(String identificacion, Long id);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}