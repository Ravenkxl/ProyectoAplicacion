package co.edu.uis.organizationapp.modelo.comunidades;

import com.google.gson.*;
import co.edu.uis.organizationapp.modelo.Usuario;
import java.lang.reflect.Type;
import java.time.LocalDateTime;

/**
 * Adaptadores GSON para serialización/deserialización de comunidades.
 */
public class ComunidadAdapters {

    public static class ComunidadAdapter implements JsonSerializer<Comunidad>, JsonDeserializer<Comunidad> {
        @Override
        public JsonElement serialize(Comunidad src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject json = new JsonObject();
            json.addProperty("id", src.getId());
            json.addProperty("nombre", src.getNombre());
            json.addProperty("descripcion", src.getDescripcion());
            json.addProperty("fechaCreacion", src.getFechaCreacion() != null ? src.getFechaCreacion().toString() : "");

            // Serializar creador (solo nombre para evitar ciclos)
            if (src.getCreador() != null) {
                json.addProperty("creador", src.getCreador().getNombre());
            }

            // Serializar miembros (solo nombres)
            JsonArray miembrosArray = new JsonArray();
            for (Usuario usuario : src.getMiembros()) {
                miembrosArray.add(usuario.getNombre());
            }
            json.add("miembros", miembrosArray);

            // Serializar temas
            JsonArray temasArray = new JsonArray();
            for (String tema : src.getTemas()) {
                temasArray.add(tema);
            }
            json.add("temas", temasArray);

            return json;
        }

        @Override
        public Comunidad deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            String id = jsonObject.get("id").getAsString();
            String nombre = jsonObject.get("nombre").getAsString();
            String descripcion = jsonObject.has("descripcion") ? jsonObject.get("descripcion").getAsString() : "";
            String fechaStr = jsonObject.has("fechaCreacion") ? jsonObject.get("fechaCreacion").getAsString() : "";
            String creadorNombre = jsonObject.has("creador") ? jsonObject.get("creador").getAsString() : "Desconocido";

            // Crear usuario creador temporal
            Usuario creador = new Usuario(creadorNombre);

            // Crear comunidad
            Comunidad comunidad = new Comunidad(nombre, creador);
            comunidad.setId(id);
            comunidad.setDescripcion(descripcion);

            // Restaurar fecha
            if (!fechaStr.isEmpty()) {
                try {
                    comunidad.setFechaCreacion(LocalDateTime.parse(fechaStr));
                } catch (Exception e) {
                    // Usar fecha actual si hay error
                }
            }

            // Restaurar temas
            if (jsonObject.has("temas")) {
                JsonArray temasArray = jsonObject.getAsJsonArray("temas");
                for (JsonElement tema : temasArray) {
                    comunidad.agregarTema(tema.getAsString());
                }
            }

            return comunidad;
        }
    }
}
