package com.safehome.disenosoft.safehome.Servicios;

public class Rostro {
    private String nombre;
    private boolean esConocido;

    public Rostro(String nombre, boolean esConocido){
        this.nombre = nombre;
        this.esConocido = esConocido;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isEsConocido() {
        return esConocido;
    }

    public void setEsConocido(boolean esConocido) {
        this.esConocido = esConocido;
    }
}
