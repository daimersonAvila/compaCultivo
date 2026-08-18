package com.compacultivo.service;

import com.compacultivo.Entity.Predio;
import com.compacultivo.Entity.ResumenActividad;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.PredioRepository;
import com.compacultivo.repository.ResumenActividadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PredioService {

    private final PredioRepository predioRepository;
    private final ResumenActividadRepository resumenRepository;

    public List<Predio> findByOwner(User owner) {
        return predioRepository.findByOwner(owner);
    }

    public List<ResumenActividad> movimientos(Predio predio) {
        return resumenRepository.findByPredioOrderByFechaAsc(predio);
    }

    // Cambia el % del socio y recalcula el reparto de TODOS los movimientos
    // ya registrados para ese predio. Esto es lo que dispara el HTMX
    // "actualizar porcentaje" sin recargar la pagina.
    public Predio actualizarPorcentajeSocio(Predio predio, int nuevoPorcentaje) {
        predio.setPorcentajeSocio(nuevoPorcentaje);
        Predio guardado = predioRepository.save(predio);

        for (ResumenActividad r : resumenRepository.findByPredioOrderByFechaAsc(predio)) {
            BigDecimal aportePropio = r.getTotalGeneral()
                    .multiply(BigDecimal.valueOf(nuevoPorcentaje))
                    .divide(BigDecimal.valueOf(100));
            r.setAportePropio(aportePropio);
            r.setAporteSocio(r.getTotalGeneral().subtract(aportePropio));
            resumenRepository.save(r);
        }
        return guardado;
    }

    public BigDecimal totalInvertido(Predio predio) {
        return resumenRepository.findByPredioOrderByFechaAsc(predio).stream()
                .map(ResumenActividad::getTotalGeneral)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
