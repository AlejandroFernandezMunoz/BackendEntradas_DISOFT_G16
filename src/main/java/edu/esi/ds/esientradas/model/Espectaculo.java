package edu.esi.ds.esientradas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Un evento concreto: artista + fecha + escenario. aperturaTaquilla marca cuando se
// abre la cola virtual (null => venta abierta sin cola).
@Entity
public class Espectaculo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String artista;
    private LocalDateTime fecha;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escenario_id", nullable = false)
    private Escenario escenario;

    @OneToMany(mappedBy = "espectaculo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Entrada> entradas = new ArrayList<>();

    // El esquema real no tiene columna de apertura de taquilla, asi que no se persiste.
    // Queda como null y la cola virtual funciona sin verja horaria.
    @Transient
    private LocalDateTime aperturaTaquilla;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    @JsonIgnore
    public Escenario getEscenario() { return escenario; }
    public void setEscenario(Escenario escenario) { this.escenario = escenario; }

    @JsonIgnore
    public List<Entrada> getEntradas() { return entradas; }
    public void setEntradas(List<Entrada> entradas) { this.entradas = entradas; }

    public LocalDateTime getAperturaTaquilla() { return aperturaTaquilla; }
    public void setAperturaTaquilla(LocalDateTime aperturaTaquilla) { this.aperturaTaquilla = aperturaTaquilla; }
}
