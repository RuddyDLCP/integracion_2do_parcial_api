package com.grupo.cheques.repositories;

import com.grupo.cheques.models.SolicitudCheque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SolicitudChequeRepository extends JpaRepository<SolicitudCheque, Long> {

    List<SolicitudCheque> findByEstado(String estado);

    List<SolicitudCheque> findByEstadoAndFechaRegistroBetween(
            String estado,
            LocalDate fechaInicio,
            LocalDate fechaFin);

    @Query("SELECT DISTINCT s FROM SolicitudCheque s JOIN FETCH s.proveedor WHERE s.estado = :estado "
            + "AND s.fechaRegistro BETWEEN :inicio AND :fin")
    List<SolicitudCheque> findParaAsientoContable(
            @Param("estado") String estado,
            @Param("inicio") LocalDate inicio,
            @Param("fin") LocalDate fin);
}