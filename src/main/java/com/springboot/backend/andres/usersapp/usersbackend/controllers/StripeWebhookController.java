package com.springboot.backend.andres.usersapp.usersbackend.controllers;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session; 
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @PostMapping
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload,
                                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (Exception e) {
            System.err.println("Error al verificar la firma del webhook: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Firma del webhook inválida");
        }

        // Procesa el evento según su tipo
        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new RuntimeException("Error deserializando el evento de sesión."));
                System.out.println("Pago completado para la sesión: " + session.getId());
                // AQUÍ: Llama a tu servicio para actualizar el estado de la orden en tu DB
                // session.getMetadata() podría tener el ID de tu orden interna
                break;
            case "payment_intent.succeeded":
                // Si usas Payment Intents directamente, también puedes manejarlo aquí
                // PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().get();
                // System.out.println("PaymentIntent exitoso: " + paymentIntent.getId());
                break;
            case "payment_intent.payment_failed":
                // Maneja pagos fallidos
                // System.out.println("PaymentIntent fallido.");
                break;
            // ... otros eventos que quieras manejar
        }

        return new ResponseEntity<>("Webhook recibido con éxito", HttpStatus.OK);
    }
}