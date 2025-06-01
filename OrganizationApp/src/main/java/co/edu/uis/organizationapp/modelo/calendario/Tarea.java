package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Tarea {
    private String titulo;
    private LocalDate fecha;
    private LocalTime inicio, fin;
    private String descripcion;
    private List<Subtarea> subtareas = new ArrayList<>();
    private boolean IsTareaCompleted;
    private LocalDateTime fechaLimite;
    private boolean completada;
    private LocalDateTime fechaCompletada;

    public boolean isIsTareaCompleted() {
        return IsTareaCompleted;
    }

    public void setIsTareaCompleted(boolean IsTareaCompleted) {
        this.IsTareaCompleted = IsTareaCompleted;
    }
    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getInicio() {
        return inicio;
    }

    public void setInicio(LocalTime inicio) {
        this.inicio = inicio;
    }

    public LocalTime getFin() {
        return fin;
    }

    public void setFin(LocalTime fin) {
        this.fin = fin;
    }

    public List<Subtarea> getSubtareas() {
        return subtareas;
    }

    public void setSubtareas(List<Subtarea> subtareas) {
        this.subtareas = subtareas;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public void setFechaCompletada(LocalDateTime fechaCompletada) {
        this.fechaCompletada = fechaCompletada;
    }

    public void marcarComoCompletada() {
        this.completada = true;
        this.fechaCompletada = LocalDateTime.now();
    }

    public void desmarcarCompletada() {
        this.completada = false;
        this.fechaCompletada = null;
    }

    public boolean estaVencida() {
        if (completada) return false;
        return fechaLimite != null && LocalDateTime.now().isAfter(fechaLimite);
    }

    public void agregarSubtarea(Subtarea subtarea) {
        if (subtareas == null) {
            subtareas = new ArrayList<>();
        }
        subtareas.add(subtarea);
    }
    
    public void eliminarSubtarea(Subtarea subtarea) {
        subtareas.remove(subtarea);
    }
}
