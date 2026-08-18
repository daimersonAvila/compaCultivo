package com.compacultivo.repository;

import com.compacultivo.Entity.CostoSiembra;
import com.compacultivo.Entity.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostoSiembraRepository extends JpaRepository<CostoSiembra, Long> {
    List<CostoSiembra> findByPredioOrderByFechaDesc(Predio predio);
}
