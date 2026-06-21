package edu.esi.ds.esientradas.http;

import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dto.SeatDto;
import edu.esi.ds.esientradas.dto.ZoneSummaryDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// Devuelve la ubicacion de entradas disponibles. Si el espectaculo tiene butacas
// (Precisa) responde tipo ASIENTO; si es por zonas, tipo ZONA. Lo usa el plano del front.
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
        if (!seats.isEmpty()) {
            return ResponseEntity.ok(Map.of("tipo", "ASIENTO", "datos", seats));
        }
        List<ZoneSummaryDto> zonas = entradaDao.findZonaSummaryByEspectaculo(id);
        return ResponseEntity.ok(Map.of("tipo", "ZONA", "datos", zonas));
    }
}
