package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDateTime;

public class Subtarea {
    private String titulo;
    private String descripcion;
    private boolean completada;
    private LocalDateTime fechaCompletada;
    private transient Tarea tareaPadre;

    public Subtarea() {
        this.completada = false;
    }
    
    public Subtarea(String titulo) {
        this.titulo = titulo;
        this.completada = false;
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

    public void setCompletada(boolean completada) {
        boolean oldValue = this.completada;
        this.completada = completada;
        if (completada && !oldValue) {
            marcarComoCompletada();
        } else if (!completada && oldValue) {
            desmarcarCompletada();
        }
        if (tareaPadre != null) {
            tareaPadre.actualizarEstadoCompletado();
        }
    }

    public void marcarComoCompletada() {
        this.completada = true;
        this.fechaCompletada = LocalDateTime.now();
        if (tareaPadre != null) {
            tareaPadre.actualizarEstadoCompletado();
        }
    }

    public void desmarcarCompletada() {
        this.completada = false;
        this.fechaCompletada = null;
        if (tareaPadre != null) {
            tareaPadre.desmarcarCompletada();
        }
    }

    public LocalDateTime getFechaCompletada() {
        return fechaCompletada;
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
