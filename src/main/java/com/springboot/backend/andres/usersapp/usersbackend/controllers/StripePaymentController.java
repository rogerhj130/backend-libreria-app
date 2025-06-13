package com.springboot.backend.andres.usersapp.usersbackend.controllers;

import com.springboot.backend.andres.usersapp.usersbackend.services.StripeService;
import com.stripe.exception.StripeException;
import com.stripe.exception.InvalidRequestException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//import static org.mockito.Mockito.description;

import java.util.HashMap;
import java.util.Map;

@RestController
//@CrossOrigin(origins = "http://localhost:4200") 
@RequestMapping("/api/stripe")
public class StripePaymentController {

    @Autowired
    private StripeService stripeService;

    public static class PaymentRequest {
        private Double total;
        private String currency;
        private String description;
        private Map<String, String> metadata;

        public Double getTotal() { return total; }
        public void setTotal(Double total) { this.total = total; }
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public Map<String, String> getMetadata() { return metadata; }
        public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    }

    @PostMapping("/create-checkout-session")
    public ResponseEntity<Map<String, String>> createCheckoutSession(@RequestBody PaymentRequest paymentRequest) {
        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("Recibida solicitud de pago de Angular:");
            System.out.println("  Total recibido: " + paymentRequest.getTotal());
            System.out.println("  Moneda recibida: " + paymentRequest.getCurrency());
            System.out.println("  Descripción: " + paymentRequest.getDescription());

            // --- INICIO DE LA CORRECCIÓN CLAVE ---
            // Primero, asegúrate de que el total no sea nulo antes de intentar la conversión.
            if (paymentRequest.getTotal() == null) {
                System.err.println("Error de validación: El monto total es nulo.");
                response.put("error", "Monto de pago inválido. Debe ser un número.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            long amountInCents; // <-- Declara como 'long'

            // Lógica de conversión de moneda BOB a USD y luego a centavos (Long)
            // Asegúrate de que 'paymentRequest.getCurrency()' no sea nulo ni vacío
            String inputCurrency = paymentRequest.getCurrency() != null ? paymentRequest.getCurrency().toLowerCase() : "";

            if ("bob".equals(inputCurrency)) {
                final double TIPO_DE_CAMBIO_BOB_A_USD = 6.96; // Tipo de cambio fijo
                double totalEnUsd = paymentRequest.getTotal() / TIPO_DE_CAMBIO_BOB_A_USD;
                amountInCents = Math.round(totalEnUsd * 100); // Redondea y convierte a long
                System.out.println("  Convertido de " + paymentRequest.getTotal() + " BOB a " + amountInCents + " centavos USD.");
            } else {
                // Si la moneda no es BOB, asume que ya es la moneda de Stripe (ej. USD) y solo convierte a centavos
                amountInCents = Math.round(paymentRequest.getTotal() * 100);
                System.out.println("  Asumiendo moneda Stripe, convertido a " + amountInCents + " centavos.");
            }
            // --- FIN DE LA CORRECCIÓN CLAVE ---


            // Validaciones
            if (amountInCents <= 0) { // Ahora validamos el 'long'
                System.err.println("Error de validación: El monto convertido es cero/negativo.");
                response.put("error", "Monto de pago inválido. Debe ser mayor a cero.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            if (inputCurrency.isEmpty()) { // Validamos la moneda original
                System.err.println("Error de validación: Moneda no especificada.");
                response.put("error", "Moneda no especificada.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }


           // Map<String, String> metadata = new HashMap<>();
            // metadata.put("internal_venta_id", "ID_GENERADO_POR_TU_DB");
            Map<String, String> metadata = paymentRequest.getMetadata() != null ?
             paymentRequest.getMetadata() : new HashMap<>();
             

            String stripeCurrency = "usd"; // La moneda que tu cuenta de Stripe usa (la de liquidación)
            String description = paymentRequest.getDescription();
            if (description == null || description.trim().isEmpty()) {
                description = "Compra de Materiales"; // Valor por defecto si la descripción está vacía
                System.out.println("  AVISO: Descripción de producto estaba vacía. Usando valor por defecto: " + description);
            }


            String checkoutUrl = stripeService.createCheckoutSession(
                amountInCents, // ¡Aquí pasamos el 'long' correctamente!
                description,
                stripeCurrency, // Siempre 'usd' para Stripe en tu configuración actual
                metadata
            );

            response.put("url", checkoutUrl);
            System.out.println("URL de Checkout de Stripe generada: " + checkoutUrl);
            return ResponseEntity.ok(response);

        } catch (InvalidRequestException e) {
            System.err.println("Stripe InvalidRequestException: " + e.getStripeError().getMessage());
            e.printStackTrace();
            response.put("error", "Error en los datos de la solicitud a Stripe: " + e.getStripeError().getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (StripeException e) {
            System.err.println("Error de la API de Stripe: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Error de comunicación con Stripe: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } catch (Exception e) {
            System.err.println("Error inesperado en create-checkout-session: " + e.getMessage());
            e.printStackTrace();
            response.put("error", "Error interno del servidor. Por favor, inténtalo de nuevo.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}