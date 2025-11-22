package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class CalendarioFormateador {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    // Guardar todos los eventos y tareas en un archivo JSON
    public static void guardarModelo(ModeloCalendario modelo, String rutaArchivo) throws IOException {
        List<Evento> eventos = modelo.getTodosLosEventos();
        if (eventos == null || eventos.isEmpty()) {
            new File(rutaArchivo).delete();
            return;
        }
        try (Writer writer = new FileWriter(rutaArchivo)) {
            gson.toJson(eventos, writer);
        }
    }

    // Cargar todos los eventos y tareas desde un archivo JSON
    public static void cargarModelo(ModeloCalendario modelo, String rutaArchivo) throws IOException {
        try (Reader reader = new FileReader(rutaArchivo)) {
            Evento[] eventos = gson.fromJson(reader, Evento[].class);
            if (eventos != null) {
                for (Evento evento : eventos) {
                    modelo.addEvento(evento);
                }
            }
        }
    }
}
