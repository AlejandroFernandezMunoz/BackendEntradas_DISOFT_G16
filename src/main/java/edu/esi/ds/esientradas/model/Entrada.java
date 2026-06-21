package edu.esi.ds.esientradas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

// Entrada abstracta. Herencia JOINED: subclases Precisa (butaca/fila/planta) y DeZona (zona).
// El precio se guarda en centimos para evitar errores de coma flotante.
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Entrada {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
    private Long precio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espectaculo_id", nullable = false)
    protected Espectaculo espectaculo;

    @Enumerated(EnumType.STRING)
    protected Estado estado;

    // El token de prerreserva se gestiona en su propia tabla; aqui no se persiste.
    @Transient
    protected Token token;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @JsonIgnore
    public Espectaculo getEspectaculo() { return espectaculo; }
    public void setEspectaculo(Espectaculo espectaculo) { this.espectaculo = espectaculo; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
    public Long getPrecio() { return precio; }
    public void setPrecio(Long precio) { this.precio = precio; }
    public Token getToken() { return token; }
    public void setToken(Token token) { this.token = token; }
}
