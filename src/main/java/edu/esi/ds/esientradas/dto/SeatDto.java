package edu.esi.ds.esientradas.dto;

// Una butaca concreta para el plano del frontend (caso teatro).
public class SeatDto {
    private Long id;
    private String estado;
    private Long precio;
    private Integer columna;
    private Integer fila;
    private Integer planta;

    public SeatDto(Long id, Object estado, Long precio, Integer columna, Integer fila, Integer planta) {
        this.id = id;
        this.estado = estado != null ? estado.toString() : null;
        this.precio = precio;
        this.columna = columna;
        this.fila = fila;
        this.planta = planta;
    }

    public Long getId() { return id; }
    public String getEstado() { return estado; }
    public Long getPrecio() { return precio; }
    public Integer getColumna() { return columna; }
    public Integer getFila() { return fila; }
    public Integer getPlanta() { return planta; }
}
