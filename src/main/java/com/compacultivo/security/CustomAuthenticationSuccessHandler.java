package com.compacultivo.security;

import com.compacultivo.Entity.SubscriptionStatus;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Decide a donde redirigir justo despues del login segun el estado real
// guardado en la base de datos -- nunca segun lo que diga el token de Google.
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {
        OAuth2User principal = (OAuth2User) authentication.getPrincipal();
        String email = principal.getAttribute("email");
        User user = userRepository.findByEmail(email).orElse(null);

        String target = "/pago";
        if (user != null && user.isActive() && user.getSubscriptionStatus() == SubscriptionStatus.ACTIVE) {
            target = "/dashboard";
        }
        getRedirectStrategy().sendRedirect(request, response, target);
    }
}
