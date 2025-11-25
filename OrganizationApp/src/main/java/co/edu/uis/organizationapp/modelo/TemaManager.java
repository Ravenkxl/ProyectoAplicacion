package co.edu.uis.organizationapp.modelo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.FileReader;
import java.util.*;

/**
 * Gestor de temas para cargar temas desde JSON.
 * Incluye relación de temas por carrera para Matemáticas Discretas.
 * 
 * Carga dos fuentes:
 * 1. temas_uis.json: Lista de todos los temas disponibles
 * 2. carrera_temas_mapa.json: MAPEO EXPLÍCITO de Carrera → Temas (FUENTE DE VERDAD)
 */
public class TemaManager {
    private List<String> temas = new ArrayList<>();
    
    // Mapeo de carrera a temas esperados (DESDE JSON, no hardcodeado)
    private Map<String, Set<String>> temasEsperadosPorCarrera = new HashMap<>();

    public TemaManager() {
        cargarMapeoCarrerasTemas("resources/carrera_temas_mapa.json");
    }

    /**
     * Carga la lista de temas desde temas_uis.json
     */
    public void cargarTemas(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Gson gson = new Gson();
            String[] arr = gson.fromJson(reader, String[].class);
            temas = Arrays.asList(arr);
        } catch (Exception e) {
            temas = new ArrayList<>();
        }
    }

    /**
     * Carga el MAPEO EXPLÍCITO de Carreras → Temas desde carrera_temas_mapa.json
     * 
     * 📄 ARCHIVO JSON EXTERNO (NO HARDCODEADO):
     * resources/carrera_temas_mapa.json
     * 
     * FUENTE DE VERDAD para la relación carrera-tema.
     * Cada carrera tiene 3 categorías de temas:
     *   - temas_core: Temas obligatorios de la carrera
     *   - temas_relacionados: Temas electivos/especializaciones
     *   - temas_basicos: Temas comunes a todas las ingenierías
     * 
     * Esto responde a tu pregunta:
     * "¿Dónde está la relación entre carreras y temas?"
     * ✅ AQUÍ: En carrera_temas_mapa.json (mapeo explícito en JSON)
     */
    private void cargarMapeoCarrerasTemas(String rutaArchivo) {
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            // Iterar sobre cada carrera en el JSON
            for (String carrera : json.keySet()) {
                JsonObject carrJson = json.getAsJsonObject(carrera);
                Set<String> temasCompletos = new HashSet<>();

                // Agregar temas_core
                if (carrJson.has("temas_core")) {
                    for (var tema : carrJson.getAsJsonArray("temas_core")) {
                        temasCompletos.add(tema.getAsString());
                    }
                }

                // Agregar temas_relacionados
                if (carrJson.has("temas_relacionados")) {
                    for (var tema : carrJson.getAsJsonArray("temas_relacionados")) {
                        temasCompletos.add(tema.getAsString());
                    }
                }

                // Agregar temas_basicos (comunes)
                if (carrJson.has("temas_basicos")) {
                    for (var tema : carrJson.getAsJsonArray("temas_basicos")) {
                        temasCompletos.add(tema.getAsString());
                    }
                }

                temasEsperadosPorCarrera.put(carrera, temasCompletos);
            }
        } catch (Exception e) {
            // Si hay error, inicializa con valores por defecto
            System.err.println("Error al cargar carrera_temas_mapa.json: " + e.getMessage());
            inicializarTemasEsperadosPorDefecto();
        }
    }

    /**
     * Inicializa valores por defecto si no se puede cargar carrera_temas_mapa.json
     * FALLBACK: Si el JSON no está disponible, usa estos valores hardcodeados
     */
    private void inicializarTemasEsperadosPorDefecto() {
        temasEsperadosPorCarrera.put("Ingeniería de Sistemas", new HashSet<>(
            Arrays.asList(
                "Programación", "Python", "Java", "C++", "JavaScript", "C#", "Go", "Rust",
                "Sistemas Operativos", "Linux", "Windows Server",
                "Bases de Datos", "SQL", "MongoDB", "PostgreSQL", "MySQL", "NoSQL",
                "Desarrollo Web", "Frontend", "Backend", "React", "Angular", "Vue",
                "API REST", "Seguridad Web", "Ciberseguridad",
                "Arquitectura de Software", "Microservicios", "Patrones de Diseño",
                "DevOps", "Docker", "Kubernetes", "CI/CD", "Cloud Computing", "AWS", "Azure",
                "Inteligencia Artificial", "Machine Learning", "Deep Learning",
                "Control de Versiones", "Git", "GitHub",
                "Testing", "Algoritmos", "Matemáticas Discretas", "Teoría de Grafos"
            )
        ));
        
        temasEsperadosPorCarrera.put("Ingeniería Eléctrica", new HashSet<>(
            Arrays.asList(
                "Circuitos", "Análisis de Circuitos", "Teoría de Circuitos",
                "Electromagnetismo", "Campos Eléctricos", "Campos Magnéticos",
                "Potencia Eléctrica", "Distribución Eléctrica", "Máquinas Eléctricas",
                "Transformadores", "Motores", "Generadores",
                "Subestaciones", "Calidad de Energía",
                "Sistemas de Control", "Automatización", "Control Automático",
                "Procesamiento de Señales", "Transformada de Fourier",
                "Electrónica", "Semiconductores",
                "Simulación MATLAB", "Programación", "Análisis de Datos",
                "Física", "Mecánica Clásica", "Cálculo", "Álgebra Lineal",
                "Ecuaciones Diferenciales", "Estadística"
            )
        ));
        
        temasEsperadosPorCarrera.put("Ingeniería Electrónica", new HashSet<>(
            Arrays.asList(
                "Electrónica", "Electrónica Digital", "Electrónica Analógica",
                "Circuitos", "Análisis de Circuitos", "Diseño de Circuitos", "PCB",
                "Microcontroladores", "Arduino", "Raspberry Pi", "FPGA",
                "Sistemas Embebidos", "Firmware", "Hardware",
                "Programación", "C", "C++", "Assembly", "VHDL", "Verilog",
                "Automatización", "Control Automático", "Sistemas de Control",
                "Procesamiento de Señales", "Sensores", "Actuadores",
                "Comunicaciones", "Protocolos",
                "Física", "Electromagnetismo", "Campos Eléctricos",
                "Cálculo", "Álgebra", "Álgebra Lineal", "Ecuaciones Diferenciales",
                "Estadística", "Probabilidad"
            )
        ));
        
        temasEsperadosPorCarrera.put("Ingeniería Industrial", new HashSet<>(
            Arrays.asList(
                "Estadística", "Estadística Descriptiva", "Estadística Inferencial",
                "Probabilidad", "Análisis de Datos", "Predicción",
                "Optimización", "Programación Lineal", "Algoritmos de Optimización",
                "Investigación Operativa", "Teoría de Grafos",
                "Calidad", "Control de Calidad", "Six Sigma", "Lean",
                "Logística", "Cadena de Suministro", "Gestión de Inventario",
                "Producción", "Procesos Productivos", "Manufactura",
                "Mejora Continua", "Eficiencia", "Productividad",
                "Gestión de Proyectos", "Programación", "Python", "R", "Excel",
                "Cálculo", "Álgebra", "Álgebra Lineal", "Ecuaciones Diferenciales",
                "Física", "Termodinámica", "Matemáticas Discretas"
            )
        ));
        
        temasEsperadosPorCarrera.put("Ingeniería Mecánica", new HashSet<>(
            Arrays.asList(
                "Mecánica Clásica", "Estática", "Dinámica", "Cinemática",
                "Resistencia de Materiales", "Propiedades de Materiales",
                "Termodinámica", "Transferencia de Calor", "Combustión",
                "Mecánica de Fluidos", "Dinámicas de Fluidos", "Hidráulica",
                "CAD", "Diseño Asistido por Computadora", "Modelado 3D",
                "FEA", "Análisis de Elementos Finitos", "Simulación",
                "Manufactura", "Procesos de Fabricación", "CNC", "Metrología",
                "Automatización", "Robots Industriales", "Sistemas Mecatrónicos",
                "Control Automático", "Sistemas de Control", "PID",
                "Cálculo", "Cálculo Diferencial", "Cálculo Integral",
                "Álgebra", "Álgebra Lineal", "Ecuaciones Diferenciales",
                "Física", "Electromagnetismo", "Estadística", "Probabilidad"
            )
        ));
    }

    public List<String> getTemas() {
        return temas;
    }

    /**
     * Obtiene todos los temas válidos para una carrera específica.
     * Desde: carrera_temas_mapa.json (MAPEO EXPLÍCITO)
     * 
     * @param carrera Nombre de la carrera
     * @return Set con todos los temas válidos para esa carrera
     */
    public Set<String> getTemasEsperados(String carrera) {
        return temasEsperadosPorCarrera.getOrDefault(carrera, new HashSet<>());
    }

    /**
     * Obtiene el mapeo completo Carrera → Temas
     * Útil para depuración y análisis
     * 
     * @return Map con todas las relaciones carrera-tema
     */
    public Map<String, Set<String>> getTemasEsperadosPorCarrera() {
        return temasEsperadosPorCarrera;
    }

    /**
     * Valida si un tema es válido para una carrera específica.
     * 
     * Matemáticas Discretas: Validación de membresía en conjunto
     * tema ∈ temasEsperados(carrera)?
     * 
     * @param carrera Nombre de la carrera
     * @param tema Nombre del tema
     * @return true si el tema es válido para esa carrera
     */
    public boolean esTemaValido(String carrera, String tema) {
        Set<String> temasValidos = getTemasEsperados(carrera);
        return temasValidos.contains(tema);
    }

    /**
     * Obtiene los temas comunes entre una carrera y un conjunto de temas.
     * Teoría de Conjuntos: Intersección (∩)
     * 
     * @param carrera Nombre de la carrera
     * @param temasUsuario Set con los temas del usuario
     * @return Intersección de temas: temasCarrera ∩ temasUsuario
     */
    public Set<String> obtenerTemasValidosDelUsuario(String carrera, Set<String> temasUsuario) {
        Set<String> temasCarrera = getTemasEsperados(carrera);
        Set<String> interseccion = new HashSet<>(temasCarrera);
        interseccion.retainAll(temasUsuario);  // ∩
        return interseccion;
    }
}
