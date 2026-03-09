package com.grupo.cheques.repositories;

import com.grupo.cheques.models.ConceptoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoPagoRepository extends JpaRepository<ConceptoPago, Long> {
    boolean existsByDescripcionIgnoreCase(String descripcion);

    boolean existsByDescripcionIgnoreCaseAndIdNot(String descripcion, Long id);
}