package com.compacultivo;

import com.compacultivo.Entity.*;
import com.compacultivo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

// Crea un ADMIN listo para entrar, un USER en PENDING, y el predio de
// ejemplo "Santa Cecilia" con datos reales del ciclo mar-jul 2026, para
// que el dashboard no arranque vacio. Solo corre si la tabla esta vacia.
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PredioRepository predioRepository;
    private final ResumenActividadRepository resumenRepository;

    @Value("${app.admin-seed-email}")
    private String adminEmail;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setName("Administrador CompaCultivo");
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        admin.setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        userRepository.save(admin);

        User socio = new User();
        socio.setEmail("socio.demo@compacultivo.com");
        socio.setName("Usuario de prueba (pago pendiente)");
        socio.setRole(Role.USER);
        socio.setActive(false);
        socio.setSubscriptionStatus(SubscriptionStatus.PENDING);
        userRepository.save(socio);

        Predio predio = new Predio();
        predio.setOwner(admin);
        predio.setNombre("Santa Cecilia");
        predio.setUbicacion("Vereda Santa Cecilia");
        predio.setHectareas(new BigDecimal("8"));
        predio.setFechaInicioCiclo(LocalDate.of(2026, 3, 25));
        predio.setFechaEstimadaCosecha(LocalDate.of(2026, 11, 1));
        predio.setPorcentajeSocio(50);
        predioRepository.save(predio);

        Object[][] movimientos = {
            {"2026-03-25", "Inversión", "SEMILLA · MIA", 360000},
            {"2026-05-15", "Inversión", "ANÁLISIS DE SUELOS · Sta. Cecilia", 250000},
            {"2026-05-16", "Inversión", "CAL VIVA · Compra", 646000},
            {"2026-06-15", "Siembra", "RETOVO · Preparación de tierra", 640000},
            {"2026-06-18", "Nómina", "Cuadrilla · Contrato", 900000},
            {"2026-06-19", "Insumos", "TRICHODERMA · Biológico", 93750},
            {"2026-07-08", "Siembra", "ABONO · Abono siembra", 2190000},
        };

        for (Object[] m : movimientos) {
            BigDecimal total = new BigDecimal((Integer) m[3]);
            BigDecimal aportePropio = total.multiply(BigDecimal.valueOf(predio.getPorcentajeSocio()))
                    .divide(BigDecimal.valueOf(100));
            ResumenActividad r = new ResumenActividad();
            r.setPredio(predio);
            r.setFecha(LocalDate.parse((String) m[0]));
            r.setCategoria((String) m[1]);
            r.setDescripcion((String) m[2]);
            r.setTotalGeneral(total);
            r.setAportePropio(aportePropio);
            r.setAporteSocio(total.subtract(aportePropio));
            resumenRepository.save(r);
        }
    }
}
