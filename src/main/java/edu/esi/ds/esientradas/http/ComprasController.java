package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.services.ColaService;
import edu.esi.ds.esientradas.services.EmailService;
import edu.esi.ds.esientradas.services.PDFService;
import edu.esi.ds.esientradas.services.ReservasService;
import edu.esi.ds.esientradas.services.UsuariosService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

// Cierre de la compra (mensaje 33 del escenario): valida el JWT del usuario contra
// esiusuarios, confirma las entradas prerreservadas, genera el PDF y lo envia por correo.
@Slf4j
@RestController
@RequestMapping("/compras")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ComprasController {

    private final UsuariosService usuariosService;
    private final ReservasService reservasService;
    private final PDFService pdfService;
    private final EmailService emailService;
    private final ColaService colaService;

    public ComprasController(UsuariosService usuariosService, ReservasService reservasService,
                             PDFService pdfService, EmailService emailService, ColaService colaService) {
        this.usuariosService = usuariosService;
        this.reservasService = reservasService;
        this.pdfService = pdfService;
        this.emailService = emailService;
        this.colaService = colaService;
    }

    @PutMapping("/comprar")
    public ResponseEntityWrapper comprar(HttpSession session,
                                         @RequestParam(required = false) String userToken,
                                         @RequestParam(required = false) String tokenReserva) {

        if (userToken == null || userToken.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesion para finalizar la compra.");
        }

        // Comunicacion entre backends: obtiene el email del comprador a partir del JWT.
        String emailDestino = usuariosService.checkToken(userToken);
        log.info("Compra iniciada por {}", emailDestino);

        // El identificador de grupo es el token de reserva si llego; si no, el id de sesion.
        String sessionIdLogico = (tokenReserva != null && !tokenReserva.isBlank())
                ? tokenReserva
                : session.getId();

        List<Entrada> entradas = reservasService.confirmarCompra(sessionIdLogico);
        if (entradas.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay entradas reservadas o el tiempo ha expirado.");
        }

        byte[] pdfBytes = pdfService.generarPdfEntradas(entradas);
        emailService.enviarEntradasConAdjunto(emailDestino, pdfBytes);
        session.removeAttribute("precioTotal");

        // Libera el turno de la cola del espectaculo comprado.
        Long idEspectaculo = entradas.get(0).getEspectaculo().getId();
        colaService.finalizarTurno(idEspectaculo, "user:" + emailDestino);

        return new ResponseEntityWrapper("Compra completada. Entradas enviadas a " + emailDestino, emailDestino, entradas.size());
    }

    // Pequeno DTO de respuesta para que el front confirme la compra.
    public record ResponseEntityWrapper(String mensaje, String email, int numeroEntradas) {
    }
}
