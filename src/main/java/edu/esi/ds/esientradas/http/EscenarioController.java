package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.services.EscenarioService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Alta de escenarios (utilidad de administracion / carga de datos).
@RestController
@RequestMapping("/escenarios")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class EscenarioController {

    private final EscenarioService service;

    public EscenarioController(EscenarioService service) {
        this.service = service;
    }

    @PostMapping("/insertar")
    public void insertar(@RequestBody Escenario escenario) {
        if (escenario.getNombre() == null || escenario.getNombre().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del escenario no puede estar vacio");
        }
        service.insertar(escenario);
    }
}
