package co.edu.uis.organizationapp.modelo.calendario;

import java.time.LocalDate;
import java.time.LocalTime;

public class Subtarea {
    private String Titulo;
    private LocalDate fecha;
    private LocalTime inicio, fin;

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String Titulo) {
        this.Titulo = Titulo;
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
    
    
}
