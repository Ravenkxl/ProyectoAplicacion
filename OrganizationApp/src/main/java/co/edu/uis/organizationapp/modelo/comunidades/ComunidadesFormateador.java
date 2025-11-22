package co.edu.uis.organizationapp.modelo.comunidades;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.List;

/**
 * Maneja la persistencia de comunidades en JSON usando GSON.
 */
public class ComunidadesFormateador {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Comunidad.class, new ComunidadAdapters.ComunidadAdapter())
            .create();

    /**
     * Guarda todas las comunidades en un archivo JSON.
     * @param comunidades Lista de comunidades a guardar
     * @param rutaArchivo Ruta del archivo de destino
     * @throws IOException Si hay error al escribir
     */
    public static void guardarComunidades(List<Comunidad> comunidades, String rutaArchivo) throws IOException {
        if (comunidades == null || comunidades.isEmpty()) {
            new File(rutaArchivo).delete();
            return;
        }
        try (Writer writer = new FileWriter(rutaArchivo)) {
            gson.toJson(comunidades, writer);
        }
    }

    /**
     * Carga todas las comunidades desde un archivo JSON.
     * @param rutaArchivo Ruta del archivo de origen
     * @return Lista de comunidades cargadas
     * @throws IOException Si hay error al leer
     */
    public static List<Comunidad> cargarComunidades(String rutaArchivo) throws IOException {
        try (Reader reader = new FileReader(rutaArchivo)) {
            Comunidad[] comunidades = gson.fromJson(reader, Comunidad[].class);
            if (comunidades != null) {
                return java.util.Arrays.asList(comunidades);
            }
        }
        return new java.util.ArrayList<>();
    }

    /**
     * Guarda una comunidad individual en formato JSON.
     * @param comunidad Comunidad a guardar
     * @return String JSON con la comunidad
     */
    public static String serializarComunidad(Comunidad comunidad) {
        return gson.toJson(comunidad, Comunidad.class);
    }

    /**
     * Carga una comunidad desde un string JSON.
     * @param json String JSON
     * @return Comunidad deserializada
     */
    public static Comunidad deserializarComunidad(String json) {
        return gson.fromJson(json, Comunidad.class);
    }
}
