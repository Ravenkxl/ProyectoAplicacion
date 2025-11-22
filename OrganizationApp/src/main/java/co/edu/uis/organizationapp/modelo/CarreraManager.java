package co.edu.uis.organizationapp.modelo;

import com.google.gson.Gson;
import java.io.FileReader;
import java.util.*;

public class CarreraManager {
    private List<String> carreras = new ArrayList<>();

    public void cargarCarreras(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Gson gson = new Gson();
            String[] arr = gson.fromJson(reader, String[].class);
            carreras = Arrays.asList(arr);
        } catch (Exception e) {
            carreras = new ArrayList<>();
        }
    }

    public List<String> getCarreras() {
        return carreras;
    }
}
