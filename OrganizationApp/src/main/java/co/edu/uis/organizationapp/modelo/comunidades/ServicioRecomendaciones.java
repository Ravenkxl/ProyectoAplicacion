package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para generar recomendaciones de comunidades a usuarios.
 */
public class ServicioRecomendaciones {
    private DetectorComunidades detector;
    private GrafoSimilitud grafo;
    private double umbralRecomendacion;

    public ServicioRecomendaciones(DetectorComunidades detector, GrafoSimilitud grafo) {
        this.detector = detector;
        this.grafo = grafo;
        this.umbralRecomendacion = 0.3; // Umbral por defecto
    }

    /**
     * Obtiene usuarios recomendados (similares) para un usuario dado.
     * @param usuario Usuario de referencia
     * @param limite Cantidad máxima de recomendaciones
     * @return Lista de usuarios ordenados por similitud
     */
    public List<Usuario> recomendarUsuarios(Usuario usuario, int limite) {
        Map<Usuario, Double> similitudes = CalculadorSimilitud.calcularSimilitudConTodos(
            usuario,
            new ArrayList<>(grafo.obtenerNodos()),
            true // considerar carrera
        );

        return similitudes.entrySet().stream()
                .filter(e -> e.getValue() >= umbralRecomendacion)
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene comunidades recomendadas para un usuario.
     * @param usuario Usuario de referencia
     * @param comunidades Lista de todas las comunidades disponibles
     * @param limite Cantidad máxima de recomendaciones
     * @return Lista de comunidades ordenadas por relevancia
     */
    public List<Comunidad> recomendarComunidades(Usuario usuario, List<Comunidad> comunidades, int limite) {
        Set<String> temasUsuario = usuario != null && usuario.getTemas() != null ? usuario.getTemas() : new HashSet<>();

        List<Map.Entry<Comunidad, Double>> puntuaciones = comunidades.stream()
                .filter(c -> c != null && !c.contieneUsuario(usuario))
                .map(c -> {
                    Set<Usuario> miembros;
                    try {
                        miembros = c.getMiembros();
                        if (miembros == null) miembros = new HashSet<>();
                    } catch (Exception ex) {
                        miembros = new HashSet<>();
                    }
                    // Similitud de temas promedio con miembros
                    double promedioTemas = miembros.stream()
                        .mapToDouble(u -> {
                            Set<String> temasU = (u != null && u.getTemas() != null) ? u.getTemas() : new HashSet<>();
                            return CalculadorSimilitud.calcularJaccardEntre(temasUsuario, temasU);
                        })
                        .average().orElse(0.0);

                    // Similitud de carrera: porcentaje de miembros con misma carrera
                    long mismosCarrera = miembros.stream()
                        .filter(u -> u != null && usuario.getCarrera() != null && usuario.getCarrera().equalsIgnoreCase(u.getCarrera()))
                        .count();
                    double porcentajeCarrera = miembros.isEmpty() ? 0.0 : (double)mismosCarrera / miembros.size();

                    // Ponderación: 70% temas, 30% carrera
                    double similitud = 0.7 * promedioTemas + 0.3 * porcentajeCarrera;
                    double puntuacion = similitud * (1.0 + Math.log(c.getNumMiembros() + 1) / 10.0);
                    return new AbstractMap.SimpleEntry<>(c, puntuacion);
                })
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limite)
                .collect(Collectors.toList());

        return puntuaciones.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    /**
     * Obtiene usuarios en la misma comunidad que podrían ser compatibles.
     * @param usuario Usuario de referencia
     * @param comunidades Lista de comunidades
     * @param limite Cantidad máxima
     * @return Lista de usuarios ordenados por similitud
     */
    public List<Usuario> recomendarEnComunidad(Usuario usuario, List<Comunidad> comunidades, int limite) {
        // Encontrar comunidad del usuario
        Comunidad comunidadUsuario = comunidades.stream()
                .filter(c -> c.contieneUsuario(usuario))
                .findFirst()
                .orElse(null);

        if (comunidadUsuario == null) {
            return new ArrayList<>();
        }

        // Recomendaciones de miembros de la misma comunidad
        return recomendarUsuarios(usuario, limite);
    }

    /**
     * Obtiene comunidades con temas similares a los de un usuario.
     * @param usuario Usuario de referencia
     * @param comunidades Lista de comunidades
     * @return Comunidades ordenadas por similitud temática
     */
    public List<Comunidad> obtenerComunidadesPorTemas(Usuario usuario, List<Comunidad> comunidades) {
        Set<String> temasUsuario = usuario.getTemas();

        return comunidades.stream()
                .filter(c -> !c.contieneUsuario(usuario))
                .map(c -> {
                    double similitud = CalculadorSimilitud.calcularJaccardEntre(temasUsuario, c.getTemas());
                    return new AbstractMap.SimpleEntry<>(c, similitud);
                })
                .filter(e -> e.getValue() > 0)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public double getUmbralRecomendacion() {
        return umbralRecomendacion;
    }

    public void setUmbralRecomendacion(double umbral) {
        this.umbralRecomendacion = umbral;
    }
}
