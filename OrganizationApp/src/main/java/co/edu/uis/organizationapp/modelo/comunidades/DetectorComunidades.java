package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.util.*;

/**
 * Detecta comunidades usando DFS (Depth-First Search) sobre el grafo de similitud.
 * Las comunidades son componentes conexas del grafo.
 */
public class DetectorComunidades {
    private GrafoSimilitud grafo;
    private Set<Usuario> visitados;

    public DetectorComunidades(GrafoSimilitud grafo) {
        this.grafo = grafo;
    }

    /**
     * Detecta todas las comunidades en el grafo.
     * @return Lista de comunidades (cada una es una componente conexa)
     */
    public List<Set<Usuario>> detectarComunidades() {
        visitados = new HashSet<>();
        List<Set<Usuario>> comunidades = new ArrayList<>();

        for (Usuario usuario : grafo.obtenerNodos()) {
            if (!visitados.contains(usuario)) {
                Set<Usuario> comunidad = new HashSet<>();
                dfs(usuario, comunidad);
                if (!comunidad.isEmpty()) {
                    comunidades.add(comunidad);
                }
            }
        }

        return comunidades;
    }

    /**
     * DFS para encontrar componente conexa.
     * @param usuario Usuario actual
     * @param comunidad Set donde se acumulan los usuarios de la comunidad
     */
    private void dfs(Usuario usuario, Set<Usuario> comunidad) {
        visitados.add(usuario);
        comunidad.add(usuario);

        // Visitar todos los vecinos
        for (Usuario vecino : grafo.obtenerVecinos(usuario)) {
            if (!visitados.contains(vecino)) {
                dfs(vecino, comunidad);
            }
        }
    }

    /**
     * Busca la comunidad a la que pertenece un usuario.
     * @param usuario Usuario a buscar
     * @return Set con la comunidad del usuario, o empty set si no existe
     */
    public Set<Usuario> obtenerComunidadDeUsuario(Usuario usuario) {
        visitados = new HashSet<>();
        Set<Usuario> comunidad = new HashSet<>();
        
        if (grafo.obtenerNodos().contains(usuario)) {
            dfs(usuario, comunidad);
        }

        return comunidad;
    }

    /**
     * Obtiene la cantidad de comunidades.
     * @return Número de comunidades
     */
    public int contarComunidades() {
        return detectarComunidades().size();
    }

    /**
     * Obtiene estadísticas sobre las comunidades.
     * @return Map con "total" comunidades y "tamaño_promedio"
     */
    public Map<String, Double> obtenerEstadisticas() {
        List<Set<Usuario>> comunidades = detectarComunidades();
        Map<String, Double> stats = new HashMap<>();
        
        stats.put("total", (double) comunidades.size());
        
        if (!comunidades.isEmpty()) {
            double tamaño_promedio = comunidades.stream()
                    .mapToInt(Set::size)
                    .average()
                    .orElse(0.0);
            stats.put("tamaño_promedio", tamaño_promedio);
        } else {
            stats.put("tamaño_promedio", 0.0);
        }

        return stats;
    }
}
