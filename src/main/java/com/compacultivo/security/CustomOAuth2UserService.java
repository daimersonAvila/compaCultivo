package com.compacultivo.security;

import com.compacultivo.Entity.Role;
import com.compacultivo.Entity.SubscriptionStatus;
import com.compacultivo.Entity.User;
import com.compacultivo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;

// Se ejecuta cada vez que alguien termina el flujo de login de Google.
// Crea el usuario la primera vez (isActive=false, PENDING) o simplemente
// lo recupera si ya existe -- nunca confia en datos de sesion para el estado
// de pago, siempre relee de la base de datos.
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(request);

        String googleId = oAuth2User.getAttribute("sub");
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user = userRepository.findByGoogleId(googleId)
                .orElseGet(() -> userRepository.findByEmail(email).orElseGet(User::new));

        boolean isNew = user.getId() == null;
        user.setGoogleId(googleId);
        user.setEmail(email);
        user.setName(name);
        if (isNew) {
            user.setRole(Role.USER);
            user.setActive(false);
            user.setSubscriptionStatus(SubscriptionStatus.PENDING);
        }
        userRepository.save(user);

        // Las autoridades se derivan SIEMPRE de lo que hay en la base de
        // datos, nunca del token de Google -- asi un usuario no puede
        // "auto-promoverse" a ADMIN reautenticandose.
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new DefaultOAuth2User(authorities, oAuth2User.getAttributes(), "email");
    }
}
