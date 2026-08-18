package com.compacultivo.service;

import com.compacultivo.config.StripeConfig;
import com.compacultivo.repository.UserRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final StripeConfig stripeConfig;
    private final UserRepository userRepository;
    private final UserService userService;

    // Crea una sesion de Stripe Checkout para el email del usuario logueado.
    public Session crearSesionCheckout(String userEmail) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
                .setCustomerEmail(userEmail)
                .setSuccessUrl(stripeConfig.getSuccessUrl() + "?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(stripeConfig.getCancelUrl())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(stripeConfig.getPriceId())
                                .setQuantity(1L)
                                .build()
                )
                .build();
        return Session.create(params);
    }

    // Llamado desde el webhook de Stripe cuando checkout.session.completed
    // confirma el pago -- activa al usuario automaticamente.
    public void confirmarPagoPorEmail(String email) {
        userRepository.findByEmail(email).ifPresent(user -> userService.activate(user.getId()));
    }
}
