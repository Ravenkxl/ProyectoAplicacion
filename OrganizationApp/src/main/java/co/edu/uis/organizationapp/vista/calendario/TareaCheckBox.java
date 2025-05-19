package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Tarea;
import java.time.format.DateTimeFormatter;

public class TareaCheckBox {
    private Tarea tarea;
    private boolean completada;
    private TareaCheckBox tareapadre;
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
    
    public String getTextoMostrar() {
        String texto = tarea.getTitulo();
        if (tarea.getInicio() != null && tarea.getFin() != null) {
            texto += " (" + tarea.getInicio().format(formatoFecha) + 
                    " - " + tarea.getFin().format(formatoFecha) + ")";
        }
        return texto;
    }
}
