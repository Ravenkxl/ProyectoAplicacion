package co.edu.uis.organizationapp.modelo.comunidades;

import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.TemaManager;
import java.util.*;
import java.util.stream.Collectors;

public class ServicioRecomendaciones {
    private DetectorComunidades detector;
    private GrafoSimilitud grafo;
    private double umbralRecomendacion;
    private TemaManager temaManager;

    public ServicioRecomendaciones(DetectorComunidades detector, GrafoSimilitud grafo) {
        this.detector = detector;
        this.grafo = grafo;
        this.umbralRecomendacion = 0.3; 
        this.temaManager = new TemaManager();
    }

    public List<Usuario> recomendarUsuarios(Usuario usuario, int limite) {
        Map<Usuario, Double> similitudes = CalculadorSimilitud.calcularSimilitudConTodos(
            usuario,
            new ArrayList<>(grafo.obtenerNodos()),
            true
        );

        return similitudes.entrySet().stream()
                .filter(e -> e.getValue() >= umbralRecomendacion)
                .limit(limite)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private boolean esMateriasComunes(Comunidad c) {
        if (c == null) return false;
        String d = c.getDescripcion();
        if (d != null && d.toLowerCase(java.util.Locale.ROOT).contains("todas las ingenierías")) return true;
        String n = c.getNombre();
        if (n != null) {
            String ln = normaliza(n);
            if (ln.contains("calculo") || ln.contains("algebra") || ln.contains("matematic")
                    || ln.contains("fisica") || ln.contains("probabilidad") || ln.contains("estadistica")
                    || ln.contains("algoritmos") || ln.contains("estructuras de datos") || ln.contains("orientada a objetos")) {
                return true;
            }
        }
        return false;
    }

    private boolean nombreContieneTema(String nombre, Set<String> temas) {
        if (nombre == null || temas == null || temas.isEmpty()) return false;
        String n = normaliza(nombre);
        for (String t : temas) {
            if (t == null) continue;
            String tt = normaliza(t);
            if (tt.length() < 3) continue;
            if (n.contains(tt)) return true;
        }
        return false;
    }

    private String normaliza(String s) {
        String r = s.toLowerCase(java.util.Locale.ROOT);
        r = r.replace("á","a").replace("é","e").replace("í","i").replace("ó","o").replace("ú","u").replace("ñ","n");
        r = r.replace("Á","a").replace("É","e").replace("Í","i").replace("Ó","o").replace("Ú","u").replace("Ñ","n");
        return r;
    }

    public List<Comunidad> recomendarComunidades(Usuario usuario, List<Comunidad> comunidades, int limite) {
        if (usuario == null || comunidades == null || comunidades.isEmpty()) {
            return new ArrayList<>();
        }

        final Set<String> temasUsuarioFinal = usuario.getTemas() != null ? usuario.getTemas() : new HashSet<>();
        final String carreraUsuarioFinal = usuario.getCarrera() != null ? usuario.getCarrera() : "";

        if (temasUsuarioFinal.isEmpty() && carreraUsuarioFinal.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<Comunidad, Double>> puntuaciones = comunidades.stream()
                .filter(c -> c != null && !c.contieneUsuario(usuario))
                .map(c -> {
                    Set<Usuario> miembros = c.getMiembros();
                    if (miembros == null || miembros.isEmpty()) {
                        return new AbstractMap.SimpleEntry<>(c, 0.0);
                    }

                    long miembrosMismaCarrera = miembros.stream()
                        .filter(u -> u != null && carreraUsuarioFinal.equalsIgnoreCase(u.getCarrera()))
                        .count();
                    
                    double porcentajeCarrera = miembros.isEmpty() ? 0.0 : (double) miembrosMismaCarrera / miembros.size();
                    
                    boolean esMC = esMateriasComunes(c);
                    if (!esMC && porcentajeCarrera < 0.3) {
                        porcentajeCarrera = porcentajeCarrera * 0.5;
                    }

                    Set<String> temasComunidad = c.getTemas() != null ? c.getTemas() : new HashSet<>();
                    
                    Set<String> temasUnidos = new HashSet<>(temasComunidad);
                    for (Usuario m : miembros) {
                        if (m != null && m.getTemas() != null) {
                            temasUnidos.addAll(m.getTemas());
                        }
                    }
                    
                    double jaccardCore = CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasComunidad);
                    double jaccardComunidad = CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasUnidos);
                    
                    double promedioJaccardMiembros = miembros.stream()
                        .mapToDouble(u -> {
                            Set<String> temasU = (u != null && u.getTemas() != null) ? u.getTemas() : new HashSet<>();
                            return CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasU);
                        })
                        .average().orElse(0.0);
                    
                    double similaridadTemas = esMC
                        ? (0.8 * jaccardCore + 0.2 * promedioJaccardMiembros)
                        : (0.6 * jaccardComunidad + 0.4 * promedioJaccardMiembros);

                    double coherencia = calcularCoherenciaTemasCarrera(temasUsuarioFinal, carreraUsuarioFinal);

                    int numMiembros = miembros.size();
                    double factorTamanio = Math.min(1.0, numMiembros / 10.0); 

                    double wCarrera = esMC ? 0.20 : 0.40;
                    double wTemas = esMC ? 0.50 : 0.30;
                    double wCoherencia = 0.20;
                    double wTam = 0.10;
                    double puntuacion = (wCarrera * porcentajeCarrera)
                                      + (wTemas * similaridadTemas)
                                      + (wCoherencia * coherencia)
                                      + (wTam * factorTamanio);

                    if (nombreContieneTema(c.getNombre(), temasUsuarioFinal)) {
                        puntuacion = Math.min(1.0, puntuacion + 0.10);
                    }

                    return new AbstractMap.SimpleEntry<>(c, puntuacion);
                })
                .filter(e -> e.getValue() >= 0.15)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(limite)
                .collect(Collectors.toList());

        return puntuaciones.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public List<Comunidad> recomendarComunidadesDinamicas(Usuario usuario, List<Comunidad> comunidades, double umbralMinimo) {
        if (usuario == null || comunidades == null || comunidades.isEmpty()) {
            return new ArrayList<>();
        }

        final Set<String> temasUsuarioFinal = usuario.getTemas() != null ? usuario.getTemas() : new HashSet<>();
        final String carreraUsuarioFinal = usuario.getCarrera() != null ? usuario.getCarrera() : "";

        if (temasUsuarioFinal.isEmpty() && carreraUsuarioFinal.isEmpty()) {
            return new ArrayList<>();
        }

        List<Map.Entry<Comunidad, Double>> puntuaciones = comunidades.stream()
                .filter(c -> c != null && !c.contieneUsuario(usuario))
                .map(c -> {
                    Set<Usuario> miembros = c.getMiembros();
                    if (miembros == null || miembros.isEmpty()) {
                        return new AbstractMap.SimpleEntry<>(c, 0.0);
                    }

                    long miembrosMismaCarrera = miembros.stream()
                        .filter(u -> u != null && carreraUsuarioFinal.equalsIgnoreCase(u.getCarrera()))
                        .count();
                    
                    double porcentajeCarrera = miembros.isEmpty() ? 0.0 : (double) miembrosMismaCarrera / miembros.size();
                    
                    boolean esMC = esMateriasComunes(c);
                    if (!esMC && porcentajeCarrera < 0.3) {
                        porcentajeCarrera = porcentajeCarrera * 0.5;
                    }

                    Set<String> temasComunidad = c.getTemas() != null ? c.getTemas() : new HashSet<>();
                    
                    Set<String> temasUnidos = new HashSet<>(temasComunidad);
                    for (Usuario m : miembros) {
                        if (m != null && m.getTemas() != null) {
                            temasUnidos.addAll(m.getTemas());
                        }
                    }
                    
                    double jaccardComunidad = CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasUnidos);
                    
                    double promedioJaccardMiembros = miembros.stream()
                        .mapToDouble(u -> {
                            Set<String> temasU = (u != null && u.getTemas() != null) ? u.getTemas() : new HashSet<>();
                            return CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasU);
                        })
                        .average().orElse(0.0);
                    
                    double similaridadTemas = 0.6 * jaccardComunidad + 0.4 * promedioJaccardMiembros;

                    double coherencia = calcularCoherenciaTemasCarrera(temasUsuarioFinal, carreraUsuarioFinal);

                    int numMiembros = miembros.size();
                    double factorTamanio = Math.min(1.0, numMiembros / 10.0);

                    double puntuacion = (0.40 * porcentajeCarrera)
                                      + (0.30 * similaridadTemas)
                                      + (0.20 * coherencia)
                                      + (0.10 * factorTamanio);

                    return new AbstractMap.SimpleEntry<>(c, puntuacion);
                })
                .filter(e -> e.getValue() >= umbralMinimo) // Usar umbral dinámico (SIN límite de cantidad)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toList());

        return puntuaciones.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public Map<Comunidad, Double> recomendarComunidadesConPuntuacion(Usuario usuario, List<Comunidad> comunidades, double umbralMinimo) {
        if (usuario == null || comunidades == null || comunidades.isEmpty()) {
            return new HashMap<>();
        }

        final Set<String> temasUsuarioFinal = usuario.getTemas() != null ? usuario.getTemas() : new HashSet<>();
        final String carreraUsuarioFinal = usuario.getCarrera() != null ? usuario.getCarrera() : "";

        if (temasUsuarioFinal.isEmpty() && carreraUsuarioFinal.isEmpty()) {
            return new HashMap<>();
        }

        return comunidades.stream()
                .filter(c -> c != null && !c.contieneUsuario(usuario))
                .map(c -> {
                    Set<Usuario> miembros = c.getMiembros();
                    if (miembros == null || miembros.isEmpty()) {
                        return new AbstractMap.SimpleEntry<>(c, 0.0);
                    }

                    long miembrosMismaCarrera = miembros.stream()
                        .filter(u -> u != null && carreraUsuarioFinal.equalsIgnoreCase(u.getCarrera()))
                        .count();
                    
                    double porcentajeCarrera = miembros.isEmpty() ? 0.0 : (double) miembrosMismaCarrera / miembros.size();
                    boolean esMC = esMateriasComunes(c);
                    if (!esMC && porcentajeCarrera < 0.3) {
                        porcentajeCarrera = porcentajeCarrera * 0.5;
                    }

                    Set<String> temasComunidad = c.getTemas() != null ? c.getTemas() : new HashSet<>();
                    Set<String> temasUnidos = new HashSet<>(temasComunidad);
                    for (Usuario m : miembros) {
                        if (m != null && m.getTemas() != null) {
                            temasUnidos.addAll(m.getTemas());
                        }
                    }
                    
                    double jaccardCore = CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasComunidad);
                    double jaccardComunidad = CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasUnidos);
                    double promedioJaccardMiembros = miembros.stream()
                        .mapToDouble(u -> {
                            Set<String> temasU = (u != null && u.getTemas() != null) ? u.getTemas() : new HashSet<>();
                            return CalculadorSimilitud.calcularJaccardEntre(temasUsuarioFinal, temasU);
                        })
                        .average().orElse(0.0);
                    
                    double similaridadTemas = esMC
                        ? (0.8 * jaccardCore + 0.2 * promedioJaccardMiembros)
                        : (0.6 * jaccardComunidad + 0.4 * promedioJaccardMiembros);
                    double coherencia = calcularCoherenciaTemasCarrera(temasUsuarioFinal, carreraUsuarioFinal);
                    double factorTamanio = Math.min(1.0, miembros.size() / 10.0);

                    double wCarrera = esMC ? 0.20 : 0.40;
                    double wTemas = esMC ? 0.50 : 0.30;
                    double wCoherencia = 0.20;
                    double wTam = 0.10;
                    double puntuacion = (wCarrera * porcentajeCarrera)
                                      + (wTemas * similaridadTemas)
                                      + (wCoherencia * coherencia)
                                      + (wTam * factorTamanio);

                    if (nombreContieneTema(c.getNombre(), temasUsuarioFinal)) {
                        puntuacion = Math.min(1.0, puntuacion + 0.10);
                    }

                    return new AbstractMap.SimpleEntry<>(c, puntuacion);
                })
                .filter(e -> e.getValue() >= umbralMinimo)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a,b) -> a, java.util.LinkedHashMap::new));
    }

    private double calcularCoherenciaTemasCarrera(Set<String> temas, String carrera) {
        if (temas.isEmpty() || carrera.isEmpty()) {
            return 0.5;
        }

        Set<String> temasEsperados = temaManager.getTemasEsperados(carrera);
        
        if (temasEsperados.isEmpty()) {
            return 0.5; 
        }

        long temasCoherentes = temas.stream()
            .filter(t -> temasEsperados.stream()
                .anyMatch(te -> te.equalsIgnoreCase(t)))
            .count();

        return (double) temasCoherentes / temas.size();
    }

    public List<Usuario> recomendarEnComunidad(Usuario usuario, List<Comunidad> comunidades, int limite) {
        Comunidad comunidadUsuario = comunidades.stream()
                .filter(c -> c.contieneUsuario(usuario))
                .findFirst()
                .orElse(null);

        if (comunidadUsuario == null) {
            return new ArrayList<>();
        }

        return recomendarUsuarios(usuario, limite);
    }

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
