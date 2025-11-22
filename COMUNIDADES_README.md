# Sistema de Comunidades - Documentación

## Descripción General

El sistema de comunidades es un módulo de la aplicación OrganizationApp que detecta y recomienda comunidades de usuarios basándose en la similitud de sus temas/intereses académicos. Implementa conceptos de **Matemáticas Discretas y Teoría de Grafos**.

## Fundamentos Matemáticos

### 1. Similitud Jaccard
Se calcula la similitud entre dos usuarios usando el **coeficiente de Jaccard**:

```
Jaccard(u,v) = |temas_u ∩ temas_v| / |temas_u ∪ temas_v|
```

**Ejemplo:**
- Usuario A: {Álgebra, Programación, Cálculo}
- Usuario B: {Álgebra, Programación, Bases de Datos}
- Intersección: {Álgebra, Programación} = 2 elementos
- Unión: {Álgebra, Programación, Cálculo, Bases de Datos} = 4 elementos
- **Jaccard = 2/4 = 0.5** (50% similitud)

### 2. Grafo de Similitud
Se construye un grafo no dirigido donde:
- **Nodos**: Usuarios
- **Aristas**: Existen entre usuarios con Jaccard ≥ umbral (ej: 0.3)
- **Peso**: Valor Jaccard de similitud

### 3. Detección de Comunidades
Se usan **componentes conexas** (DFS) para identificar comunidades:
- Una comunidad es un conjunto máximo de usuarios conectados
- Se encuentra usando **búsqueda en profundidad (DFS)**

**Complejidad**: O(V + E) donde V = usuarios, E = conexiones

## Arquitectura de Clases

### Modelo
```
co.edu.uis.organizationapp.modelo.comunidades/
├── Comunidad.java           # Representa una comunidad
├── CalculadorSimilitud.java # Calcula Jaccard entre usuarios
├── GrafoSimilitud.java      # Construye grafo de similitud
├── DetectorComunidades.java # Detección por componentes conexas
├── ServicioRecomendaciones.java # Motor de recomendaciones
└── ModeloComunidades.java   # Gestor central

Persistencia:
├── ComunidadAdapters.java   # Adaptadores GSON
└── ComunidadesFormateador.java # Guardar/cargar JSON
```

### Usuario Mejorado
```java
public class Usuario {
    private String nombre;
    private int puntos;
    private Set<String> temas;  // NUEVO: temas/intereses
    
    public void agregarTema(String tema)
    public Set<String> getTemas()
}
```

## Uso del Sistema

### 1. Crear Usuarios con Temas
```java
Usuario usuario = new Usuario("Carlos");
usuario.agregarTema("Álgebra Lineal");
usuario.agregarTema("Matemáticas Discretas");
usuario.agregarTema("Programación");
```

### 2. Calcular Similitud Jaccard
```java
double similitud = CalculadorSimilitud.calcularJaccard(usuario1, usuario2);
// Retorna valor entre 0 y 1
```

### 3. Detectar Comunidades Automáticamente
```java
ModeloComunidades modelo = new ModeloComunidades();
modelo.setUmbralSimilitud(0.3); // 30% mínimo
List<Set<Usuario>> comunidades = modelo.detectarComunidadesAutomaticamente(usuarios);
```

### 4. Crear Comunidades Manuales
```java
Comunidad c = modelo.crearComunidad("Matemáticos", creador);
c.agregarTema("Álgebra Lineal");
c.agregarMiembro(usuario);
```

### 5. Obtener Recomendaciones
```java
// Recomendaciones de comunidades para un usuario
List<Comunidad> recomendadas = modelo.recomendarComunidades(usuario, 5);

// Usuarios similares
List<Usuario> usuariosSimilares = modelo.recomendarUsuarios(usuario, 5);

// Comunidades por temas
List<Comunidad> porTemas = modelo.obtenerComunidadesPorTemas(usuario);
```

## Persistencia

### Guardar Comunidades
```java
ComunidadesFormateador.guardarComunidades(comunidades, "comunidades_data.json");
```

### Cargar Comunidades
```java
List<Comunidad> comunidades = ComunidadesFormateador.cargarComunidades("comunidades_data.json");
```

**Automático**: Se guarda/carga al iniciar y cerrar la aplicación.

## Ejemplo Completo

Ver: `co.edu.uis.organizationapp.ejemplo.EjemploComunidades`

```bash
java -cp . co.edu.uis.organizationapp.ejemplo.EjemploComunidades
```

## Algoritmos Implementados

### 1. Cálculo de Jaccard
```
Entrada: usuario1, usuario2
Salida: similitud (0-1)

1. intersección = temas1 ∩ temas2
2. unión = temas1 ∪ temas2
3. jaccard = |intersección| / |unión|
4. return jaccard
```

### 2. Construcción del Grafo
```
Entrada: lista de usuarios, umbral
Salida: grafo de similitud

1. Para cada par (u1, u2):
   a. calcular jaccard(u1, u2)
   b. si jaccard >= umbral:
      - crear arista bidireccional
2. return grafo
```

### 3. Detección de Comunidades (DFS)
```
Entrada: grafo
Salida: lista de comunidades

1. visitados = {}
2. comunidades = []
3. Para cada nodo v en grafo:
   a. si v no visitado:
      - crear nueva comunidad
      - dfs(v, comunidad)
      - agregar comunidad a lista
4. return comunidades

DFS(nodo, comunidad):
  1. marcar nodo como visitado
  2. agregar nodo a comunidad
  3. para cada vecino de nodo:
     - si vecino no visitado: DFS(vecino, comunidad)
```

### 4. Recomendación de Comunidades
```
Entrada: usuario, lista de comunidades
Salida: comunidades ordenadas por relevancia

1. Para cada comunidad c:
   a. similitud = jaccard(temas_usuario, temas_c)
   b. puntuación = similitud * (1 + log(|miembros_c| + 1) / 10)
2. Ordenar por puntuación descendente
3. return top-k comunidades
```

## Complejidad Temporal

| Operación | Complejidad | Descripción |
|-----------|------------|-------------|
| Calcular Jaccard | O(n) | n = cantidad de temas |
| Construir Grafo | O(V² × n) | V = usuarios, n = temas promedio |
| Detectar Comunidades | O(V + E) | V = usuarios, E = aristas |
| Recomendaciones | O(V × M) | V = usuarios, M = comunidades |

## Parámetros Configurables

```java
modelo.setUmbralSimilitud(0.3);  // Mínimo Jaccard para conectar (0-1)
servicio.setUmbralRecomendacion(0.2); // Mínimo para recomendar usuario
```

## Relación con Matemáticas Discretas

- **Teoría de Grafos**: Grafo de similitud, componentes conexas
- **Combinatoria**: Cálculo de Jaccard (intersección/unión)
- **Búsqueda en Profundidad (DFS)**: Detección de comunidades
- **Teoría de Conjuntos**: Operaciones con temas (∩, ∪)

## Próximas Mejoras

1. UI en CalendarioDashboard para visualizar comunidades
2. Algoritmos avanzados: Louvain, Infomap
3. Métricas de evaluación: Precisión@k, Cobertura
4. Recomendaciones en tiempo real
5. Análisis de influenciadores en comunidades
