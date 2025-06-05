package co.edu.uis.organizationapp.modelo.calendario;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class PersistenciaCalendario {
    private static final String ARCHIVO_JSON = "calendario_data.json";

    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(LocalTime.class, new LocalTimeAdapter())
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .registerTypeAdapter(Color.class, new ColorAdapter())
            .setPrettyPrinting()
            .create();

    public static void guardarDatos(Map<LocalDate, List<Evento>> eventos, Map<LocalDate, List<Tarea>> tareas) {
        CalendarioDTO dto = new CalendarioDTO();
        for (Map.Entry<LocalDate, List<Evento>> entry : eventos.entrySet()) {
            dto.eventos.put(entry.getKey().toString(), entry.getValue());
        }
        for (Map.Entry<LocalDate, List<Tarea>> entry : tareas.entrySet()) {
            dto.tareas.put(entry.getKey().toString(), entry.getValue());
        }
        try (Writer writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(dto, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static CalendarioDTO cargarDatos() {
        try (Reader reader = new FileReader(ARCHIVO_JSON)) {
            return gson.fromJson(reader, CalendarioDTO.class);
        } catch (Exception e) {
            // Si hay error, retorna DTO vacío
            return new CalendarioDTO();
        }
    }

    // Adaptadores para tipos especiales
    public static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
        public JsonElement serialize(LocalDate src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        public LocalDate deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return LocalDate.parse(json.getAsString());
        }
    }
    public static class LocalTimeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {
        public JsonElement serialize(LocalTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        public LocalTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return LocalTime.parse(json.getAsString());
        }
    }
    public static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.toString());
        }
        public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return LocalDateTime.parse(json.getAsString());
        }
    }
    public static class ColorAdapter implements JsonSerializer<Color>, JsonDeserializer<Color> {
        public JsonElement serialize(Color src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(src.getRGB());
        }
        public Color deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
            return new Color(json.getAsInt(), true);
        }
    }
} 