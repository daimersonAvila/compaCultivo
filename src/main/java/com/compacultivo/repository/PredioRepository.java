package com.compacultivo.repository;

import com.compacultivo.Entity.Predio;
import com.compacultivo.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredioRepository extends JpaRepository<Predio, Long> {
    List<Predio> findByOwner(User owner);
}
