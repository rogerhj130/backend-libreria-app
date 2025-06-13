package com.springboot.backend.andres.usersapp.usersbackend.controllers;

import com.springboot.backend.andres.usersapp.usersbackend.services.VentaService;  // IMPORTA TU SERVICIO DE VENTA

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;

@RestController
@RequestMapping("/api/stripe/webhook")
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Autowired
    private VentaService ventaService;  // INYECCIÓN DEL SERVICIO

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

        switch (event.getType()) {
            case "checkout.session.completed":
                Session session = (Session) event.getDataObjectDeserializer().getObject()
                        .orElseThrow(() -> new RuntimeException("Error deserializando el evento de sesión."));
                System.out.println("Pago completado para la sesión: " + session.getId());

                String ventaId = session.getMetadata().get("ventaId");
                if (ventaId != null) {
                    try {
                        Long idVenta = Long.parseLong(ventaId);
                        ventaService.marcarVentaComoPagada(idVenta);
                        System.out.println("Venta con ID " + ventaId + " marcada como pagada.");
                    } catch (NumberFormatException nfe) {
                        System.err.println("ID de venta inválido en metadata: " + ventaId);
                    } catch (Exception ex) {
                        System.err.println("Error al actualizar estado de venta: " + ex.getMessage());
                    }
                } else {
                    System.err.println("No se encontró el ID de venta en la metadata.");
                }
                break;

            case "payment_intent.succeeded":
                // Maneja otros eventos si es necesario
                break;

            case "payment_intent.payment_failed":
                // Maneja pagos fallidos si es necesario
                break;

            // ... otros eventos que quieras manejar
        }

        return new ResponseEntity<>("Webhook recibido con éxito", HttpStatus.OK);
    }
}