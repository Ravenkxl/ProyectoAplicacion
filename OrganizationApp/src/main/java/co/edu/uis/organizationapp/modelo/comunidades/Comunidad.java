package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.time.LocalDateTime;
import java.util.*;

public class Comunidad {
        /**
         * Devuelve todos los usuarios miembros de la comunidad.
         */
        public Set<Usuario> getUsuarios() {
            return getMiembros();
        }
    private String id;
    private String nombre;
    private String descripcion;
    private Set<Usuario> miembros;
    private Set<String> temas; // Temas principales de la comunidad
    private LocalDateTime fechaCreacion;
    private Usuario creador;

    public Comunidad(String nombre, Usuario creador) {
        this.id = UUID.randomUUID().toString();
        this.nombre = nombre;
        this.creador = creador;
        this.descripcion = "";
        this.miembros = new HashSet<>();
        this.temas = new HashSet<>();
        this.fechaCreacion = LocalDateTime.now();
        
        // El creador es miembro por defecto
        this.miembros.add(creador);
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion != null ? descripcion : "";
    }

    public Set<Usuario> getMiembros() {
        return new HashSet<>(miembros);
    }

    public int getNumMiembros() {
        return miembros.size();
    }

    public boolean agregarMiembro(Usuario usuario) {
        return usuario != null && miembros.add(usuario);
    }

    public boolean eliminarMiembro(Usuario usuario) {
        if (usuario != null && usuario.equals(creador)) {
            return false; // No se puede eliminar al creador
        }
        return usuario != null && miembros.remove(usuario);
    }

    public boolean contieneUsuario(Usuario usuario) {
        return usuario != null && miembros.contains(usuario);
    }

    public Set<String> getTemas() {
        return new HashSet<>(temas);
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

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public void setMiembros(Set<Usuario> miembros) {
        this.miembros = miembros != null ? new HashSet<>(miembros) : new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comunidad comunidad = (Comunidad) o;
        return Objects.equals(id, comunidad.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return nombre + " (" + miembros.size() + " miembros)";
    }
}
