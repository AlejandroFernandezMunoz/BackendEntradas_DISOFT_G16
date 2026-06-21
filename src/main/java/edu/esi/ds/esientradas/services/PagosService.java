package edu.esi.ds.esientradas.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

// Pasarela de pago externa (Stripe). Crea un PaymentIntent y devuelve el clientSecret
// que el frontend usa para confirmar la tarjeta. La clave se inyecta por configuracion.
@Slf4j
@Service
public class PagosService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    public String prepararPago(Long centimos) throws StripeException {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setCurrency("eur")
                .setAmount(centimos)
                .addPaymentMethodType("card")
                .build();

        PaymentIntent intent = PaymentIntent.create(params);
        String clientSecret = new JSONObject(intent.toJson()).getString("client_secret");
        log.info("Client secret de pago generado correctamente.");
        return clientSecret;
    }
}
