package co.edu.uis.Notas;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import co.edu.uis.Notas.Model.NotasData.*;

public class DataManager {
    private static final String DATA_FILE = "notas_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static void guardarDatos(Object datos) {
        try (FileWriter writer = new FileWriter(DATA_FILE)) {
            gson.toJson(datos, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Object cargarDatos(Class<?> tipo) {
        try (FileReader reader = new FileReader(DATA_FILE)) {
            return gson.fromJson(reader, tipo);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
