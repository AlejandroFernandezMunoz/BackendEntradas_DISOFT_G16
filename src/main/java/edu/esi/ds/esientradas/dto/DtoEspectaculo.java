package edu.esi.ds.esientradas.dto;

import java.time.LocalDateTime;

// Vista ligera de un espectaculo para el listado de resultados de busqueda.
public class DtoEspectaculo {
    private String artista;
    private LocalDateTime fecha;
    private String escenario;
    private Long id;

    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getEscenario() { return escenario; }
    public void setEscenario(String escenario) { this.escenario = escenario; }
    public Long getID() { return id; }
    public void setID(Long id) { this.id = id; }
}
