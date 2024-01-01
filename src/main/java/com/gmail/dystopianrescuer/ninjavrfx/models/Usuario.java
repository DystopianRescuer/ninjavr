package com.gmail.dystopianrescuer.ninjavrfx.models;

public class Usuario {

    private final String username, password, nombre, privilegios;

    public Usuario(String username, String password, String nombre, String privilegios) {
        this.username = username;
        this.password = password;
        this.nombre = nombre;
        this.privilegios = privilegios;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isAdmin() {
        return privilegios.equals("ADMIN");
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", nombre='" + nombre + '\'' +
                ", privilegios='" + privilegios + '\'' +
                '}';
    }
}
