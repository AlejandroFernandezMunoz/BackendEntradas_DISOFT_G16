package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dto.SeatDto;
import edu.esi.ds.esientradas.dto.ZoneSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Devuelve la ubicacion de entradas de un espectaculo. Un mismo recinto puede tener a la
// vez butacas numeradas (Precisa, zona central) y zonas generales (DeZona: pista y gradas),
// por eso se devuelven AMBAS listas y el frontend decide que parte es seleccionable.
@RestController
@RequestMapping("/espectaculos/{id}/ubicaciones")
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UbicacionController {

    private final EntradaDao entradaDao;

    public UbicacionController(EntradaDao entradaDao) {
        this.entradaDao = entradaDao;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getUbicaciones(@PathVariable Long id) {
        List<SeatDto> seats = entradaDao.findSeatsByEspectaculo(id);
        List<ZoneSummaryDto> zonas = entradaDao.findZonaSummaryByEspectaculo(id);

        Map<String, Object> resp = new HashMap<>();
        resp.put("seats", seats);
        resp.put("zonas", zonas);
        return ResponseEntity.ok(resp);
    }
}