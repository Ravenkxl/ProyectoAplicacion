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
    private LocalDateTime fechaLimite;
    private boolean completada;
    private LocalDateTime fechaCompletada;
    private int puntosOtorgados = 0;

    public Tarea(String titulo) {
        this.titulo = titulo;
        this.subtareas = new ArrayList<>();
        this.completada = false;
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
        if (completada) {
            marcarComoCompletada();
        } else {
            desmarcarCompletada();
        }
    }

    public LocalDateTime getFechaCompletada() {
        return fechaCompletada;
    }

    public void marcarComoCompletada() {
        this.completada = true;
        this.fechaCompletada = LocalDateTime.now();
        // When a task is completed, mark all subtasks as completed
        if (subtareas != null) {
            for (Subtarea subtarea : subtareas) {
                subtarea.setCompletada(true);
            }
        }
    }

    public void desmarcarCompletada() {
        this.completada = false;
        this.fechaCompletada = null;
        // When a task is uncompleted, unmark all subtasks
        if (subtareas != null) {
            for (Subtarea subtarea : subtareas) {
                subtarea.setCompletada(false);
            }
        }
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
        subtarea.setTareaPadre(this);
    }
    
    public void eliminarSubtarea(Subtarea subtarea) {
        if (subtareas != null && subtarea != null) {
            subtareas.remove(subtarea);
            subtarea.setTareaPadre(null);
        }
    }

    public void actualizarEstadoCompletado() {
        // Update task completion status based on subtasks
        if (subtareas != null && !subtareas.isEmpty()) {
            boolean todasCompletadas = true;
            for (Subtarea subtarea : subtareas) {
                if (!subtarea.isCompletada()) {
                    todasCompletadas = false;
                    break;
                }
            }
            if (todasCompletadas != completada) {
                setCompletada(todasCompletadas);
            }
        }
    }

    public int getPuntosOtorgados() {
        return puntosOtorgados;
    }

    public void setPuntosOtorgados(int puntosOtorgados) {
        this.puntosOtorgados = puntosOtorgados;
    }
}
