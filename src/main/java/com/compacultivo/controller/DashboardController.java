package com.compacultivo.controller;

import com.compacultivo.Entity.Predio;
import com.compacultivo.Entity.ResumenActividad;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.UserRepository;
import com.compacultivo.service.PredioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserRepository userRepository;
    private final PredioService predioService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal OAuth2User principal, Model model) {
        User user = currentUser(principal);
        List<Predio> predios = predioService.findByOwner(user);
        Predio predio = predios.isEmpty() ? null : predios.get(0);

        model.addAttribute("predio", predio);
        if (predio != null) {
            List<ResumenActividad> movimientos = predioService.movimientos(predio);
            model.addAttribute("movimientos", movimientos);
            model.addAttribute("totalInvertido", predioService.totalInvertido(predio));
            model.addAttribute("aportePropio", movimientos.stream()
                    .map(ResumenActividad::getAportePropio).reduce(BigDecimal.ZERO, BigDecimal::add));
            model.addAttribute("aporteSocio", movimientos.stream()
                    .map(ResumenActividad::getAporteSocio).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return "dashboard";
    }

    // HTMX llama este endpoint cuando se mueve el slider de % socio, y
    // reemplaza solo el fragmento de la tabla + KPIs -- sin recargar la
    // pagina completa.
    @PostMapping("/dashboard/porcentaje")
    public String actualizarPorcentaje(@AuthenticationPrincipal OAuth2User principal,
                                        @RequestParam Long predioId,
                                        @RequestParam int porcentaje,
                                        Model model) {
        User user = currentUser(principal);
        Predio predio = predioService.findByOwner(user).stream()
                .filter(p -> p.getId().equals(predioId))
                .findFirst()
                .orElseThrow();

        predioService.actualizarPorcentajeSocio(predio, porcentaje);
        List<ResumenActividad> movimientos = predioService.movimientos(predio);

        model.addAttribute("predio", predio);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("totalInvertido", predioService.totalInvertido(predio));
        model.addAttribute("aportePropio", movimientos.stream()
                .map(ResumenActividad::getAportePropio).reduce(BigDecimal.ZERO, BigDecimal::add));
        model.addAttribute("aporteSocio", movimientos.stream()
                .map(ResumenActividad::getAporteSocio).reduce(BigDecimal.ZERO, BigDecimal::add));
        return "fragments/costo-table :: panel";
    }

    private User currentUser(OAuth2User principal) {
        String email = principal.getAttribute("email");
        return userRepository.findByEmail(email).orElseThrow();
    }
}
