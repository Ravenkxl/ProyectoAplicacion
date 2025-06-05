package co.edu.uis.Notas.Model;

import java.util.ArrayList;
import java.util.List;

public class NotasData {
    public static class Calificacion {
        private String descripcion;
        private double nota;
        private double porcentaje;
        
        public Calificacion(String descripcion, double nota, double porcentaje) {
            this.descripcion = descripcion;
            this.nota = nota;
            this.porcentaje = porcentaje;
        }
    }

    public static class Tema {
        private String nombre;
        private String contenido;
        private String formatoTexto; // Guardará el formato RTF
        
        public Tema(String nombre) {
            this.nombre = nombre;
            this.contenido = "";
            this.formatoTexto = "";
        }
    }

    public static class Materia {
        private String nombre;
        private List<Tema> temas;
        private List<Calificacion> calificaciones;
        
        public Materia(String nombre) {
            this.nombre = nombre;
            this.temas = new ArrayList<>();
            this.calificaciones = new ArrayList<>();
        }
    }
}
