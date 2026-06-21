package edu.esi.ds.esientradas.http;

import com.stripe.exception.StripeException;
import edu.esi.ds.esientradas.services.PagosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

// Prepara el pago en la pasarela externa y devuelve el clientSecret al frontend.
@Slf4j
@RestController
@RequestMapping("/pago")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class PagosController {

    private final PagosService service;

    public PagosController(PagosService service) {
        this.service = service;
    }

    @PostMapping("/prepararPago")
    public Map<String, String> prepararPago(@RequestBody Map<String, Object> infoPago) {
        if (!infoPago.containsKey("centimos")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Falta el importe en centimos");
        }

        Long centimos = ((Number) infoPago.get("centimos")).longValue();
        try {
            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", service.prepararPago(centimos));
            return response;
        } catch (StripeException e) {
            log.error("Error al comunicar con la pasarela de pago", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al preparar el pago");
        }
    }
}
