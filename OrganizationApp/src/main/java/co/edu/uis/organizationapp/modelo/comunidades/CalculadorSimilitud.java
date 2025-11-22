package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.util.*;

/**
 * Calcula similitud entre usuarios usando el coeficiente de Jaccard.
 * Jaccard(u,v) = |temas_u ∩ temas_v| / |temas_u ∪ temas_v|
 */
public class CalculadorSimilitud {
        /**
         * Calcula la similitud considerando tanto temas como carrera.
         * Fórmula: similitud_final = 0.7 * jaccard_temas + 0.3 * similitud_carrera
         * @param usuario1 Primer usuario
         * @param usuario2 Segundo usuario
         * @return Similitud ponderada entre 0 y 1
         */
        public static double calcularSimilitudConCarrera(Usuario usuario1, Usuario usuario2) {
            if (usuario1 == null || usuario2 == null) {
                return 0.0;
            }

            double jaccardTemas = calcularJaccard(usuario1, usuario2);
            double similitudCarrera = 0.0;

            // Si ambos tienen carrera definida
            if (usuario1.getCarrera() != null && usuario2.getCarrera() != null
                    && !usuario1.getCarrera().isEmpty() && !usuario2.getCarrera().isEmpty()) {
                similitudCarrera = usuario1.getCarrera().equalsIgnoreCase(usuario2.getCarrera()) ? 1.0 : 0.0;
            }

            // Ponderación: 70% temas, 30% carrera
            return 0.7 * jaccardTemas + 0.3 * similitudCarrera;
        }
    
    /**
     * Calcula la similitud Jaccard entre dos usuarios basada en sus temas.
     * @param usuario1 Primer usuario
     * @param usuario2 Segundo usuario
     * @return Similitud entre 0 y 1. Retorna 0 si alguno está nulo o no tienen temas.
     */
    public static double calcularJaccard(Usuario usuario1, Usuario usuario2) {
        if (usuario1 == null || usuario2 == null) {
            return 0.0;
        }

        Set<String> temas1 = usuario1.getTemas();
        Set<String> temas2 = usuario2.getTemas();

        if (temas1.isEmpty() && temas2.isEmpty()) {
            return 0.0;
        }

        if (temas1.isEmpty() || temas2.isEmpty()) {
            return 0.0;
        }

        // Calcular intersección
        Set<String> interseccion = new HashSet<>(temas1);
        interseccion.retainAll(temas2);

        // Calcular unión
        Set<String> union = new HashSet<>(temas1);
        union.addAll(temas2);

        // Jaccard = |intersección| / |unión|
        return (double) interseccion.size() / union.size();
    }

    /**
     * Calcula la similitud Jaccard entre dos conjuntos de temas.
     * @param temas1 Primer conjunto de temas
     * @param temas2 Segundo conjunto de temas
     * @return Similitud entre 0 y 1
     */
    public static double calcularJaccardEntre(Set<String> temas1, Set<String> temas2) {
        if (temas1 == null || temas2 == null) {
            return 0.0;
        }

        if (temas1.isEmpty() && temas2.isEmpty()) {
            return 0.0;
        }

        if (temas1.isEmpty() || temas2.isEmpty()) {
            return 0.0;
        }

        Set<String> interseccion = new HashSet<>(temas1);
        interseccion.retainAll(temas2);

        Set<String> union = new HashSet<>(temas1);
        union.addAll(temas2);

        return (double) interseccion.size() / union.size();
    }

    /**
     * Crea un mapa de similitudes entre un usuario y una lista de usuarios.
     * @param usuario Usuario de referencia
     * @param usuarios Lista de otros usuarios
     * @return Mapa con Usuario -> similitud, ordenado de mayor a menor similitud
     */
    public static Map<Usuario, Double> calcularSimilitudConTodos(Usuario usuario, List<Usuario> usuarios) {
        return calcularSimilitudConTodos(usuario, usuarios, false);
    }

    /**
     * Crea un mapa de similitudes considerando carrera si es indicado.
     * @param usuario Usuario de referencia
     * @param usuarios Lista de otros usuarios
     * @param considerarCarrera Si true, incluye carrera en el cálculo
     * @return Mapa con Usuario -> similitud, ordenado de mayor a menor similitud
     */
    public static Map<Usuario, Double> calcularSimilitudConTodos(Usuario usuario, List<Usuario> usuarios, boolean considerarCarrera) {
        Map<Usuario, Double> similitudes = new LinkedHashMap<>();

        for (Usuario otro : usuarios) {
            if (!otro.equals(usuario)) {
                double sim = considerarCarrera 
                    ? calcularSimilitudConCarrera(usuario, otro)
                    : calcularJaccard(usuario, otro);
                if (sim > 0) {
                    similitudes.put(otro, sim);
                }
            }
        }

        // Ordenar por similitud descendente
        return similitudes.entrySet()
                .stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(java.util.stream.Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}
