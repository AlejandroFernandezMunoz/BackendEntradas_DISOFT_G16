package edu.esi.ds.esientradas.services;

import edu.esi.ds.esientradas.dao.EntradaDao;
import edu.esi.ds.esientradas.dao.TokenDao;
import edu.esi.ds.esientradas.model.Entrada;
import edu.esi.ds.esientradas.model.Estado;
import edu.esi.ds.esientradas.model.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

// Nucleo de la prerreserva con caducidad. Implementa el requisito del enunciado:
// "entre que selecciona y paga hay un tiempo de espera; deben liberarse pasado un
// tiempo razonable". Aqui el limite es 5 minutos.
@Slf4j
@Service
public class ReservasService {

    private final EntradaDao dao;
    private final TokenDao tokenDao;

    private static final long TIEMPO_EXPIRACION_MS = 5 * 60 * 1000;

    public ReservasService(EntradaDao dao, TokenDao tokenDao) {
        this.dao = dao;
        this.tokenDao = tokenDao;
    }

    // Marca la entrada como RESERVADA y crea su token de prerreserva (mensaje 18-20).
    @Transactional
    public Long reservar(Long idEntrada, String sessionId) {
        Entrada entrada = dao.findById(idEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));

        if (entrada.getEstado() != Estado.DISPONIBLE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Entrada no disponible");
        }

        Token token = new Token();
        token.setEntrada(entrada);
        token.setSessionId(sessionId);
        tokenDao.save(token);
        dao.updateEstado(idEntrada, Estado.RESERVADA);

        return entrada.getPrecio();
    }

    // El usuario deselecciona una entrada antes de pagar: vuelve a DISPONIBLE.
    @Transactional
    public Long desreservar(Long idEntrada, String sessionId) {
        Entrada entrada = dao.findById(idEntrada).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Entrada no encontrada"));

        if (entrada.getEstado() != Estado.RESERVADA) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La entrada no esta reservada");
        }

        tokenDao.deleteByEntradaIdAndSessionId(idEntrada, sessionId);
        dao.updateEstado(idEntrada, Estado.DISPONIBLE);
        return entrada.getPrecio();
    }

    // Tarea periodica: libera las prerreservas cuyo token ha superado el tiempo limite.
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void liberarEntradasExpiradas() {
        long horaLimite = System.currentTimeMillis() - TIEMPO_EXPIRACION_MS;
        List<Token> tokensExpirados = tokenDao.findByHoraLessThan(horaLimite);

        if (!tokensExpirados.isEmpty()) {
            for (Token token : tokensExpirados) {
                Entrada entrada = token.getEntrada();
                if (entrada != null && entrada.getEstado() == Estado.RESERVADA) {
                    dao.updateEstado(entrada.getId(), Estado.DISPONIBLE);
                }
                tokenDao.delete(token);
            }
            log.info("Liberadas {} entradas prerreservadas caducadas.", tokensExpirados.size());
        }
    }

    // Tras el pago: pasa de RESERVADA a VENDIDA todas las entradas del comprador.
    @Transactional
    public List<Entrada> confirmarCompra(String sessionId) {
        List<Token> tokens = tokenDao.findBySessionId(sessionId);
        List<Entrada> compradas = new ArrayList<>();

        for (Token t : tokens) {
            Entrada e = t.getEntrada();
            if (e != null && e.getEstado() == Estado.RESERVADA) {
                dao.updateEstado(e.getId(), Estado.VENDIDA);
                compradas.add(e);
                tokenDao.delete(t);
            }
        }
        return compradas;
    }
}
