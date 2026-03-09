package com.grupo.cheques.repositories;

import com.grupo.cheques.models.SolicitudCheque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SolicitudChequeRepository extends JpaRepository<SolicitudCheque, Long> {

    // Para buscar solo las solicitudes "Pendientes" o "Cheque Generado"
    List<SolicitudCheque> findByEstado(String estado);
}