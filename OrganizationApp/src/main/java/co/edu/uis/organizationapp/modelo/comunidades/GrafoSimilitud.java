package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.util.*;

/**
 * Representa un grafo de similitud entre usuarios.
 * Las aristas existen entre usuarios con similitud Jaccard >= umbral.
 */
public class GrafoSimilitud {
    private Map<Usuario, Map<Usuario, Double>> grafo; // Usuario -> (Vecino -> Similitud)
    private double umbral;

    public GrafoSimilitud(double umbral) {
        this.grafo = new HashMap<>();
        this.umbral = umbral;
    }

    /**
     * Construye el grafo a partir de una lista de usuarios.
     * @param usuarios Lista de usuarios
     */
    public void construir(List<Usuario> usuarios) {
        grafo.clear();

        // Inicializar nodos
        for (Usuario u : usuarios) {
            grafo.put(u, new HashMap<>());
        }

        // Crear aristas entre usuarios con similitud >= umbral
        for (int i = 0; i < usuarios.size(); i++) {
            for (int j = i + 1; j < usuarios.size(); j++) {
                Usuario u1 = usuarios.get(i);
                Usuario u2 = usuarios.get(j);

                double similitud = CalculadorSimilitud.calcularSimilitudConCarrera(u1, u2);
                if (similitud >= umbral) {
                    // Arista bidireccional
                    grafo.get(u1).put(u2, similitud);
                    grafo.get(u2).put(u1, similitud);
                }
            }
        }
    }

    /**
     * Obtiene los vecinos de un usuario (usuarios conectados).
     * @param usuario Usuario
     * @return Set de usuarios vecinos
     */
    public Set<Usuario> obtenerVecinos(Usuario usuario) {
        return grafo.getOrDefault(usuario, new HashMap<>()).keySet();
    }

    /**
     * Obtiene la similitud entre dos usuarios.
     * @param u1 Primer usuario
     * @param u2 Segundo usuario
     * @return Similitud o 0 si no está conectados
     */
    public double obtenerSimilitud(Usuario u1, Usuario u2) {
        return grafo.getOrDefault(u1, new HashMap<>()).getOrDefault(u2, 0.0);
    }

    /**
     * Verifica si existe arista entre dos usuarios.
     * @param u1 Primer usuario
     * @param u2 Segundo usuario
     * @return true si están conectados
     */
    public boolean conectados(Usuario u1, Usuario u2) {
        return grafo.getOrDefault(u1, new HashMap<>()).containsKey(u2);
    }

    /**
     * Obtiene todos los nodos del grafo.
     * @return Set de usuarios
     */
    public Set<Usuario> obtenerNodos() {
        return new HashSet<>(grafo.keySet());
    }

    /**
     * Obtiene el tamaño del grafo (número de nodos).
     * @return Número de usuarios en el grafo
     */
    public int obtenerTamaño() {
        return grafo.size();
    }

    public double getUmbral() {
        return umbral;
    }

    public void setUmbral(double umbral) {
        this.umbral = umbral;
    }
}
