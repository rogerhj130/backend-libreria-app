package com.springboot.backend.andres.usersapp.usersbackend.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @Value("${stripe.success.url}")
    private String successUrl;

    @Value("${stripe.cancel.url}")
    private String cancelUrl;

    /**
     * Crea una sesión de Checkout de Stripe.
     * @param amount El monto total a pagar (en centavos/unidades más pequeñas, ej. 1000 para \\$10.00).
     * @param productName El nombre del producto o una descripción de la compra.
     * @param currency La moneda en la que se realizará el pago (ej. "usd", "pen").
     * @param metadata Opcional: un mapa de datos que se adjuntarán a la sesión, útil para ID de orden internos.
     * @return La URL a la que el cliente debe ser redirigido para completar el pago.
     * @throws StripeException Si ocurre un error con la API de Stripe.
     */
    public String createCheckoutSession(Long amount, String productName, String currency, Map<String, String> metadata) throws StripeException {
        // Establece tu clave secreta de Stripe para esta solicitud
        Stripe.apiKey = stripeSecretKey;

        // --- Inicio de depuración: Muestra los valores recibidos antes de llamar a Stripe ---
        System.out.println("\n--- Iniciando creación de Checkout Session en StripeService ---");
        System.out.println("  Clave Secreta utilizada (primeros 5 caracteres): " + stripeSecretKey.substring(0, Math.min(stripeSecretKey.length(), 5)) + "...");
        System.out.println("  Monto recibido (en unidades pequeñas): " + amount);
        System.out.println("  Nombre de Producto: " + productName);
        System.out.println("  Moneda: " + currency);
        System.out.println("  URL de Éxito: " + successUrl);
        System.out.println("  URL de Cancelación: " + cancelUrl);
        System.out.println("  Metadatos: " + (metadata != null ? metadata.toString() : "Ninguno"));
        // --- Fin de depuración ---

        SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl);

        // Añade los ítems de la línea (un solo ítem para el total de la compra)
        paramsBuilder.addLineItem(
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L) // Siempre 1 porque es el total de la compra
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency(currency)
                                        .setUnitAmount(amount) // Este monto debe ser un entero > 0 (ej. 50 para \$0.50 USD)
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(productName)
                                                        .build())
                                        .build())
                        .build());

        // Adjunta metadata si es proporcionada
        if (metadata != null && !metadata.isEmpty()) {
            paramsBuilder.putAllMetadata(metadata);
        }

        try {
            Session session = Session.create(paramsBuilder.build());
            System.out.println("--- Checkout Session creada exitosamente en Stripe ---");
            System.out.println("  ID de Sesión de Stripe: " + session.getId());
            System.out.println("  URL de redirección: " + session.getUrl());
            return session.getUrl(); // Retorna la URL de la sesión de Stripe
        } catch (StripeException e) {
            System.err.println("--- ERROR al crear Checkout Session en Stripe ---");
            System.err.println("  Tipo de error: " + e.getClass().getSimpleName());
            System.err.println("  Mensaje de Stripe: " + e.getStripeError().getMessage());
            System.err.println("  Código de error de Stripe: " + e.getStripeError().getCode());
            System.err.println("  Param de error de Stripe: " + e.getStripeError().getParam());
            throw e; // Relanza la excepción para que el controlador la capture
        }
    }
}