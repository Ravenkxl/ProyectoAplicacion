package co.edu.uis.organizationapp.modelo;

import java.util.*;

public class Usuario {
    private String nombre;
    private int puntos;
    private Set<String> temas; // Temas/intereses del usuario
    private String carrera; // Carrera que cursa o aspira

    public Usuario(String nombre) {
        this.nombre = nombre;
        this.puntos = 0;
        this.temas = new HashSet<>();
        this.carrera = "";
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

    // Métodos para manejar temas
    public Set<String> getTemas() {
        if (temas == null) {
            temas = new HashSet<>();
        }
        return new HashSet<>(temas); // Retorna copia para evitar modificación directa
    }

    public void agregarTema(String tema) {
        if (tema != null && !tema.trim().isEmpty()) {
            temas.add(tema.trim());
        }
    }

    public void eliminarTema(String tema) {
        temas.remove(tema);
    }

    public void setTemas(Set<String> temas) {
        this.temas = temas != null ? new HashSet<>(temas) : new HashSet<>();
    }

    // Métodos para manejar carrera
    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera != null ? carrera.trim() : "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(nombre, usuario.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }

    @Override
    public String toString() {
        return nombre;
    }
} 