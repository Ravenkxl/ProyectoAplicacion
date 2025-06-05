package co.edu.uis.organizationapp.modelo.calendario;

public class TareaCheckBox {
    private Tarea tarea;
    private Subtarea subtarea;
    private TareaCheckBox tareaPadre;
    private boolean completada;    public TareaCheckBox(Tarea tarea) {
        this.tarea = tarea;
        if (tarea != null) {
            this.completada = tarea.isCompletada();
        }
    }

    public Tarea getTarea() {
        return tarea;
    }    public void setTarea(Tarea tarea) {
        this.tarea = tarea;
        if (tarea != null) {
            this.completada = tarea.isCompletada();
        }
    }

    public Subtarea getSubtarea() {
        return subtarea;
    }

    public void setSubtarea(Subtarea subtarea) {
        this.subtarea = subtarea;
        if (subtarea != null) {
            this.completada = subtarea.isCompletada();
        }
    }

    public TareaCheckBox getTareaPadre() {
        return tareaPadre;
    }

    public void setTareaPadre(TareaCheckBox tareaPadre) {
        this.tareaPadre = tareaPadre;
    }

    public boolean isCompletada() {
        return completada;
    }    public void setCompletada(boolean completada) {
        this.completada = completada;
        if (tarea != null) {
            tarea.setCompletada(completada);
        }
        if (subtarea != null) {
            subtarea.setCompletada(completada);
        }
    }

    public boolean isSubtarea() {
        return subtarea != null;
    }

    @Override
    public String toString() {
        if (subtarea != null) {
            return " └─ " + subtarea.getTitulo();
        }
        return tarea != null ? tarea.getTitulo() : "Sin título";
    }
}
