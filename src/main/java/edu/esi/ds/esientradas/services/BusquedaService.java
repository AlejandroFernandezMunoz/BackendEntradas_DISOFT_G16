package edu.esi.ds.esientradas.services;

import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.dao.EspectaculoDao;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Escenario;
import edu.esi.ds.esientradas.model.Espectaculo;
import edu.esi.ds.esientradas.model.Estado;
import org.springframework.stereotype.Service;

import java.util.List;

// Logica de busqueda de espectaculos y consulta de entradas (mensajes 6-9 del escenario).
@Service
public class BusquedaService {

    private final EscenarioDao escenarioDao;
    private final EspectaculoDao espectaculoDao;
    private final EntradaDao entradaDao;

    public BusquedaService(EscenarioDao escenarioDao, EspectaculoDao espectaculoDao, EntradaDao entradaDao) {
        this.escenarioDao = escenarioDao;
        this.espectaculoDao = espectaculoDao;
        this.entradaDao = entradaDao;
    }

    public List<Escenario> getEscenarios() {
        return escenarioDao.findAll();
    }

    public List<Espectaculo> getEspectaculos(String artista) {
        if (artista == null || artista.trim().isEmpty()) {
            return espectaculoDao.findAll();
        }
        return espectaculoDao.findByArtistaContainingIgnoreCase(artista);
    }

    public List<Espectaculo> getEspectaculos(Long idEscenario) {
        return espectaculoDao.findByEscenarioId(idEscenario);
    }

    public List<Entrada> getEntradas(Long espectaculoId) {
        return entradaDao.findByEspectaculoId(espectaculoId);
    }

    public Integer getNumeroDeEntradas(Long espectaculoId) {
        return entradaDao.countByEspectaculoId(espectaculoId);
    }

    public Integer getEntradasLibres(Long espectaculoId) {
        return entradaDao.countByEspectaculoIdAndEstado(espectaculoId, Estado.DISPONIBLE);
    }
}
