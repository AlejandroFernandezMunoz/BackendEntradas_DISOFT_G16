package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;

// Entrada de zona (no nominal): caso "concierto en campo de futbol" del enunciado.
@Entity
public class DeZona extends Entrada {
    private Integer zona;

    public DeZona() { super(); }

    public Integer getZona() { return zona; }
    public void setZona(Integer zona) { this.zona = zona; }
}
