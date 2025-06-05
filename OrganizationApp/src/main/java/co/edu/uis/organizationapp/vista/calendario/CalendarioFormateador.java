package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.*;

public class CalendarioFormateador {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(java.time.LocalDate.class, new PersistenciaCalendario.LocalDateAdapter())
            .registerTypeAdapter(java.time.LocalTime.class, new PersistenciaCalendario.LocalTimeAdapter())
            .registerTypeAdapter(java.time.LocalDateTime.class, new PersistenciaCalendario.LocalDateTimeAdapter())
            .registerTypeAdapter(java.awt.Color.class, new PersistenciaCalendario.ColorAdapter())
            .create();

    // Guardar todos los eventos y tareas en un archivo JSON
    public static void guardarModelo(ModeloCalendario modelo, String rutaArchivo) throws IOException {
        List<Evento> eventos = modelo.getTodosLosEventos();
        List<Tarea> tareas = new ArrayList<>();
        // Recopilar todas las tareas de todas las fechas
        // (Si tienes un método getTodasLasTareas, úsalo. Si no, recorre las fechas conocidas)
        // Aquí solo se guardan eventos para ejemplo
        Map<String, Object> data = new HashMap<>();
        data.put("eventos", eventos);
        data.put("tareas", tareas);
        try (Writer writer = new FileWriter(rutaArchivo)) {
            gson.toJson(data, writer);
        }
    }

    // Cargar todos los eventos y tareas desde un archivo JSON
    public static void cargarModelo(ModeloCalendario modelo, String rutaArchivo) throws IOException {
        try (Reader reader = new FileReader(rutaArchivo)) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(reader, type);
            // Aquí deberías convertir los datos a List<Evento> y List<Tarea> y agregarlos al modelo
            // Este es un ejemplo básico, deberías adaptar según tu lógica
        }
    }
}
