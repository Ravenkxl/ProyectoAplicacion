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
        this.completada = false;
    }
    
    public boolean isCompletada() {
        return completada;
    }
    
    public void setCompletada(boolean completada) {
        this.completada = completada;
        if (isSubtarea()) {
            // Si es una subtarea
            if (completada) {
                subtarea.marcarComoCompletada();
            } else {
                subtarea.setCompletada(false);
                subtarea.setFechaCompletada(null);
            }
        } else if (tarea != null) {
            // Si es una tarea principal
            if (completada) {
                tarea.marcarComoCompletada();
            } else {
                tarea.setCompletada(false);
                tarea.setFechaCompletada(null);
            }
        }
    }
    
    public Tarea getTarea() {
        return tarea;
    }
    
    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
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
    }
    
    public Subtarea getSubtarea() {
        return subtarea;
    }
    
    public String getTextoMostrar() {
        String texto = tarea.getTitulo();
        if (tarea.getInicio() != null && tarea.getFin() != null) {
            texto += " (" + tarea.getInicio().format(formatoFecha) + 
                    " - " + tarea.getFin().format(formatoFecha) + ")";
        }
        return texto;
    }
}
