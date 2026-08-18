package com.compacultivo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "costo_siembra")
@Getter
@Setter
@NoArgsConstructor
public class CostoSiembra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predio_id")
    private Predio predio;

    private LocalDate fecha;
    private String nombre;
    private String descripcion;
    private BigDecimal cantidad;
    private BigDecimal valorUnitario;
    private BigDecimal total;
}
