package com.compacultivo.repository;

import com.compacultivo.Entity.Nomina;
import com.compacultivo.Entity.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NominaRepository extends JpaRepository<Nomina, Long> {
    List<Nomina> findByPredioOrderByFechaDesc(Predio predio);
}
