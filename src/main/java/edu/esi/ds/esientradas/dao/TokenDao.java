package edu.esi.ds.esientradas.dao;

import edu.esi.ds.esientradas.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TokenDao extends JpaRepository<Token, String> {

    // Tokens mas antiguos que el limite: prerreservas a liberar.
    List<Token> findByHoraLessThan(Long horaLimite);

    List<Token> findBySessionId(String sessionId);

    void deleteByEntradaIdAndSessionId(Long entradaId, String sessionId);
}
