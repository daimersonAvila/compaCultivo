package com.compacultivo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "predio")
@Getter
@Setter
@NoArgsConstructor
public class Predio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User owner;

    private String nombre;
    private String ubicacion;
    private BigDecimal hectareas;

    private LocalDate fechaInicioCiclo;
    private LocalDate fechaEstimadaCosecha;

    @Column(nullable = false)
    private Integer porcentajeSocio = 50;

    private String estado = "ACTIVO";
}
