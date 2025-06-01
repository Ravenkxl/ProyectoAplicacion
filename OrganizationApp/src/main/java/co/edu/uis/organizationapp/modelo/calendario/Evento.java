package co.edu.uis.organizationapp.modelo.calendario;

import java.awt.Color;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Evento { 
    private LocalDate fecha;
    private LocalTime inicio, fin;
    private String titulo;
    private String descripcion;
    private Color color;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    private List<Tarea> tareas = new ArrayList<>();
    
    public void agregarTarea(Tarea tarea) {
        if (tareas == null) {
            tareas = new ArrayList<>();
        }
        tareas.add(tarea);
    }
    
    public List<Tarea> getTareas() {
        return tareas != null ? new ArrayList<>(tareas) : new ArrayList<>();
    }
    
    public void eliminarTarea(Tarea tarea) {
        if (tareas != null) {
            tareas.remove(tarea);
        }
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    
    @Override
    public String toString() {
        return titulo != null ? titulo : "Evento sin título";
    }
}
