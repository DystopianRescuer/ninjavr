package com.gmail.dystopianrescuer.ninjavrfx.models;

public class Transaccion {

    private final String descripcion;
    private final double precio;

    public Transaccion(String descripcion, double precio) {
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }
}
