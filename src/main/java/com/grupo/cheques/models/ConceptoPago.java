package com.grupo.cheques.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "conceptos_pago")
@Data // Anotación de Lombok para Getters/Setters
public class ConceptoPago {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Identificador [cite: 16]
    
    @Column(nullable = false, length = 100)
    private String descripcion; // Descripción [cite: 17]
    
    @Column(nullable = false, length = 20)
    private String estado; // Estado [cite: 18]
}