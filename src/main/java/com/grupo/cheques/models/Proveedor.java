package com.grupo.cheques.models;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(name = "proveedores")
@Data
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private String nombre;
    
    @Column(nullable = false, length = 20)
    private String tipoPersona;
    
    @Column(nullable = false, length = 20, unique = true)
    private String identificacion;
    
    @Column(precision = 18, scale = 2)
    private BigDecimal balance;
    
    @Column(length = 50)
    private String cuentaContable;
    
    @Column(nullable = false, length = 20)
    private String estado;
}