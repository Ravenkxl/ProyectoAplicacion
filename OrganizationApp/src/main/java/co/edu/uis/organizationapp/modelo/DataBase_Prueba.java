package co.edu.uis.organizationapp.modelo;

import co.edu.uis.organizationapp.modelo.comunidades.Comunidad;
import java.util.*;

/**
 * Base de datos de prueba para TestUI.
 * Genera un conjunto realista de usuarios, comunidades y temas
 * para probar el sistema de recomendaciones basado en Matemáticas Discretas.
 * 
 * Conceptos aplicados:
 * - Teoría de Conjuntos: Distribución de temas entre usuarios
 * - Teoría de Grafos: Conectividad entre usuarios a través de comunidades
 * - Probabilidad: Distribución realista de intereses
 */
public class DataBase_Prueba {
    
    // Temas disponibles agrupados por área
    private static final String[] TEMAS_PROGRAMACION = {
        "Programación", "Python", "Java", "C++", "JavaScript", "C#", "Go", "Rust",
        "Sistemas Operativos", "Administración de Sistemas", "Linux", "Windows Server",
        "Bases de Datos", "SQL", "MongoDB", "PostgreSQL", "MySQL", "NoSQL",
        "Diseño de Aplicaciones", "Arquitectura de Software", "Microservicios",
        "Patrones de Diseño", "SOLID", "Clean Code", "Testing", "Unit Testing", "Integration Testing",
        "Control de Versiones", "Git", "GitHub", "GitLab", "Bitbucket",
        "Docker", "Kubernetes", "Contenedores", "DevOps", "CI/CD", "Jenkins", "GitLab CI",
        "Desarrollo Web", "Frontend", "Backend", "Full Stack", "React", "Angular", "Vue",
        "API REST", "GraphQL", "WebSockets", "Seguridad Web", "OAuth", "JWT",
        "Cloud Computing", "AWS", "Azure", "Google Cloud", "Serverless", "Lambda"
    };
    
    private static final String[] TEMAS_IA_DATOS = {
        "Inteligencia Artificial", "Machine Learning", "Deep Learning", "Redes Neuronales",
        "TensorFlow", "PyTorch", "Keras", "Scikit-learn",
        "Análisis de Datos", "Big Data", "Data Science", "Ciencia de Datos",
        "Estadística", "Probabilidad", "Inferencia Estadística", "Análisis Exploratorio",
        "Procesamiento de Imágenes", "Visión por Computadora", "Reconocimiento de Patrones",
        "NLP", "Procesamiento de Lenguaje Natural", "Text Mining", "Análisis de Sentimientos",
        "Minería de Datos", "Predicción", "Clasificación", "Clustering", "Segmentación",
        "Pandas", "NumPy", "Matplotlib", "Seaborn", "Tableau", "Power BI"
    };
    
    private static final String[] TEMAS_INFRAESTRUCTURA = {
        "Redes", "TCP/IP", "OSI", "Protocolos de Red", "DNS", "DHCP", "BGP", "OSPF",
        "Ciberseguridad", "Seguridad Informática", "Firewall", "VPN", "Proxy", "WAF",
        "Encriptación", "SSL/TLS", "PKI", "Certificados Digitales", "Autenticación",
        "Servidores", "Administración de Servidores", "Load Balancing", "Clustering",
        "Cloud Computing", "AWS", "Azure", "Google Cloud", "OpenStack",
        "Virtualización", "Máquinas Virtuales", "Hipervisores", "VMware", "Hyper-V",
        "Contenedores", "Docker", "Kubernetes", "Orquestación de Contenedores",
        "DevOps", "CI/CD", "Infrastructure as Code", "Terraform", "Ansible",
        "Monitoreo", "Logging", "Observabilidad", "Prometheus", "ELK Stack", "Grafana",
        "Backup y Recuperación", "Disaster Recovery", "Business Continuity", "RAID"
    };
    
    private static final String[] TEMAS_INGENIERIA = {
        "Física", "Mecánica Clásica", "Mecánica de Fluidos", "Termodinámica",
        "Electromagnetismo", "Campos Eléctricos", "Campos Magnéticos", "Ondas Electromagnéticas",
        "Circuitos", "Análisis de Circuitos", "Teoría de Circuitos", "Leyes de Kirchhoff",
        "Electrónica", "Electrónica Digital", "Electrónica Analógica", "Semiconductores",
        "Microcontroladores", "Arduino", "Raspberry Pi", "PLC", "FPGA",
        "Automatización", "Control Automático", "Sistemas de Control", "PID",
        "Señales", "Procesamiento de Señales", "Transformada de Fourier", "Filtros Digitales",
        "Ingeniería Eléctrica", "Máquinas Eléctricas", "Transformadores", "Motores",
        "Ingeniería Electrónica", "Diseño de Circuitos", "PCB", "Diseño de Hardware",
        "Potencia Eléctrica", "Distribución Eléctrica", "Subestaciones", "Calidad de Energía",
        "Ingeniería Mecánica", "CAD", "Modelado 3D", "AutoCAD", "SolidWorks",
        "Resistencia de Materiales", "Análisis de Esfuerzos", "Dinámica", "Cinemática"
    };
    
    private static final String[] TEMAS_MATEMATICAS = {
        "Cálculo", "Cálculo Diferencial", "Cálculo Integral", "Cálculo Multivariado",
        "Álgebra", "Álgebra Lineal", "Matrices", "Sistemas de Ecuaciones Lineales",
        "Geometría", "Geometría Analítica", "Geometría Descriptiva", "Topología",
        "Estadística", "Estadística Descriptiva", "Estadística Inferencial", "Regresión",
        "Probabilidad", "Variables Aleatorias", "Distribuciones", "Teorema del Límite Central",
        "Análisis Matemático", "Ecuaciones Diferenciales", "EDP", "Cálculo de Variaciones",
        "Matemáticas Discretas", "Combinatoria", "Teoría de Grafos", "Lógica Matemática",
        "Teoría de Números", "Números Primos", "Aritmética Modular", "Criptografía",
        "Trigonometría", "Funciones Trigonométricas", "Identidades Trigonométricas"
    };

    
    private static final String[] CARRERAS = {
        "Ingeniería de Sistemas", "Ingeniería Eléctrica", "Ingeniería Electrónica",
        "Ingeniería Industrial", "Ingeniería Mecánica"
    };
    
    private static final String[] NOMBRES = {
        "Ana García", "Carlos López", "María Rodríguez", "Juan Martínez", "Sofia Pérez",
        "Diego Sánchez", "Laura Fernández", "Roberto Gómez", "Valentina Morales", "Miguel Ángel Ruiz",
        "Isabella Ortiz", "Fernando Duque", "Camila Reyes", "Andrés Vargas", "Gabriela Torres",
        "Lucas Herrera", "Natalia Silva", "Javier Castillo", "Alejandra Rojas", "Pablo Núñez",
        "Daniela Quintero", "Mateo Parra", "Victoria Salazar", "Cristian Medina", "Elena Fuentes",
        "Álvaro Jiménez", "Mariana Ibáñez", "Ricardo Vidal", "Francisca Campos", "Eduardo Flores",
        "Paulina Guerrero", "Ignacio Muñoz", "Verónica Navas", "Guillermo Orozco", "Antonia Pineda"
    };
    
    /**
     * Genera una lista de usuarios de prueba con perfiles variados.
     * @param cantidad Número de usuarios a generar
     * @return Lista de usuarios configurados
     */
    public static List<Usuario> generarUsuarios(int cantidad) {
        List<Usuario> usuarios = new ArrayList<>();
        Random random = new Random(42); // Seed fijo para reproducibilidad
        
        for (int i = 0; i < cantidad; i++) {
            String nombre = NOMBRES[i % NOMBRES.length];
            if (i >= NOMBRES.length) {
                nombre += " " + (i / NOMBRES.length);
            }
            
            Usuario usuario = new Usuario(nombre);
            usuario.setPuntos(50 + random.nextInt(300));
            
            // Asignar carrera aleatoria
            usuario.setCarrera(CARRERAS[random.nextInt(CARRERAS.length)]);
            
            // Asignar temas según el perfil
            asignarTemasSegunPerfil(usuario, random);
            
            usuarios.add(usuario);
        }
        
        return usuarios;
    }
    
    /**
     * Asigna temas a un usuario según su carrera y perfil.
     */
    private static void asignarTemasSegunPerfil(Usuario usuario, Random random) {
        String carrera = usuario.getCarrera();
        Set<String> temasAsignados = new HashSet<>();
        
        // Asignar temas principales según carrera
        switch (carrera) {
            case "Ingeniería de Sistemas":
            case "Ciencias de la Computación":
                // Temas principales: Programación
                agregarTemasAleatorios(temasAsignados, TEMAS_PROGRAMACION, 4, 6, random);
                // Temas secundarios: IA o Infraestructura
                if (random.nextBoolean()) {
                    agregarTemasAleatorios(temasAsignados, TEMAS_IA_DATOS, 2, 4, random);
                } else {
                    agregarTemasAleatorios(temasAsignados, TEMAS_INFRAESTRUCTURA, 2, 3, random);
                }
                break;
                
            case "Ingeniería Eléctrica":
                // Temas principales: Ingeniería
                agregarTemasAleatorios(temasAsignados, TEMAS_INGENIERIA, 5, 8, random);
                // Temas secundarios: Matemáticas
                agregarTemasAleatorios(temasAsignados, TEMAS_MATEMATICAS, 2, 3, random);
                break;
                
            case "Ingeniería Electrónica":
                // Temas principales: Ingeniería
                agregarTemasAleatorios(temasAsignados, TEMAS_INGENIERIA, 4, 6, random);
                // Temas secundarios: Programación o IA
                if (random.nextBoolean()) {
                    agregarTemasAleatorios(temasAsignados, TEMAS_PROGRAMACION, 2, 3, random);
                } else {
                    agregarTemasAleatorios(temasAsignados, TEMAS_IA_DATOS, 1, 2, random);
                }
                break;
                
            case "Matemáticas":
                // Temas principales: Matemáticas
                agregarTemasAleatorios(temasAsignados, TEMAS_MATEMATICAS, 6, 8, random);
                // Temas secundarios: Programación o IA
                if (random.nextBoolean()) {
                    agregarTemasAleatorios(temasAsignados, TEMAS_PROGRAMACION, 2, 3, random);
                } else {
                    agregarTemasAleatorios(temasAsignados, TEMAS_IA_DATOS, 2, 3, random);
                }
                break;
                
            case "Física":
                // Temas principales: Ingeniería e IA
                agregarTemasAleatorios(temasAsignados, TEMAS_INGENIERIA, 3, 5, random);
                agregarTemasAleatorios(temasAsignados, TEMAS_MATEMATICAS, 3, 4, random);
                break;
                
            default:
                // Tema genérico
                agregarTemasAleatorios(temasAsignados, TEMAS_PROGRAMACION, 2, 4, random);
                break;
        }
        
        // Agregar los temas al usuario
        for (String tema : temasAsignados) {
            usuario.agregarTema(tema);
        }
    }
    
    /**
     * Agrega una cantidad aleatoria de temas a un conjunto.
     */
    private static void agregarTemasAleatorios(Set<String> conjunto, String[] temas, 
                                               int minimo, int maximo, Random random) {
        int cantidad = minimo + random.nextInt(maximo - minimo + 1);
        Set<Integer> indices = new HashSet<>();
        
        while (indices.size() < Math.min(cantidad, temas.length)) {
            indices.add(random.nextInt(temas.length));
        }
        
        for (int idx : indices) {
            conjunto.add(temas[idx]);
        }
    }
    
    /**
     * Genera comunidades de prueba basadas en usuarios.
     * MEJORADO: Crea comunidades específicas por carrera para garantizar
     * que las recomendaciones sean coherentes.
     * 
     * @param usuarios Lista de usuarios de los cuales seleccionar creadores
     * @param cantidadComunidades Número de comunidades a crear
     * @return Lista de comunidades configuradas
     */
    public static List<Comunidad> generarComunidades(List<Usuario> usuarios, int cantidadComunidades) {
        List<Comunidad> comunidades = new ArrayList<>();
        Random random = new Random(42);
        
        // Definir comunidades específicas por carrera - EXPANDIDO
        Map<String, String[]> comunidadesPorCarrera = new HashMap<>();
        
        // Comunidades de Ingeniería de Sistemas (12 comunidades)
        comunidadesPorCarrera.put("Ingeniería de Sistemas", new String[]{
            "Comunidad de Programación Avanzada",
            "Club de Python y Desarrollo Backend",
            "Comunidad DevOps e Infraestructura",
            "Comunidad Cloud Computing y AWS",
            "Red de Desarrollo Web Full Stack",
            "Foro de Bases de Datos SQL y NoSQL",
            "Comunidad de Ciberseguridad y Seguridad Web",
            "Red de Machine Learning y Análisis de Datos",
            "Foro de Arquitectura de Software",
            "Comunidad Docker y Kubernetes",
            "Red de Testing y Quality Assurance",
            "Foro de Git y Control de Versiones"
        });
        
        // Comunidades de Ingeniería Eléctrica (11 comunidades)
        comunidadesPorCarrera.put("Ingeniería Eléctrica", new String[]{
            "Foro de Ingeniería Eléctrica Aplicada",
            "Grupo de Sistemas de Potencia y Distribución",
            "Comunidad de Máquinas Eléctricas y Motores",
            "Red de Electromagnetismo y Campos",
            "Foro de Análisis de Circuitos",
            "Comunidad de Automatización Eléctrica",
            "Red de Subestaciones y Control",
            "Foro de Electrónica de Potencia",
            "Comunidad de Transformadores y Generadores",
            "Red de Calidad de Energía",
            "Foro de Simulación de Sistemas Eléctricos"
        });
        
        // Comunidades de Ingeniería Electrónica (10 comunidades)
        comunidadesPorCarrera.put("Ingeniería Electrónica", new String[]{
            "Grupo de Electrónica Digital y Diseño",
            "Foro de Microcontroladores y Arduino",
            "Comunidad de Procesamiento de Señales",
            "Red de Sistemas Embebidos",
            "Foro de Circuitos Electrónicos",
            "Comunidad FPGA y Lógica Programable",
            "Red de Diseño de PCB",
            "Foro de Semiconductores y Componentes",
            "Comunidad de Sensores y Actuadores",
            "Red de VHDL y Verilog"
        });
        
        // Comunidades de Ingeniería Industrial (9 comunidades)
        comunidadesPorCarrera.put("Ingeniería Industrial", new String[]{
            "Red de Optimización y Investigación Operativa",
            "Foro de Logística y Cadena de Suministro",
            "Comunidad de Calidad y Mejora Continua",
            "Red de Estadística Industrial y Control",
            "Comunidad de Automatización de Procesos",
            "Foro de Producción Lean y Six Sigma",
            "Red de Gestión de Inventario",
            "Comunidad de Análisis de Datos Industrial",
            "Foro de Modelado de Sistemas"
        });
        
        // Comunidades de Ingeniería Mecánica (10 comunidades)
        comunidadesPorCarrera.put("Ingeniería Mecánica", new String[]{
            "Foro de CAD y Diseño Mecánico",
            "Comunidad de Análisis de Esfuerzos y FEA",
            "Red de Termodinámica y Motores",
            "Foro de Mecánica de Fluidos",
            "Comunidad de Manufactura y CNC",
            "Red de Materiales y Propiedades",
            "Foro de Simulación Mecánica ANSYS",
            "Comunidad de Diseño Automotriz",
            "Red de Automatización Mecánica",
            "Foro de Tribología y Lubricación"
        });
        
        // Comunidades de Materias Comunes - Transversales a todas las Ingenierías
        // Estas comunidades son accesibles por estudiantes de TODAS las carreras
        String[] materiasComunesNames = {
            // Matemáticas (8 comunidades)
            "Foro de Cálculo Diferencial e Integral",
            "Comunidad de Álgebra Lineal y Matrices",
            "Red de Ecuaciones Diferenciales",
            "Foro de Análisis Matemático",
            "Comunidad de Geometría y Trigonometría",
            "Red de Matemáticas Discretas y Grafos",
            "Foro de Combinatoria y Lógica Matemática",
            "Comunidad de Cálculo Multivariado",
            
            // Física (6 comunidades)
            "Foro de Física Clásica y Mecánica",
            "Comunidad de Electromagnetismo",
            "Red de Termodinámica y Ondas",
            "Foro de Óptica y Acústica",
            "Comunidad de Física Experimental",
            "Red de Análisis de Fenómenos Físicos",
            
            // Estadística y Probabilidad (4 comunidades)
            "Foro de Probabilidad y Variables Aleatorias",
            "Comunidad de Estadística Descriptiva e Inferencial",
            "Red de Análisis de Datos y Correlación",
            "Foro de Modelos Estadísticos",
            
            // Programación General (3 comunidades)
            "Comunidad de Programación Estructurada",
            "Foro de Algoritmos y Estructuras de Datos",
            "Red de Programación Orientada a Objetos"
        };
        
        // Agregar comunidades de materias comunes para cada carrera
        comunidadesPorCarrera.put("Materias Comunes", materiasComunesNames);
        
        int indicesComunidad = 0;
        
        // Crear comunidades para cada carrera - TODAS LAS POSIBLES
        for (String carrera : comunidadesPorCarrera.keySet()) {
            String[] nombresComunidad = comunidadesPorCarrera.get(carrera);
            
            // Encontrar usuarios con esta carrera
            List<Usuario> usuariosParaComunidad = new ArrayList<>();
            
            if (carrera.equals("Materias Comunes")) {
                // Para materias comunes, agregar usuarios de TODAS las carreras
                usuariosParaComunidad.addAll(usuarios);
            } else {
                // Para carreras específicas, solo usuarios de esa carrera
                for (Usuario u : usuarios) {
                    if (carrera.equalsIgnoreCase(u.getCarrera())) {
                        usuariosParaComunidad.add(u);
                    }
                }
            }
            
            if (usuariosParaComunidad.isEmpty()) continue;
            
            // Crear TODAS las comunidades posibles de esta carrera
            for (int j = 0; j < nombresComunidad.length; j++) {
                if (indicesComunidad >= cantidadComunidades) break;
                
                Usuario creador = usuariosParaComunidad.get(j % usuariosParaComunidad.size());
                Comunidad comunidad = new Comunidad(nombresComunidad[j], creador);
                
                // Asignar descripción
                String desc = carrera.equals("Materias Comunes") 
                    ? "Comunidad para estudiantes de todas las Ingenierías"
                    : "Comunidad especializada de " + carrera;
                comunidad.setDescripcion(desc);
                
                if (carrera.equals("Materias Comunes")) {
                    asignarTemasMateriasComunes(comunidad, nombresComunidad[j], random);
                } else {
                    // Asignar temas relevantes DEL CREADOR (garantiza coherencia)
                    asignarTemasComunidad(comunidad, creador.getTemas(), random);
                }
                
                // Agregar miembros
                int numMiembros = carrera.equals("Materias Comunes") 
                    ? 4 + random.nextInt(5)  // 4-8 miembros para materias comunes (diverso)
                    : 3 + random.nextInt(4); // 3-6 miembros para carreras específicas
                
                Set<Integer> indicesAgregados = new HashSet<>();
                indicesAgregados.add(usuariosParaComunidad.indexOf(creador));
                
                while (indicesAgregados.size() < Math.min(numMiembros + 1, usuariosParaComunidad.size())) {
                    int idx = random.nextInt(usuariosParaComunidad.size());
                    Usuario miembro = usuariosParaComunidad.get(idx);
                    
                    if (!comunidad.contieneUsuario(miembro)) {
                        comunidad.agregarMiembro(miembro);
                        indicesAgregados.add(idx);
                    }
                }
                
                comunidades.add(comunidad);
                indicesComunidad++;
            }
        }
        
        return comunidades;
    }
    
    /**
     * Asigna temas a una comunidad basándose en los temas del creador.
     */
    private static void asignarTemasComunidad(Comunidad comunidad, Set<String> temasCreador, Random random) {
        // Agregar algunos temas del creador
        List<String> temasList = new ArrayList<>(temasCreador);
        Collections.shuffle(temasList);
        
        int temasAAdd = Math.min(3, temasList.size());
        for (int i = 0; i < temasAAdd; i++) {
            comunidad.agregarTema(temasList.get(i));
        }
    }
    
    private static void asignarTemasMateriasComunes(Comunidad comunidad, String nombreComunidad, Random random) {
        List<String> exactos = temasDeterministicosMateriasComunes(nombreComunidad);
        if (!exactos.isEmpty()) {
            for (String t : exactos) comunidad.agregarTema(t);
            return;
        }

        String lower = nombreComunidad.toLowerCase(Locale.ROOT);
        List<String> pool = new ArrayList<>();
        if (lower.contains("cálculo") || lower.contains("calculo") || lower.contains("álgebra") || lower.contains("algebra")
                || lower.contains("ecuaciones") || lower.contains("matem") || lower.contains("geometr")
                || lower.contains("combinatoria") || lower.contains("lógica") || lower.contains("logica")) {
            pool = Arrays.asList(TEMAS_MATEMATICAS);
        } else if (lower.contains("física") || lower.contains("fisica") || lower.contains("electromagnet")
                || lower.contains("óptica") || lower.contains("optica") || lower.contains("acústica") || lower.contains("acustica")
                || lower.contains("ondas") || lower.contains("termodinámica") || lower.contains("termodinamica")) {
            pool = filtrar(TEMAS_INGENIERIA, "física", "fisica", "mecánica", "mecanica", "termodin", "electromagnet", "fluidos", "ondas");
        } else if (lower.contains("estadística") || lower.contains("estadistica") || lower.contains("probabilidad")
                || lower.contains("variables") || lower.contains("modelos") || lower.contains("correlación") || lower.contains("correlacion")) {
            pool = filtrar(TEMAS_MATEMATICAS, "estad", "probab", "variables", "distribu", "regresión", "regresion");
        } else if (lower.contains("programación") || lower.contains("programacion") || lower.contains("algoritmos")
                || lower.contains("estructuras de datos") || lower.contains("orientada a objetos")) {
            pool = filtrar(TEMAS_PROGRAMACION, "programación", "programacion", "java", "python", "c++", "c#", "go", "rust", "backend", "frontend", "api rest", "testing");
            pool.removeIf(s -> {
                String ls = s.toLowerCase(Locale.ROOT);
                return ls.contains("full stack") || ls.contains("angular") || ls.contains("react") || ls.contains("vue");
            });
        }
        if (pool.isEmpty()) {
            pool = Arrays.asList(TEMAS_MATEMATICAS);
        }
        agregarTemasDesdePool(comunidad, pool, 3, 4, random);
    }
    
    private static List<String> filtrar(String[] arr, String... palabras) {
        List<String> res = new ArrayList<>();
        for (String s : arr) {
            String ls = s.toLowerCase(Locale.ROOT);
            for (String p : palabras) {
                if (ls.contains(p)) { res.add(s); break; }
            }
        }
        return res;
    }
    
    private static void agregarTemasDesdePool(Comunidad comunidad, List<String> pool, int minimo, int maximo, Random random) {
        if (pool == null || pool.isEmpty()) return;
        int cantidad = minimo + random.nextInt(Math.max(1, maximo - minimo + 1));
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < Math.min(cantidad, pool.size())) {
            indices.add(random.nextInt(pool.size()));
        }
        for (int idx : indices) {
            comunidad.agregarTema(pool.get(idx));
        }
    }
    
    private static List<String> temasDeterministicosMateriasComunes(String nombre) {
        String n = nombre.trim().toLowerCase(Locale.ROOT);
        List<String> r = new ArrayList<>();
        switch (n) {
            case "foro de cálculo diferencial e integral":
                r = Arrays.asList("Cálculo", "Cálculo Diferencial", "Cálculo Integral", "Cálculo Multivariado");
                break;
            case "comunidad de álgebra lineal y matrices":
            case "comunidad de algebra lineal y matrices":
                r = Arrays.asList("Álgebra", "Álgebra Lineal", "Matrices", "Sistemas de Ecuaciones Lineales");
                break;
            case "red de ecuaciones diferenciales":
                r = Arrays.asList("Ecuaciones Diferenciales", "Cálculo", "Cálculo Diferencial");
                break;
            case "foro de análisis matemático":
            case "foro de analisis matemático":
            case "foro de analisis matematico":
                r = Arrays.asList("Análisis Matemático", "Cálculo", "Cálculo Integral");
                break;
            case "comunidad de geometría y trigonometría":
            case "comunidad de geometria y trigonometria":
                r = Arrays.asList("Geometría", "Geometría Analítica", "Trigonometría");
                break;
            case "red de matemáticas discretas y grafos":
            case "red de matematicas discretas y grafos":
                r = Arrays.asList("Matemáticas Discretas", "Teoría de Grafos", "Combinatoria", "Lógica Matemática");
                break;
            case "foro de combinatoria y lógica matemática":
            case "foro de combinatoria y logica matematica":
                r = Arrays.asList("Combinatoria", "Lógica Matemática", "Matemáticas Discretas");
                break;
            case "comunidad de cálculo multivariado":
            case "comunidad de calculo multivariado":
                r = Arrays.asList("Cálculo Multivariado", "Cálculo", "Álgebra Lineal");
                break;

            case "foro de física clásica y mecánica":
            case "foro de fisica clasica y mecanica":
                r = Arrays.asList("Física", "Mecánica Clásica", "Dinámica", "Cinemática");
                break;
            case "comunidad de electromagnetismo":
                r = Arrays.asList("Electromagnetismo", "Campos Eléctricos", "Campos Magnéticos");
                break;
            case "red de termodinámica y ondas":
            case "red de termodinamica y ondas":
                r = Arrays.asList("Termodinámica", "Ondas", "Física");
                break;
            case "foro de óptica y acústica":
            case "foro de optica y acustica":
                r = Arrays.asList("Física", "Ondas");
                break;
            case "comunidad de física experimental":
            case "comunidad de fisica experimental":
                r = Arrays.asList("Física");
                break;
            case "red de análisis de fenómenos físicos":
            case "red de analisis de fenomenos fisicos":
                r = Arrays.asList("Física", "Mecánica Clásica", "Termodinámica");
                break;

            case "foro de probabilidad y variables aleatorias":
                r = Arrays.asList("Probabilidad", "Variables Aleatorias", "Distribuciones");
                break;
            case "comunidad de estadística descriptiva e inferencial":
            case "comunidad de estadistica descriptiva e inferencial":
                r = Arrays.asList("Estadística", "Estadística Descriptiva", "Estadística Inferencial");
                break;
            case "red de análisis de datos y correlación":
            case "red de analisis de datos y correlacion":
                r = Arrays.asList("Análisis de Datos", "Estadística", "Probabilidad");
                break;
            case "foro de modelos estadísticos":
            case "foro de modelos estadisticos":
                r = Arrays.asList("Estadística", "Probabilidad");
                break;

            case "comunidad de programación estructurada":
            case "comunidad de programacion estructurada":
                r = Arrays.asList("Programación", "Algoritmos");
                break;
            case "foro de algoritmos y estructuras de datos":
                r = Arrays.asList("Algoritmos", "Programación");
                break;
            case "red de programación orientada a objetos":
            case "red de programacion orientada a objetos":
                r = Arrays.asList("Programación", "Algoritmos");
                break;
        }
        return r;
    }
    
    /**
     * Genera un resumen estadístico de los usuarios y comunidades.
     */
    public static String generarResumen(List<Usuario> usuarios, List<Comunidad> comunidades) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════════════════════╗\n");
        sb.append("║     RESUMEN BASE DE DATOS DE PRUEBA - MATEMÁTICAS      ║\n");
        sb.append("║                    DISCRETAS                           ║\n");
        sb.append("╚════════════════════════════════════════════════════════╝\n\n");
        
        sb.append("📊 ESTADÍSTICAS GENERALES:\n");
        sb.append("  • Usuarios totales: ").append(usuarios.size()).append("\n");
        sb.append("  • Comunidades totales: ").append(comunidades.size()).append("\n\n");
        
        // Estadísticas por carrera
        Map<String, Integer> usuariosPorCarrera = new HashMap<>();
        for (Usuario u : usuarios) {
            usuariosPorCarrera.put(u.getCarrera(), usuariosPorCarrera.getOrDefault(u.getCarrera(), 0) + 1);
        }
        
        sb.append("🏫 USUARIOS POR CARRERA:\n");
        usuariosPorCarrera.forEach((carrera, count) -> 
            sb.append("  • ").append(carrera).append(": ").append(count).append("\n")
        );
        sb.append("\n");
        
        // Temas más populares
        Map<String, Integer> frecuenciaTemas = new HashMap<>();
        for (Usuario u : usuarios) {
            for (String tema : u.getTemas()) {
                frecuenciaTemas.put(tema, frecuenciaTemas.getOrDefault(tema, 0) + 1);
            }
        }
        
        sb.append("📚 TOP 10 TEMAS MÁS POPULARES:\n");
        frecuenciaTemas.entrySet().stream()
            .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
            .limit(10)
            .forEach(e -> sb.append("  • ").append(e.getKey()).append(": ").append(e.getValue()).append(" usuarios\n"));
        sb.append("\n");
        
        // Estadísticas de comunidades
        int totalMiembros = 0;
        int maxMiembros = 0;
        int minMiembros = Integer.MAX_VALUE;
        for (Comunidad c : comunidades) {
            int numMiembros = c.getNumMiembros();
            totalMiembros += numMiembros;
            maxMiembros = Math.max(maxMiembros, numMiembros);
            minMiembros = Math.min(minMiembros, numMiembros);
        }
        
        double promMiembros = comunidades.isEmpty() ? 0 : (double) totalMiembros / comunidades.size();
        
        sb.append("👥 ESTADÍSTICAS DE COMUNIDADES:\n");
        sb.append("  • Miembros promedio por comunidad: ").append(String.format("%.2f", promMiembros)).append("\n");
        sb.append("  • Máximo de miembros: ").append(maxMiembros).append("\n");
        sb.append("  • Mínimo de miembros: ").append(minMiembros).append("\n");
        sb.append("  • Total de membresías: ").append(totalMiembros).append("\n\n");
        
        // Aplicación de conceptos de Matemáticas Discretas
        sb.append("🔬 CONCEPTOS DE MATEMÁTICAS DISCRETAS APLICADOS:\n");
        sb.append("  ✓ Teoría de Conjuntos: Intersección/Unión de temas\n");
        sb.append("  ✓ Coeficiente de Jaccard: Similitud entre usuarios\n");
        sb.append("  ✓ Teoría de Grafos: Conectividad en comunidades\n");
        sb.append("  ✓ Análisis Combinatorio: Distribución de perfiles\n");
        
        return sb.toString();
    }
}
