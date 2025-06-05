package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Tarea;
import co.edu.uis.organizationapp.modelo.calendario.Subtarea;
import java.time.format.DateTimeFormatter;

public class TareaCheckBox {
    private Tarea tarea;
    private boolean completada;
    private TareaCheckBox tareapadre;
    private Subtarea subtarea;
    private static final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public TareaCheckBox(Tarea tarea) {
        this.tarea = tarea;
        this.completada = tarea.isCompletada();
    }
    
    public static TareaCheckBox createWithSubtarea(Subtarea subtarea) {
        TareaCheckBox tcb = new TareaCheckBox();
        tcb.subtarea = subtarea;
        tcb.completada = subtarea.isCompletada();
        return tcb;
    }
    
    private TareaCheckBox() {
        // Constructor privado para createWithSubtarea
    }
    
    public boolean isCompletada() {
        if (isSubtarea()) {
            return subtarea.isCompletada();
        }
        return tarea != null && tarea.isCompletada();
    }
    
    public void setCompletada(boolean completada) {
        this.completada = completada;
        if (isSubtarea()) {
            subtarea.setCompletada(completada);
            // Actualizar el estado de la tarea padre si es necesario
            if (subtarea.getTareaPadre() != null) {
                subtarea.getTareaPadre().actualizarEstadoCompletado();
            }
        } else if (tarea != null) {
            tarea.setCompletada(completada);
            // Al completar una tarea principal, actualizar todas sus subtareas
            if (completada && tarea.getSubtareas() != null) {
                for (Subtarea subtarea : tarea.getSubtareas()) {
                    subtarea.setCompletada(true);
                }
            }
        }
    }
    
    public Tarea getTarea() {
        return tarea;
    }
    
    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
        if (tarea != null) {
            this.completada = tarea.isCompletada();
        }
    }
    
    public TareaCheckBox getTareaPadre() {
        return tareapadre;
    }
    
    public void setTareaPadre(TareaCheckBox tareapadre) {
        this.tareapadre = tareapadre;
    }
    
    public boolean isSubtarea() {
        return subtarea != null;
    }
    
    public void setSubtarea(Subtarea subtarea) {
        this.subtarea = subtarea;
        if (subtarea != null) {
            this.completada = subtarea.isCompletada();
        }
    }
    
    public Subtarea getSubtarea() {
        return subtarea;
    }
    
    public String getTextoMostrar() {
        if (isSubtarea()) {
            return subtarea.getTitulo();
        } else if (tarea != null) {
            return tarea.getTitulo();
        }
        return "";
    }

    public void toggleCompletada() {
        setCompletada(!isCompletada());
    }

    @Override
    public String toString() {
        return getTextoMostrar();
    }
}
