package co.edu.uis.organizationapp.ejemplo;

import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.comunidades.*;
import java.util.*;

/**
 * Ejemplo de uso del sistema de comunidades basado en Jaccard + Grafos.
 * Demuestra cómo crear usuarios con temas, detectar comunidades y obtener recomendaciones.
 */
public class EjemploComunidades {

    public static void main(String[] args) {
        System.out.println("=== Sistema de Comunidades - Ejemplo ===\n");

        // 1. Crear usuarios con temas de interés
        Usuario u1 = new Usuario("Alice");
        u1.agregarTema("Álgebra Lineal");
        u1.agregarTema("Matemáticas Discretas");
        u1.agregarTema("Programación");

        Usuario u2 = new Usuario("Bob");
        u2.agregarTema("Álgebra Lineal");
        u2.agregarTema("Cálculo");
        u2.agregarTema("Programación");

        Usuario u3 = new Usuario("Charlie");
        u3.agregarTema("Cálculo");
        u3.agregarTema("Análisis");

        Usuario u4 = new Usuario("Diana");
        u4.agregarTema("Álgebra Lineal");
        u4.agregarTema("Matemáticas Discretas");

        Usuario u5 = new Usuario("Eve");
        u5.agregarTema("Programación");
        u5.agregarTema("Bases de Datos");

        List<Usuario> usuarios = Arrays.asList(u1, u2, u3, u4, u5);

        // 2. Crear modelo de comunidades
        ModeloComunidades modelo = new ModeloComunidades();
        modelo.setUmbralSimilitud(0.3); // 30% de similitud mínima

        // 3. Calcular similitudes Jaccard
        System.out.println("--- Similitudes Jaccard ---");
        double sim_u1_u2 = CalculadorSimilitud.calcularJaccard(u1, u2);
        double sim_u1_u3 = CalculadorSimilitud.calcularJaccard(u1, u3);
        double sim_u1_u4 = CalculadorSimilitud.calcularJaccard(u1, u4);
        
        System.out.printf("Jaccard(Alice, Bob): %.2f\n", sim_u1_u2);
        System.out.printf("Jaccard(Alice, Charlie): %.2f\n", sim_u1_u3);
        System.out.printf("Jaccard(Alice, Diana): %.2f\n", sim_u1_u4);
        System.out.println();

        // 4. Detectar comunidades automáticamente (basadas en grafo de similitud)
        System.out.println("--- Detección de Comunidades ---");
        List<Set<Usuario>> comunidadesDetectadas = modelo.detectarComunidadesAutomaticamente(usuarios);
        
        for (int i = 0; i < comunidadesDetectadas.size(); i++) {
            Set<Usuario> comunidad = comunidadesDetectadas.get(i);
            System.out.print("Comunidad " + (i+1) + ": ");
            System.out.println(comunidad.stream().map(Usuario::getNombre).toArray());
        }
        System.out.println();

        // 5. Crear comunidades manuales
        System.out.println("--- Comunidades Manuales ---");
        Comunidad c1 = modelo.crearComunidad("Estudiantes de Álgebra", u1);
        c1.agregarTema("Álgebra Lineal");
        c1.agregarTema("Matemáticas Discretas");
        c1.agregarMiembro(u2);
        c1.agregarMiembro(u4);

        Comunidad c2 = modelo.crearComunidad("Programadores", u5);
        c2.agregarTema("Programación");
        c2.agregarTema("Bases de Datos");
        c2.agregarMiembro(u1);
        c2.agregarMiembro(u2);

        System.out.println("Creada comunidad: " + c1.getNombre());
        System.out.println("Creada comunidad: " + c2.getNombre());
        System.out.println();

        // 6. Obtener recomendaciones de comunidades
        System.out.println("--- Recomendaciones de Comunidades ---");
        Usuario usuario = u3; // Charlie
        List<Comunidad> recomendaciones = modelo.recomendarComunidades(usuario, 5);
        
        System.out.println("Recomendaciones para " + usuario.getNombre() + ":");
        for (Comunidad c : recomendaciones) {
            System.out.println("  - " + c.getNombre() + " (Miembros: " + c.getNumMiembros() + ")");
        }
        System.out.println();

        // 7. Obtener recomendaciones de usuarios similares
        System.out.println("--- Recomendaciones de Usuarios ---");
        List<Usuario> usuariosRecomendados = modelo.recomendarUsuarios(u1, 5);
        System.out.println("Usuarios similares a " + u1.getNombre() + ":");
        for (Usuario u : usuariosRecomendados) {
            System.out.println("  - " + u.getNombre());
        }
        System.out.println();

        // 8. Obtener estadísticas
        System.out.println("--- Estadísticas ---");
        Map<String, Double> stats = modelo.obtenerEstadisticasComunidades();
        System.out.printf("Total de comunidades detectadas: %.0f\n", stats.get("total"));
        System.out.printf("Tamaño promedio: %.2f\n", stats.get("tamaño_promedio"));
    }
}
