package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dto.ReservaResponse;
import edu.esi.ds.esientradas.model.DeZona;
import edu.esi.ds.esientradas.services.ReservasService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

// Prerreserva/desreserva de entradas. El token agrupa todas las prerreservas del mismo
// comprador (mensajes 18-24 del escenario): el front lo recibe y lo reenvia en la siguiente.
@RestController
@RequestMapping("/reservas")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class ReservasController {

    private final ReservasService service;
    private final EntradaDao entradaDao;

    public ReservasController(ReservasService service, EntradaDao entradaDao) {
        this.service = service;
        this.entradaDao = entradaDao;
    }

    // Prerreserva de una butaca concreta (caso teatro).
    @PutMapping("/reservar")
    public ReservaResponse reservar(HttpSession session,
                                    @RequestParam Long idEntrada,
                                    @RequestParam(required = false) String tokenReserva) {

        // Primera prerreserva: se usa el id de sesion. Siguientes: el token recibido,
        // de modo que todas queden agrupadas por el mismo comprador.
        String sessionIdLogico = (tokenReserva != null && !tokenReserva.isBlank())
                ? tokenReserva
                : session.getId();

        Long precioEntrada = service.reservar(idEntrada, sessionIdLogico);

        Long precioTotal = (Long) session.getAttribute("precioTotal");
        precioTotal = (precioTotal == null ? 0L : precioTotal) + precioEntrada;
        session.setAttribute("precioTotal", precioTotal);

        return new ReservaResponse(precioTotal, sessionIdLogico);
    }

    @PutMapping("/desreservar")
    public Long desreservar(HttpSession session, @RequestParam Long idEntrada) {
        Long precioEntrada = service.desreservar(idEntrada, session.getId());

        Long precioTotal = (Long) session.getAttribute("precioTotal");
        precioTotal = (precioTotal == null ? 0L : precioTotal) - precioEntrada;
        if (precioTotal < 0) precioTotal = 0L;
        session.setAttribute("precioTotal", precioTotal);
        return precioTotal;
    }

    // Prerreserva de zona (caso concierto): asigna la entrada disponible mas barata.
    @PutMapping("/reservarZona")
    public Map<String, Object> reservarZona(HttpSession session,
                                            @RequestParam Long espectaculoId,
                                            @RequestParam Integer zona,
                                            @RequestParam(required = false) String tokenReserva) {

        List<DeZona> disponibles = entradaDao.findDisponiblesByEspectaculoAndZona(espectaculoId, zona);
        if (disponibles.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No quedan entradas en esta zona");
        }

        String sessionIdLogico = (tokenReserva != null && !tokenReserva.isBlank())
                ? tokenReserva
                : session.getId();

        DeZona masBarata = disponibles.get(0);
        Long precio = service.reservar(masBarata.getId(), sessionIdLogico);

        Long precioTotal = (Long) session.getAttribute("precioTotal");
        precioTotal = (precioTotal == null ? 0L : precioTotal) + precio;
        session.setAttribute("precioTotal", precioTotal);

        String token = (tokenReserva == null || tokenReserva.isBlank())
                ? sessionIdLogico
                : tokenReserva;

        return Map.of(
                "precioTotal", precioTotal,
                "idReservada", masBarata.getId(),
                "tokenReserva", token);
    }
}
