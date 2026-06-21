package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.dao.EspectaculoDao;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.services.ColaService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// API de la cola virtual: entrar, consultar posicion, salir y conservar el turno al
// pasar por el login. La apertura de taquilla se controla con aperturaTaquilla del evento.
@RestController
@RequestMapping("/cola")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ColaController {

    private static final Logger log = LoggerFactory.getLogger(ColaController.class);

    private final ColaService colaService;
    private final EspectaculoDao espectaculoDao;

    public ColaController(ColaService colaService, EspectaculoDao espectaculoDao) {
        this.colaService = colaService;
        this.espectaculoDao = espectaculoDao;
    }

    @PostMapping("/entrar")
    public ResponseEntity<Map<String, Object>> entrarCola(@RequestParam Long espectaculoId, HttpSession session) {
        Espectaculo esp = espectaculoDao.findById(espectaculoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espectaculo no encontrado"));

        // Si el evento define apertura de taquilla, no se permite entrar antes de esa hora.
        LocalDateTime apertura = esp.getAperturaTaquilla();
        if (apertura != null && LocalDateTime.now().isBefore(apertura)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "error", "TAQUILLA_CERRADA",
                    "mensaje", "La taquilla aun no esta abierta.",
                    "apertura", apertura.toString()));
        }

        colaService.entrarEnCola(espectaculoId, session.getId());
        return ResponseEntity.ok(Map.of("mensaje", "Has entrado en la cola."));
    }

    @GetMapping("/estado")
    public Map<String, Object> estadoCola(@RequestParam Long espectaculoId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        int posicion = colaService.obtenerPosicion(espectaculoId, session.getId());
        response.put("posicion", posicion);

        if (posicion == 0) {
            response.put("estado", "ES_TU_TURNO");
            response.put("mensaje", "Es tu turno. Tienes 3 minutos para reservar las entradas.");
        } else if (posicion > 0) {
            response.put("estado", "ENCOLA");
            response.put("mensaje", "Estas en la cola. Tienes " + (posicion - 1) + " personas delante.");
        } else {
            response.put("estado", "NOENCOLA");
            response.put("mensaje", "No estas en la cola o tu turno ha expirado.");
        }
        return response;
    }

    @PostMapping("/salir")
    public ResponseEntity<Map<String, String>> salirDeCola(@RequestParam Long espectaculoId, HttpSession session) {
        colaService.abandonarCola(espectaculoId, session.getId());
        return ResponseEntity.ok(Map.of("mensaje", "Has salido de la cola."));
    }

    // Antes de redirigir al login se conserva el turno para no perderlo.
    @PostMapping("/reservarParaLogin")
    public ResponseEntity<String> reservarParaLogin(@RequestParam Long espectaculoId, HttpSession session) {
        colaService.reservarTurnoParaLogin(espectaculoId, session.getId());
        return ResponseEntity.ok("Turno reservado.");
    }

    // Al volver del login se reactiva el turno con la nueva sesion.
    @PostMapping("/activarTurnoLogin")
    public ResponseEntity<Map<String, Object>> activarTurnoLogin(@RequestParam Long espectaculoId,
                                                                 @RequestParam String sessionIdAnterior,
                                                                 HttpSession session) {
        boolean activado = colaService.activarTurnoReservado(espectaculoId, sessionIdAnterior);
        if (activado) {
            return ResponseEntity.ok(Map.of("estado", "ES_TU_TURNO"));
        }
        return ResponseEntity.status(HttpStatus.GONE).body(Map.of("estado", "EXPIRADO"));
    }

    @GetMapping("/miSesion")
    public ResponseEntity<Map<String, Object>> miSesion(@RequestParam(required = false) Long espectaculoId,
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", session.getId());

        if (espectaculoId == null) {
            response.put("estado", "SESION_ACTIVA");
            return ResponseEntity.ok(response);
        }

        int posicion = colaService.obtenerPosicion(espectaculoId, session.getId());
        response.put("posicion", posicion);
        if (posicion == 0) response.put("estado", "ES_TU_TURNO");
        else if (posicion > 0) response.put("estado", "ENCOLA");
        else response.put("estado", "NOENCOLA");
        return ResponseEntity.ok(response);
    }
}
