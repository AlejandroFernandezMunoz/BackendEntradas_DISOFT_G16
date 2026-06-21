package edu.esi.ds.esientradas.dao;

import edu.esi.ds.esientradas.model.Escenario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EscenarioDao extends JpaRepository<Escenario, Long> {
}
