package com.compacultivo.security;

import com.compacultivo.Entity.SubscriptionStatus;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Protege /dashboard, /predios y /admin: si el usuario esta autenticado
// pero no esta activo (isActive=false o subscriptionStatus != ACTIVE), lo
// manda a /pago en vez de dejarlo pasar. /admin ademas exige rol ADMIN
// (esto ultimo tambien se aplica en SecurityConfig como segunda capa).
@Component
@RequiredArgsConstructor
public class SubscriptionAccessFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private static final String[] PROTECTED_PREFIXES = {"/dashboard", "/predios", "/admin"};

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isProtected = false;
        for (String prefix : PROTECTED_PREFIXES) {
            if (path.startsWith(prefix)) {
                isProtected = true;
                break;
            }
        }

        if (!isProtected) {
            chain.doFilter(request, response);
            return;
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof OAuth2User oAuth2User) {
            String email = oAuth2User.getAttribute("email");
            User user = userRepository.findByEmail(email).orElse(null);
            boolean allowed = user != null && user.isActive() && user.getSubscriptionStatus() == SubscriptionStatus.ACTIVE;
            if (!allowed) {
                response.sendRedirect("/pago");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
