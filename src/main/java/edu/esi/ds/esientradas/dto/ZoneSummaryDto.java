package edu.esi.ds.esientradas.dto;

// Resumen de una zona para el frontend (caso concierto).
public class ZoneSummaryDto {
    private Integer zona;
    private Long disponibles;
    private Long total;
    private Long precioMin;
    private Long precioMax;

    public ZoneSummaryDto(Integer zona, Long disponibles, Long total, Long precioMin, Long precioMax) {
        this.zona = zona;
        this.disponibles = disponibles;
        this.total = total;
        this.precioMin = precioMin;
        this.precioMax = precioMax;
    }

    public Integer getZona() { return zona; }
    public Long getDisponibles() { return disponibles; }
    public Long getTotal() { return total; }
    public Long getPrecioMin() { return precioMin; }
    public Long getPrecioMax() { return precioMax; }
}
