package com.compacultivo.repository;

import com.compacultivo.Entity.CostoInversion;
import com.compacultivo.Entity.Predio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CostoInversionRepository extends JpaRepository<CostoInversion, Long> {
    List<CostoInversion> findByPredioOrderByFechaDesc(Predio predio);
}
