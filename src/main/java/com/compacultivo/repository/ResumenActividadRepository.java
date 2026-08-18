package com.compacultivo.repository;

import com.compacultivo.Entity.Predio;
import com.compacultivo.Entity.ResumenActividad;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumenActividadRepository extends JpaRepository<ResumenActividad, Long> {
    List<ResumenActividad> findByPredioOrderByFechaAsc(Predio predio);
}
