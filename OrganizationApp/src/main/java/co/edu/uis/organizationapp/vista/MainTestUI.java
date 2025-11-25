package co.edu.uis.organizationapp.vista;

import co.edu.uis.organizationapp.modelo.CarreraManager;
import co.edu.uis.organizationapp.modelo.TemaManager;
import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.UsuarioManager;
import co.edu.uis.organizationapp.modelo.DataBase_Prueba;
import co.edu.uis.organizationapp.modelo.comunidades.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Set;

public class MainTestUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TemaManager temaManager = new TemaManager();
            temaManager.cargarTemas("resources/temas_uis.json");
            String[] temasSugeridos = temaManager.getTemas().toArray(new String[0]);
            
            JFrame frame = new JFrame("Prueba de Funcionalidades UIS - Sistema de Recomendaciones");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1600, 900);
            frame.setLayout(new BorderLayout());

            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(JTabbedPane.TOP);

            List<Usuario> usuariosPrueba = DataBase_Prueba.generarUsuarios(25);
            List<Comunidad> comunidadesPrueba = DataBase_Prueba.generarComunidades(usuariosPrueba, 73);
            
            Usuario usuario = UsuarioManager.cargarUsuario();
            if (usuario.getTemas().isEmpty()) {
                usuario.agregarTema("Programación");
                usuario.agregarTema("Inteligencia Artificial");
                usuario.agregarTema("Bases de Datos");
            }
            if (usuario.getCarrera() == null || usuario.getCarrera().isEmpty()) {
                usuario.setCarrera("Ingeniería de Sistemas");
            }
            
            CarreraManager carreraManager = new CarreraManager();
            carreraManager.cargarCarreras("resources/carreras_uis.json");
            JPanel panelUsuario = new JPanel();
            panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.Y_AXIS));
            
            JPanel panelInfo = new JPanel(new GridLayout(3, 1, 5, 5));
            panelInfo.setBorder(BorderFactory.createTitledBorder("Información"));
            panelInfo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            JLabel lblNombre = new JLabel("Nombre: " + usuario.getNombre());
            JLabel lblPuntos = new JLabel("Puntos: " + usuario.getPuntos());
            JLabel lblCarrera = new JLabel("Carrera actual: " + (usuario.getCarrera() != null ? usuario.getCarrera() : "No seleccionada"));
            panelInfo.add(lblNombre);
            panelInfo.add(lblPuntos);
            panelInfo.add(lblCarrera);
            panelUsuario.add(panelInfo);

            JPanel panelCarrera = new JPanel(new BorderLayout(5, 5));
            panelCarrera.setBorder(BorderFactory.createTitledBorder("Cambiar Carrera"));
            panelCarrera.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
            JComboBox<String> comboCarrera = new JComboBox<>(carreraManager.getCarreras().toArray(new String[0]));
            comboCarrera.setSelectedItem(usuario.getCarrera());
            comboCarrera.addActionListener(e -> {
                String seleccion = (String) comboCarrera.getSelectedItem();
                usuario.setCarrera(seleccion);
                lblCarrera.setText("Carrera actual: " + seleccion);
            });
            JButton btnRefreshCarreras = new JButton("Refrescar");
            btnRefreshCarreras.addActionListener(e -> {
                carreraManager.cargarCarreras("resources/carreras_uis.json");
                comboCarrera.setModel(new DefaultComboBoxModel<>(carreraManager.getCarreras().toArray(new String[0])));
                comboCarrera.setSelectedItem(usuario.getCarrera());
            });
            panelCarrera.add(comboCarrera, BorderLayout.CENTER);
            panelCarrera.add(btnRefreshCarreras, BorderLayout.EAST);
            panelUsuario.add(panelCarrera);

            JPanel panelTemasCompleto = new JPanel(new BorderLayout(10, 10));
            panelTemasCompleto.setBorder(BorderFactory.createTitledBorder("GESTIÓN DE TEMAS/INTERESES"));
            panelTemasCompleto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));
            
            JPanel panelListaTemas = new JPanel(new BorderLayout(5, 5));
            panelListaTemas.setBorder(BorderFactory.createTitledBorder("Mis Temas Actuales"));
            DefaultListModel<String> modeloTemas = new DefaultListModel<>();
            Set<String> temasUsuario = usuario.getTemas();
            if (temasUsuario != null) {
                for (String t : temasUsuario) modeloTemas.addElement(t);
            }
            JList<String> listaTemas = new JList<>(modeloTemas);
            listaTemas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listaTemas.setFont(new Font("Arial", Font.PLAIN, 12));
            JScrollPane scrollTemas = new JScrollPane(listaTemas);
            scrollTemas.setPreferredSize(new Dimension(250, 300));
            panelListaTemas.add(scrollTemas, BorderLayout.CENTER);
            panelTemasCompleto.add(panelListaTemas, BorderLayout.WEST);
            
            JPanel panelSugerencias = new JPanel(new BorderLayout(5, 5));
            panelSugerencias.setBorder(BorderFactory.createTitledBorder("Temas Sugeridos (Haz clic para agregar)"));
            JPanel panelBotonesSugerencias = new JPanel();
            panelBotonesSugerencias.setLayout(new BoxLayout(panelBotonesSugerencias, BoxLayout.Y_AXIS));
            for (String sugerido : temasSugeridos) {
                JButton btnSugerido = new JButton(sugerido);
                btnSugerido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                btnSugerido.setFont(new Font("Arial", Font.PLAIN, 11));
                btnSugerido.setBackground(new Color(200, 220, 255));
                btnSugerido.setForeground(new Color(0, 0, 0));
                btnSugerido.addActionListener(e -> {
                    if (!modeloTemas.contains(sugerido)) {
                        usuario.agregarTema(sugerido);
                        modeloTemas.addElement(sugerido);
                        JOptionPane.showMessageDialog(frame, "✓ Tema '" + sugerido + "' agregado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(frame, "⚠️ Ya tienes este tema", "Duplicado", JOptionPane.WARNING_MESSAGE);
                    }
                });
                panelBotonesSugerencias.add(btnSugerido);
            }
            JScrollPane scrollSugerencias = new JScrollPane(panelBotonesSugerencias);
            scrollSugerencias.setPreferredSize(new Dimension(250, 300));
            panelSugerencias.add(scrollSugerencias, BorderLayout.CENTER);
            panelTemasCompleto.add(panelSugerencias, BorderLayout.CENTER);
            
            JPanel panelAccionesDerechas = new JPanel();
            panelAccionesDerechas.setLayout(new BoxLayout(panelAccionesDerechas, BoxLayout.Y_AXIS));
            panelAccionesDerechas.setBorder(BorderFactory.createTitledBorder("Acciones"));
            panelAccionesDerechas.setPreferredSize(new Dimension(200, 300));
            
            JPanel seccionAgregar = new JPanel(new BorderLayout(5, 5));
            seccionAgregar.setBorder(BorderFactory.createTitledBorder("Agregar Personalizado"));
            seccionAgregar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
            JLabel lblTemaPersonalizado = new JLabel("Escribe un tema:");
            JTextField txtTema = new JTextField(12);
            txtTema.setFont(new Font("Arial", Font.PLAIN, 12));
            JButton btnAgregarTema = new JButton("AGREGAR");
            btnAgregarTema.setFont(new Font("Arial", Font.BOLD, 12));
            btnAgregarTema.setBackground(new Color(76, 175, 80));
            btnAgregarTema.setForeground(Color.WHITE);
            btnAgregarTema.setPreferredSize(new Dimension(150, 40));
            seccionAgregar.add(lblTemaPersonalizado, BorderLayout.NORTH);
            seccionAgregar.add(txtTema, BorderLayout.CENTER);
            seccionAgregar.add(btnAgregarTema, BorderLayout.SOUTH);
            btnAgregarTema.addActionListener(e -> {
                String tema = txtTema.getText().trim();
                if (!tema.isEmpty() && !modeloTemas.contains(tema)) {
                    usuario.agregarTema(tema);
                    modeloTemas.addElement(tema);
                    txtTema.setText("");
                    JOptionPane.showMessageDialog(frame, "✓ Tema '" + tema + "' agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else if (modeloTemas.contains(tema)) {
                    JOptionPane.showMessageDialog(frame, "⚠️ El tema ya existe", "Duplicado", JOptionPane.WARNING_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠️ Campo vacío", "Error", JOptionPane.WARNING_MESSAGE);
                }
            });
            panelAccionesDerechas.add(seccionAgregar);
            panelAccionesDerechas.add(Box.createVerticalStrut(10));
            
            JPanel seccionEliminar = new JPanel(new BorderLayout(5, 5));
            seccionEliminar.setBorder(BorderFactory.createTitledBorder("Eliminar Seleccionado"));
            seccionEliminar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            JButton btnEliminarTema = new JButton("ELIMINAR");
            btnEliminarTema.setFont(new Font("Arial", Font.BOLD, 12));
            btnEliminarTema.setBackground(new Color(220, 100, 100));
            btnEliminarTema.setForeground(Color.WHITE);
            btnEliminarTema.setPreferredSize(new Dimension(150, 40));
            seccionEliminar.add(btnEliminarTema, BorderLayout.CENTER);
            btnEliminarTema.addActionListener(e -> {
                String tema = listaTemas.getSelectedValue();
                if (tema != null) {
                    int confirmacion = JOptionPane.showConfirmDialog(frame, 
                        "¿Eliminar '" + tema + "'?",
                        "Confirmar",
                        JOptionPane.YES_NO_OPTION);
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        usuario.eliminarTema(tema);
                        modeloTemas.removeElement(tema);
                        JOptionPane.showMessageDialog(frame, "✓ Tema eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "⚠️ Selecciona un tema primero", "Sin selección", JOptionPane.WARNING_MESSAGE);
                }
            });
            panelAccionesDerechas.add(seccionEliminar);
            panelAccionesDerechas.add(Box.createVerticalStrut(10));
            
            JPanel seccionLimpiar = new JPanel(new BorderLayout(5, 5));
            seccionLimpiar.setBorder(BorderFactory.createTitledBorder("Limpiar Todo"));
            seccionLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            JButton btnLimpiarTemas = new JButton("LIMPIAR TODO");
            btnLimpiarTemas.setFont(new Font("Arial", Font.BOLD, 12));
            btnLimpiarTemas.setBackground(new Color(180, 100, 180));
            btnLimpiarTemas.setForeground(Color.WHITE);
            btnLimpiarTemas.setPreferredSize(new Dimension(150, 40));
            seccionLimpiar.add(btnLimpiarTemas, BorderLayout.CENTER);
            btnLimpiarTemas.addActionListener(e -> {
                if (modeloTemas.size() == 0) {
                    JOptionPane.showMessageDialog(frame, "ℹ️ No hay temas", "Vacío", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                int confirmacion = JOptionPane.showConfirmDialog(frame, 
                    "¿ELIMINAR " + modeloTemas.size() + " TEMAS?\n(No se puede deshacer)",
                    "Confirmar",
                    JOptionPane.YES_NO_OPTION);
                if (confirmacion == JOptionPane.YES_OPTION) {
                    usuario.setTemas(new java.util.HashSet<>());
                    modeloTemas.clear();
                    JOptionPane.showMessageDialog(frame, "✓ Todos los temas eliminados", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            panelAccionesDerechas.add(seccionLimpiar);
            panelAccionesDerechas.add(Box.createVerticalGlue());
            
            panelTemasCompleto.add(panelAccionesDerechas, BorderLayout.EAST);
            panelUsuario.add(panelTemasCompleto);

            JPanel panelEstado = new JPanel(new BorderLayout(5, 5));
            panelEstado.setBorder(BorderFactory.createTitledBorder("Estado y Guardar"));
            panelEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            
            JLabel lblEstadoCarrera = new JLabel("Carrera: " + (usuario.getCarrera() != null && !usuario.getCarrera().isEmpty() ? usuario.getCarrera() : "No seleccionada"));
            JLabel lblEstadoTemas = new JLabel("Temas: " + modeloTemas.size());
            
            JPanel panelEstadoInfo = new JPanel(new GridLayout(1, 2, 5, 5));
            panelEstadoInfo.add(lblEstadoCarrera);
            panelEstadoInfo.add(lblEstadoTemas);
            panelEstado.add(panelEstadoInfo, BorderLayout.CENTER);
            
            JButton btnGuardarCambios = new JButton("GUARDAR CAMBIOS");
            btnGuardarCambios.setFont(new Font("Arial", Font.BOLD, 12));
            btnGuardarCambios.setBackground(new Color(76, 175, 80));
            btnGuardarCambios.setForeground(Color.WHITE);
            btnGuardarCambios.addActionListener(e -> {
                UsuarioManager.guardarUsuario(usuario);
                lblEstadoCarrera.setText("Carrera: " + (usuario.getCarrera() != null && !usuario.getCarrera().isEmpty() ? usuario.getCarrera() : "No seleccionada"));
                lblEstadoTemas.setText("Temas: " + modeloTemas.size() + " ✓");
                JOptionPane.showMessageDialog(frame, "¡Cambios guardados correctamente!\nCarrera: " + usuario.getCarrera() + "\nTemas: " + modeloTemas.size(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
            });
            panelEstado.add(btnGuardarCambios, BorderLayout.SOUTH);
            
            panelUsuario.add(panelEstado);
            
            JScrollPane scrollUsuario = new JScrollPane(panelUsuario);
            scrollUsuario.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            tabbedPane.addTab("👤 Usuario", scrollUsuario);

            ModeloComunidades modeloComunidades = new ModeloComunidades();
            modeloComunidades.setComunidades(comunidadesPrueba);

            JPanel panelComunidades = new JPanel(new BorderLayout());
            panelComunidades.setBorder(BorderFactory.createTitledBorder("Comunidades - Base de Datos de Prueba"));
            DefaultListModel<Comunidad> modeloLista = new DefaultListModel<>();
            for (Comunidad c : modeloComunidades.obtenerComunidades()) modeloLista.addElement(c);
            JList<Comunidad> listaComunidades = new JList<>(modeloLista);
            panelComunidades.add(new JScrollPane(listaComunidades), BorderLayout.CENTER);

            JPanel panelCrear = new JPanel(new FlowLayout());
            JTextField txtNombreComunidad = new JTextField(15);
            JButton btnCrearComunidad = new JButton("Crear comunidad");
            panelCrear.add(new JLabel("Nombre nueva comunidad:"));
            panelCrear.add(txtNombreComunidad);
            panelCrear.add(btnCrearComunidad);
            panelComunidades.add(panelCrear, BorderLayout.NORTH);

            btnCrearComunidad.addActionListener(e -> {
                String nombre = txtNombreComunidad.getText().trim();
                if (!nombre.isEmpty()) {
                    Comunidad nueva = modeloComunidades.crearComunidad(nombre, usuario);
                    modeloLista.addElement(nueva);
                    txtNombreComunidad.setText("");
                } else {
                    JOptionPane.showMessageDialog(frame, "El nombre de la comunidad no puede estar vacío.");
                }
            });

            JButton btnRecomendar = new JButton("Recomendar comunidades (Matemáticas Discretas)");
            btnRecomendar.setFont(new Font("Arial", Font.BOLD, 11));
            panelComunidades.add(btnRecomendar, BorderLayout.SOUTH);
            btnRecomendar.addActionListener(e -> {
                if (usuario.getTemas().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        "⚠️ Por favor, agrega al menos un tema de interés antes de buscar recomendaciones.",
                        "Información incompleta", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (usuario.getCarrera() == null || usuario.getCarrera().isEmpty()) {
                    JOptionPane.showMessageDialog(frame, 
                        "⚠️ Por favor, selecciona una carrera antes de buscar recomendaciones.",
                        "Información incompleta", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                GrafoSimilitud grafo = modeloComunidades.getGrafo();
                grafo.construir(modeloComunidades.obtenerUsuarios());
                DetectorComunidades detector = new DetectorComunidades(grafo);
                ServicioRecomendaciones servicio = new ServicioRecomendaciones(detector, grafo);
                
                double umbralMinimo = 0.30;
                Map<Comunidad, Double> recomendadasConPuntuacion = servicio.recomendarComunidadesConPuntuacion(
                    usuario, modeloComunidades.obtenerComunidades(), umbralMinimo);
                List<Comunidad> recomendadas = new ArrayList<>(recomendadasConPuntuacion.keySet());
                
                StringBuilder sb = new StringBuilder();
                sb.append("RECOMENDACIONES DINÁMICAS - MATEMÁTICAS DISCRETAS\n");
                sb.append("═════════════════════════════════════════════════\n\n");
                sb.append("Análisis basado en:\n");
                sb.append("  • Teoría de Conjuntos: Coeficiente de Jaccard\n");
                sb.append("  • Filtrado Dinámico: Umbral mínimo 30%\n");
                sb.append("  • Ponderación: Carrera(40% | MC:20%), Temas(30% | MC:50%), Coherencia(20%), Tamaño(10%)\n\n");
                sb.append("Perfil del usuario:\n");
                sb.append("  Carrera: ").append(usuario.getCarrera()).append("\n");
                sb.append("  Temas: ").append(usuario.getTemas().size()).append(" intereses\n\n");
                sb.append("═════════════════════════════════════════════════\n");
                sb.append("COMUNIDADES RECOMENDADAS:\n\n");
                
                if (recomendadas.isEmpty()) {
                    sb.append("  Ninguna comunidad disponible con suficiente similitud (>30%).\n");
                    sb.append("  Intenta crear nuevas comunidades o añadir más temas.\n");
                } else {
                    sb.append("  Total encontradas: ").append(recomendadas.size()).append("\n\n");
                    int rank = 1;
                    for (Comunidad com : recomendadas) {
                        double puntuacion = recomendadasConPuntuacion.getOrDefault(com, 0.0);
                        int percentaje = (int)(puntuacion * 100);
                        
                        // Barra de progreso visual
                        String barra = generarBarraProgreso(puntuacion);
                        
                        sb.append(rank).append(". ").append(com.getNombre()).append("\n");
                        sb.append("   ").append(barra).append(" ").append(percentaje).append("% relevancia\n");
                        sb.append("    Miembros: ").append(com.getNumMiembros()).append("\n");
                        sb.append("    Temas: ").append(com.getTemas().size()).append("\n");
                        if (!com.getDescripcion().isEmpty()) {
                            sb.append("    ").append(com.getDescripcion()).append("\n");
                        }
                        sb.append("\n");
                        rank++;
                    }
                }
                
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 10));
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(400, 300));
                
                JOptionPane.showMessageDialog(frame, scrollPane, "Recomendaciones de Comunidades", JOptionPane.INFORMATION_MESSAGE);
            });

            tabbedPane.addTab("Comunidades", panelComunidades);

            JPanel panelEstadisticas = new JPanel(new BorderLayout());
            panelEstadisticas.setBorder(BorderFactory.createTitledBorder("Estadísticas Base de Datos"));
            
            String resumenBD = DataBase_Prueba.generarResumen(usuariosPrueba, comunidadesPrueba);
            JTextArea textAreaEstadisticas = new JTextArea(resumenBD);
            textAreaEstadisticas.setEditable(false);
            textAreaEstadisticas.setFont(new Font("Monospaced", Font.PLAIN, 11));
            textAreaEstadisticas.setLineWrap(false);
            textAreaEstadisticas.setBackground(new Color(240, 240, 240));
            JScrollPane scrollEstadisticas = new JScrollPane(textAreaEstadisticas);
            panelEstadisticas.add(scrollEstadisticas, BorderLayout.CENTER);
            
            JPanel panelBotonesEstadisticas = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            
            JButton btnVerUsuarios = new JButton(" Ver usuarios de prueba");
            btnVerUsuarios.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                sb.append("USUARIOS DE PRUEBA (").append(usuariosPrueba.size()).append(" total)\n");
                sb.append("════════════════════════════════════════════════════\n\n");
                for (int i = 0; i < usuariosPrueba.size(); i++) {
                    Usuario u = usuariosPrueba.get(i);
                    sb.append((i + 1)).append(". ").append(u.getNombre()).append("\n");
                    sb.append("   Carrera: ").append(u.getCarrera()).append("\n");
                    sb.append("   Puntos: ").append(u.getPuntos()).append("\n");
                    sb.append("   Temas (").append(u.getTemas().size()).append("): ");
                    sb.append(String.join(", ", u.getTemas())).append("\n\n");
                }
                
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 9));
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(frame, scrollPane, "Usuarios de Prueba", JOptionPane.INFORMATION_MESSAGE);
            });
            panelBotonesEstadisticas.add(btnVerUsuarios);
            
            JButton btnVerComunidades = new JButton("Ver comunidades");
            btnVerComunidades.addActionListener(e -> {
                StringBuilder sb = new StringBuilder();
                sb.append("COMUNIDADES DE PRUEBA (").append(comunidadesPrueba.size()).append(" total)\n");
                sb.append("════════════════════════════════════════════════════\n\n");
                for (int i = 0; i < comunidadesPrueba.size(); i++) {
                    Comunidad c = comunidadesPrueba.get(i);
                    sb.append((i + 1)).append(". ").append(c.getNombre()).append("\n");
                    sb.append("   Creador: ").append(c.getCreador().getNombre()).append("\n");
                    sb.append("   Miembros: ").append(c.getNumMiembros()).append("\n");
                    sb.append("   Temas: ").append(String.join(", ", c.getTemas())).append("\n");
                    if (!c.getDescripcion().isEmpty()) {
                        sb.append("   Descripción: ").append(c.getDescripcion()).append("\n");
                    }
                    sb.append("\n");
                }
                
                JTextArea textArea = new JTextArea(sb.toString());
                textArea.setEditable(false);
                textArea.setFont(new Font("Monospaced", Font.PLAIN, 9));
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                scrollPane.setPreferredSize(new Dimension(600, 400));
                JOptionPane.showMessageDialog(frame, scrollPane, "Comunidades de Prueba", JOptionPane.INFORMATION_MESSAGE);
            });
            panelBotonesEstadisticas.add(btnVerComunidades);
            
            JButton btnRefrescarEstadisticas = new JButton("Refrescar");
            btnRefrescarEstadisticas.addActionListener(e -> {
                textAreaEstadisticas.setText(DataBase_Prueba.generarResumen(usuariosPrueba, comunidadesPrueba));
                textAreaEstadisticas.setCaretPosition(0);
            });
            panelBotonesEstadisticas.add(btnRefrescarEstadisticas);
            
            panelEstadisticas.add(panelBotonesEstadisticas, BorderLayout.SOUTH);
            
            tabbedPane.addTab(" Estadísticas", panelEstadisticas);

            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
    
    private static String generarBarraProgreso(double valor) {
        int longitud = 20;
        int llenos = (int) (valor * longitud);
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < longitud; i++) {
            barra.append(i < llenos ? "█" : "░");
        }
        barra.append("]");
        return barra.toString();
    }
}

