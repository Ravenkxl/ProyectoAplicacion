
package co.edu.uis.Controlador;

import co.edu.uis.Modelo.Articulo;
import co.edu.uis.Modelo.Libro;
import com.google.gson.Gson;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import co.edu.uis.Modelo.Publicaciones;
import co.edu.uis.Modelo.Revista;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Karol Hernandez
 */
public class Datos {
private static final String Archivo ="repaso.json";
private static final Path ruta= Paths.get(Archivo);
private static Gson gson = new Gson();
public static List<Publicaciones> publicaciones= new ArrayList<>();

public List<Publicaciones> lista(){
    List<Publicaciones> publicaciones=cargarJson();
    return publicaciones; 
}
public void guardarJson(List publicaciones){
    try(FileWriter w = new FileWriter(Archivo,StandardCharsets.UTF_8)){
        gson.toJson(publicaciones,w);
    } catch (IOException ex) {
        Logger.getLogger(Datos.class.getName()).log(Level.SEVERE, null, ex);
    }
}
public List<Publicaciones> cargarJson(){
    try(FileReader r = new FileReader(Archivo,StandardCharsets.UTF_8)){
        Type t = new TypeToken<ArrayList<Publicaciones>>() {}.getType();
        return gson.fromJson(r,t);
    } catch (IOException ex) {
        return new ArrayList<>();
    } 
}
public void cargarLibro(String genero, String titulo, String autor, int isbn, int year){
    List<Publicaciones> publicaciones=cargarJson();
    publicaciones.add(new Libro(genero, titulo, autor, isbn, year));
    guardarJson(publicaciones);
}
public void cargarRevista(int numeroEdicion,String titulo, String autor, int isbn, int year){
    List<Publicaciones> publicaciones=cargarJson();
    publicaciones.add(new Revista(numeroEdicion, titulo, autor, isbn, year));
    guardarJson(publicaciones);
}
public void cargarArticulo(String nombreRevista,String titulo, String autor, int isbn, int year){
    List<Publicaciones> publicaciones=cargarJson();
    publicaciones.add(new Articulo(nombreRevista, titulo, autor, isbn, year));
    guardarJson(publicaciones);
    
}

}
