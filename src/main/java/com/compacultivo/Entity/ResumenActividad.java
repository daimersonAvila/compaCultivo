package com.compacultivo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

// Fila consolidada del "conglomerado": una linea por movimiento, ya repartida
// entre dueno y socio segun el porcentajeSocio del predio en ese momento.
@Entity
@Table(name = "resumen_actividad")
@Getter
@Setter
@NoArgsConstructor
public class ResumenActividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "predio_id")
    private Predio predio;

    private LocalDate fecha;
    private String categoria;
    private String descripcion;
    private BigDecimal totalGeneral;
    private BigDecimal aportePropio;
    private BigDecimal aporteSocio;
}
