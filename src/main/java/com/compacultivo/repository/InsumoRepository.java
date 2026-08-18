package com.compacultivo.repository;

import com.compacultivo.Entity.Insumo;
import com.compacultivo.Entity.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByPredioOrderByFechaDesc(Predio predio);
}
