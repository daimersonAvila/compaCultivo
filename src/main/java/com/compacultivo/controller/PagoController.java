package com.compacultivo.controller;

import com.compacultivo.service.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class PagoController {

    private final PaymentService paymentService;

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    @GetMapping("/pago")
    public String pago() {
        return "pago";
    }

    @GetMapping("/pago/exito")
    public String exito(Model model) {
        model.addAttribute("mensaje", "Pago recibido. Un momento mientras se confirma tu acceso.");
        return "pago";
    }

    @PostMapping("/pago/checkout")
    public String iniciarCheckout(@AuthenticationPrincipal OAuth2User principal) throws StripeException {
        Session session = paymentService.crearSesionCheckout(principal.getAttribute("email"));
        return "redirect:" + session.getUrl();
    }

    // Endpoint publico que llama Stripe directamente (no pasa por login).
    // Verifica la firma con el webhook secret antes de confiar en el payload.
    @PostMapping("/pago/webhook")
    @ResponseBody
    public ResponseEntity<String> webhook(@RequestBody String payload,
                                           @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            Event event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
            if ("checkout.session.completed".equals(event.getType())) {
                event.getDataObjectDeserializer().getObject().ifPresent(obj -> {
                    Session session = (Session) obj;
                    String email = session.getCustomerDetails() != null
                            ? session.getCustomerDetails().getEmail()
                            : session.getCustomerEmail();
                    if (email != null) {
                        paymentService.confirmarPagoPorEmail(email);
                    }
                });
            }
            return ResponseEntity.ok("ok");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("firma invalida");
        }
    }
}
