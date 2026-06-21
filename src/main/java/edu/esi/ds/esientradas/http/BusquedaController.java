package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.dto.DtoEspectaculo;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.services.BusquedaService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Endpoints de busqueda y consulta (GET /search y derivados del escenario del enunciado).
@RestController
@RequestMapping("/busqueda")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class BusquedaController {

    private final BusquedaService service;

    public BusquedaController(BusquedaService service) {
        this.service = service;
    }

    @GetMapping("/getEspectaculos")
    public List<DtoEspectaculo> getEspectaculos(@RequestParam String artista) {
        return mapear(service.getEspectaculos(artista));
    }

    @GetMapping("/getEspectaculos/{idEscenario}")
    public List<DtoEspectaculo> getEspectaculos(@PathVariable Long idEscenario) {
        return mapear(service.getEspectaculos(idEscenario));
    }

    @GetMapping("/getEscenarios")
    public List<Escenario> getEscenarios() {
        return service.getEscenarios();
    }

    @GetMapping("/getEntradas")
    public List<Entrada> getEntradas(@RequestParam Long espectaculoId) {
        return service.getEntradas(espectaculoId);
    }

    @GetMapping("/getNumeroDeEntradas")
    public Integer getNumeroDeEntradas(@RequestParam Long espectaculoId) {
        return service.getNumeroDeEntradas(espectaculoId);
    }

    @GetMapping("/getEntradasLibres")
    public Integer getEntradasLibres(@RequestParam Long espectaculoId) {
        return service.getEntradasLibres(espectaculoId);
    }

    private List<DtoEspectaculo> mapear(List<Espectaculo> espectaculos) {
        return espectaculos.stream().map(e -> {
            DtoEspectaculo dto = new DtoEspectaculo();
            dto.setID(e.getId());
            dto.setArtista(e.getArtista());
            dto.setFecha(e.getFecha());
            dto.setEscenario(e.getEscenario().getNombre());
            return dto;
        }).toList();
    }
}
