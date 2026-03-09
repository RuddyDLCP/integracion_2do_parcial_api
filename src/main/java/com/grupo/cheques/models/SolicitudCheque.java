package com.grupo.cheques.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Table(name = "solicitudes_cheques")
@Data
public class SolicitudCheque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long numeroSolicitud; // Identificador / Número de Solicitud

    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor; // Relación con el proveedor

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false, length = 30)
    private String estado; // "Pendiente", "Solicitud Anulada", "Cheque Generado"

    private String cuentaContableProveedor;
    private String cuentaContableBanco;

    private String numeroCheque; // Se llena cuando se aprueba
}