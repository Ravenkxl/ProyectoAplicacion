package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import java.util.*;

/**
 * Modelo central que gestiona todas las comunidades y su detección.
 */
public class ModeloComunidades {
    private List<Comunidad> comunidades;
    private GrafoSimilitud grafo;
    private DetectorComunidades detector;
    private ServicioRecomendaciones servicio;
    private double umbralSimilitud;

    public ModeloComunidades() {
        this.comunidades = new ArrayList<>();
        this.umbralSimilitud = 0.3; // Umbral por defecto (30% similitud)
        this.grafo = new GrafoSimilitud(umbralSimilitud);
        this.detector = new DetectorComunidades(grafo);
        this.servicio = new ServicioRecomendaciones(detector, grafo);
    }

    /**
     * Crea una nueva comunidad.
     * @param nombre Nombre de la comunidad
     * @param creador Usuario creador
     * @return Comunidad creada
     */
    public Comunidad crearComunidad(String nombre, Usuario creador) {
        Comunidad comunidad = new Comunidad(nombre, creador);
        comunidades.add(comunidad);
        return comunidad;
    }

    /**
     * Elimina una comunidad.
     * @param comunidad Comunidad a eliminar
     * @return true si se eliminó, false si no existe
     */
    public boolean eliminarComunidad(Comunidad comunidad) {
        return comunidades.remove(comunidad);
    }

    /**
     * Obtiene todas las comunidades.
     * @return Lista de comunidades
     */
    public List<Comunidad> obtenerComunidades() {
        return new ArrayList<>(comunidades);
    }

    /**
     * Obtiene una comunidad por ID.
     * @param id ID de la comunidad
     * @return Comunidad o null si no existe
     */
    public Comunidad obtenerComunidadPorId(String id) {
        return comunidades.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene la comunidad a la que pertenece un usuario.
     * @param usuario Usuario
     * @return Comunidad o null si no pertenece a ninguna
     */
    public Comunidad obtenerComunidadDelUsuario(Usuario usuario) {
        return comunidades.stream()
                .filter(c -> c.contieneUsuario(usuario))
                .findFirst()
                .orElse(null);
    }

    /**
     * Detecta comunidades automáticamente basadas en similitud de temas.
     * @param usuarios Lista de usuarios a analizar
     * @return Lista de comunidades detectadas
     */
    public List<Set<Usuario>> detectarComunidadesAutomaticamente(List<Usuario> usuarios) {
        grafo.construir(usuarios);
        return detector.detectarComunidades();
    }

    /**
     * Obtiene estadísticas de detección de comunidades.
     * @return Map con estadísticas
     */
    public Map<String, Double> obtenerEstadisticasComunidades() {
        return detector.obtenerEstadisticas();
    }

    /**
     * Obtiene recomendaciones de comunidades para un usuario.
     * @param usuario Usuario
     * @param limite Cantidad máxima de recomendaciones
     * @return Lista de comunidades recomendadas
     */
    public List<Comunidad> recomendarComunidades(Usuario usuario, int limite) {
        return servicio.recomendarComunidades(usuario, comunidades, limite);
    }

    /**
     * Obtiene recomendaciones de usuarios similares.
     * @param usuario Usuario
     * @param limite Cantidad máxima
     * @return Lista de usuarios recomendados
     */
    public List<Usuario> recomendarUsuarios(Usuario usuario, int limite) {
        return servicio.recomendarUsuarios(usuario, limite);
    }

    /**
     * Obtiene recomendaciones de usuarios en la misma comunidad.
     * @param usuario Usuario
     * @param limite Cantidad máxima
     * @return Lista de usuarios recomendados
     */
    public List<Usuario> recomendarEnComunidad(Usuario usuario, int limite) {
        return servicio.recomendarEnComunidad(usuario, comunidades, limite);
    }

    /**
     * Obtiene comunidades por temas similares.
     * @param usuario Usuario
     * @return Lista de comunidades ordenadas por similitud temática
     */
    public List<Comunidad> obtenerComunidadesPorTemas(Usuario usuario) {
        return servicio.obtenerComunidadesPorTemas(usuario, comunidades);
    }

    /**
     * Obtiene el grafo de similitud.
     * @return Grafo de similitud
     */
    public GrafoSimilitud getGrafo() {
        return grafo;
    }

    /**
     * Obtiene el detector de comunidades.
     * @return Detector
     */
    public DetectorComunidades getDetector() {
        return detector;
    }

    /**
     * Obtiene el servicio de recomendaciones.
     * @return Servicio
     */
    public ServicioRecomendaciones getServicio() {
        return servicio;
    }

    public double getUmbralSimilitud() {
        return umbralSimilitud;
    }

    public void setUmbralSimilitud(double umbral) {
        this.umbralSimilitud = umbral;
        grafo.setUmbral(umbral);
    }

    public void setComunidades(List<Comunidad> comunidades) {
        if (comunidades != null) {
            this.comunidades = new ArrayList<>();
            for (Comunidad c : comunidades) {
                if (c != null) this.comunidades.add(c);
            }
        } else {
            this.comunidades = new ArrayList<>();
        }
    }

    public void setGrafo(GrafoSimilitud grafo) {
        this.grafo = grafo;
    }

        /**
         * Obtiene todos los usuarios únicos de todas las comunidades.
         * @return Lista de usuarios únicos
         */
        public List<Usuario> obtenerUsuarios() {
            Set<Usuario> usuarios = new HashSet<>();
            for (Comunidad comunidad : comunidades) {
                usuarios.addAll(comunidad.getUsuarios());
            }
            return new ArrayList<>(usuarios);
        }
}
