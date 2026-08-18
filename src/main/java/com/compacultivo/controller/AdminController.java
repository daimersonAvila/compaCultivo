package com.compacultivo.controller;

import com.compacultivo.Entity.Role;
import com.compacultivo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

// Todo lo que hay aqui exige ROLE_ADMIN -- ademas de la restriccion global
// en SecurityConfig sobre /admin/**, se repite aqui como segunda capa
// (defensa en profundidad) por si alguna ruta se registra fuera de ese prefijo.
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    @GetMapping
    public String admin(Model model) {
        model.addAttribute("usuarios", userService.findAll());
        return "admin";
    }

    @PostMapping("/usuarios/{id}/activar")
    public String activar(@PathVariable Long id, Model model) {
        userService.activate(id);
        model.addAttribute("usuarios", userService.findAll());
        return "fragments/admin-table :: tabla";
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivar(@PathVariable Long id, Model model) {
        userService.deactivate(id);
        model.addAttribute("usuarios", userService.findAll());
        return "fragments/admin-table :: tabla";
    }

    @PostMapping("/usuarios/{id}/rol")
    public String cambiarRol(@PathVariable Long id, @RequestParam Role rol, Model model) {
        userService.changeRole(id, rol);
        model.addAttribute("usuarios", userService.findAll());
        return "fragments/admin-table :: tabla";
    }
}
