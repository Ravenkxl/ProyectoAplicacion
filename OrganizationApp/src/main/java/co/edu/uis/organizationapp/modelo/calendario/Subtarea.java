package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDateTime;

public class Subtarea {
    private String titulo;
    private String descripcion;
    private boolean completada;
    private LocalDateTime fechaCompletada;
    private Tarea tareaPadre;

    public Subtarea() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void marcarComoCompletada() {
        this.completada = true;
        this.fechaCompletada = LocalDateTime.now();
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public void setFechaCompletada(LocalDateTime fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public Tarea getTareaPadre() {
        return tareaPadre;
    }

    public void setTareaPadre(Tarea tareaPadre) {
        this.tareaPadre = tareaPadre;
    }
}
