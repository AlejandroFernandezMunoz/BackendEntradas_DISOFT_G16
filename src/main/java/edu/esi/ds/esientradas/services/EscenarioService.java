package edu.esi.ds.esientradas.services;

import edu.esi.ds.esientradas.dao.EscenarioDao;
import edu.esi.ds.esientradas.model.Escenario;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EscenarioService {

    private final EscenarioDao dao;

    public EscenarioService(EscenarioDao dao) {
        this.dao = dao;
    }

    public void insertar(Escenario escenario) {
        try {
            dao.save(escenario);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
