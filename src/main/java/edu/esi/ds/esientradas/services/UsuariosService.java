package edu.esi.ds.esientradas.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

// Comunicacion entre backends: valida contra esiusuarios el JWT del comprador y obtiene
// su email. Es el punto que conecta el flujo de compra con la cuenta (mensaje 33).
@Service
public class UsuariosService {

    // URL del backend de usuarios; configurable desde application.properties.
    @Value("${esiusuarios.base-url:http://localhost:8081}")
    private String esiusuariosBaseUrl;

    public String checkToken(String userToken) {
        String endpoint = esiusuariosBaseUrl + "/external/checkToken";
        RestTemplate rest = new RestTemplate();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + userToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = rest.exchange(endpoint, HttpMethod.GET, entity, String.class);
            String email = response.getBody();
            if (email == null || email.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
            }
            return email;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No se pudo validar el token", ex);
        }
    }
}
