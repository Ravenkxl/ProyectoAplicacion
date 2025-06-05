package co.edu.uis.organizationapp.modelo;

public class Usuario {
    private String nombre;
    private int puntos;

    public Usuario(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public void sumarPuntos(int cantidad) {
        this.puntos += cantidad;
    }
} 