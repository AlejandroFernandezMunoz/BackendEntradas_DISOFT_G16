package edu.esi.ds.esientradas.dao;

import edu.esi.ds.esientradas.model.Espectaculo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EspectaculoDao extends JpaRepository<Espectaculo, Long> {

    // Busqueda por artista del enunciado ("Radiohead", "Natos y Waor").
    List<Espectaculo> findByArtistaContainingIgnoreCase(String artista);

    List<Espectaculo> findByEscenarioId(Long idEscenario);
}
