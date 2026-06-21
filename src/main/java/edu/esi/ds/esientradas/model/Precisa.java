package edu.esi.ds.esientradas.model;

import jakarta.persistence.Entity;

// Entrada con ubicacion exacta (hasta 3 coordenadas: butaca/columna, fila y planta).
// Caso "teatro" del enunciado.
@Entity
public class Precisa extends Entrada {
    private int fila;
    private int columna;
    private int planta;

    public Precisa() { super(); }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }
    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }
    public int getPlanta() { return planta; }
    public void setPlanta(int planta) { this.planta = planta; }
}
