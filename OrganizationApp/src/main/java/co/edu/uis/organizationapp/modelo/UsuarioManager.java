package co.edu.uis.organizationapp.modelo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class UsuarioManager {
    private static final String ARCHIVO_JSON = "usuario_data.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static void guardarUsuario(Usuario usuario) {
        try (Writer writer = new FileWriter(ARCHIVO_JSON)) {
            gson.toJson(usuario, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Usuario cargarUsuario() {
        try (Reader reader = new FileReader(ARCHIVO_JSON)) {
            return gson.fromJson(reader, Usuario.class);
        } catch (IOException e) {
            // Si no existe el archivo, retorna un usuario por defecto
            return new Usuario("Invitado");
        }
    }
} 