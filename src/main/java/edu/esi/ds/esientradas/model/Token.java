package edu.esi.ds.esientradas.model;

import jakarta.persistence.*;
import java.util.UUID;

// Token de prerreserva. Agrupa por sessionId todas las entradas que un mismo comprador
// reserva antes de pagar (es el "abcd" del escenario del enunciado). 'hora' permite caducarlo.
@Entity
public class Token {
    @Id @Column(length = 36)
    private String valor;
    private Long hora;
    private String sessionId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "entrada_id", referencedColumnName = "id")
    private Entrada entrada;

    public Token() {
        this.valor = UUID.randomUUID().toString();
        this.hora = System.currentTimeMillis();
    }

    public Entrada getEntrada() { return entrada; }
    public void setEntrada(Entrada entrada) { this.entrada = entrada; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public Long getHora() { return hora; }
    public void setHora(Long hora) { this.hora = hora; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
}
