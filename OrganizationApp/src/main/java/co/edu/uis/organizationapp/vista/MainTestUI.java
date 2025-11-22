package co.edu.uis.organizationapp.vista;

import co.edu.uis.organizationapp.modelo.CarreraManager;
import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.UsuarioManager;
import co.edu.uis.organizationapp.modelo.comunidades.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Set;

public class MainTestUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Temas sugeridos por defecto
            String[] temasSugeridos = {
                "Cálculo", "Álgebra", "Geometría", "Estadística", "Física", "Química", "Biología", "Programación", "Electrónica", "Economía", "Filosofía", "Historia", "Literatura", "Derecho", "Psicología", "Sociología", "Administración", "Contabilidad", "Ingeniería", "Medicina", "Arquitectura", "Inglés", "Francés", "Alemán", "Ética", "Comunicación", "Marketing", "Educación", "Arte", "Música", "Diseño", "Ciencias Políticas", "Química Orgánica", "Química Inorgánica", "Microbiología", "Genética", "Termodinámica", "Electromagnetismo", "Inteligencia Artificial", "Bases de Datos", "Redes", "Sistemas Operativos"
            };
            JFrame frame = new JFrame("Prueba de Funcionalidades UIS");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 750);
            frame.setLayout(new BorderLayout());

            // Crear pestañas principal
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setTabPlacement(JTabbedPane.TOP);

            // Panel usuario
            Usuario usuario = UsuarioManager.cargarUsuario();
            CarreraManager carreraManager = new CarreraManager();
            carreraManager.cargarCarreras("resources/carreras_uis.json");
            JPanel panelUsuario = new JPanel();
            panelUsuario.setLayout(new BoxLayout(panelUsuario, BoxLayout.Y_AXIS));
            
            // Panel superior: Información del usuario
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

            // Panel de selección de carrera
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

            // Panel para temas/intereses del usuario
            JPanel panelTemas = new JPanel(new BorderLayout(5, 5));
            panelTemas.setBorder(BorderFactory.createTitledBorder("Temas/Intereses del Usuario"));
            panelTemas.setPreferredSize(new Dimension(Integer.MAX_VALUE, 250));
            DefaultListModel<String> modeloTemas = new DefaultListModel<>();
            Set<String> temasUsuario = usuario.getTemas();
            if (temasUsuario != null) {
                for (String t : temasUsuario) modeloTemas.addElement(t);
            }
            JList<String> listaTemas = new JList<>(modeloTemas);
            listaTemas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollTemas = new JScrollPane(listaTemas);
            panelTemas.add(scrollTemas, BorderLayout.CENTER);

            // Panel de gestión de temas compacto
            JPanel panelGestionTemas = new JPanel(new BorderLayout(5, 5));
            
            // Panel de sugerencias con scroll
            JPanel panelSugerencias = new JPanel();
            panelSugerencias.setLayout(new BoxLayout(panelSugerencias, BoxLayout.Y_AXIS));
            panelSugerencias.setBorder(BorderFactory.createTitledBorder("Sugerencias (haz clic)"));
            for (String sugerido : temasSugeridos) {
                JButton btnSugerido = new JButton(sugerido);
                btnSugerido.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));
                btnSugerido.setFont(new Font("Arial", Font.PLAIN, 10));
                btnSugerido.addActionListener(e -> {
                    if (!modeloTemas.contains(sugerido)) {
                        usuario.agregarTema(sugerido);
                        modeloTemas.addElement(sugerido);
                    }
                });
                panelSugerencias.add(btnSugerido);
            }
            JScrollPane scrollSugerencias = new JScrollPane(panelSugerencias);
            scrollSugerencias.setPreferredSize(new Dimension(180, 200));
            panelGestionTemas.add(scrollSugerencias, BorderLayout.WEST);

            // Panel de acciones (agregar, eliminar, guardar)
            JPanel panelAcciones = new JPanel(new GridLayout(0, 1, 3, 3));
            panelAcciones.setBorder(BorderFactory.createTitledBorder("Acciones"));
            
            JPanel panelAgregar = new JPanel(new BorderLayout(3, 3));
            JTextField txtTema = new JTextField(10);
            JButton btnAgregarTema = new JButton("Agregar");
            btnAgregarTema.setFont(new Font("Arial", Font.PLAIN, 10));
            panelAgregar.add(txtTema, BorderLayout.CENTER);
            panelAgregar.add(btnAgregarTema, BorderLayout.EAST);
            btnAgregarTema.addActionListener(e -> {
                String tema = txtTema.getText().trim();
                if (!tema.isEmpty() && !modeloTemas.contains(tema)) {
                    usuario.agregarTema(tema);
                    modeloTemas.addElement(tema);
                    txtTema.setText("");
                }
            });
            panelAcciones.add(panelAgregar);

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
            JButton btnEliminarTema = new JButton("Eliminar");
            btnEliminarTema.setFont(new Font("Arial", Font.PLAIN, 10));
            btnEliminarTema.addActionListener(e -> {
                String tema = listaTemas.getSelectedValue();
                if (tema != null) {
                    usuario.eliminarTema(tema);
                    modeloTemas.removeElement(tema);
                }
            });
            JButton btnLimpiarTemas = new JButton("Limpiar");
            btnLimpiarTemas.setFont(new Font("Arial", Font.PLAIN, 10));
            btnLimpiarTemas.addActionListener(e -> {
                usuario.setTemas(new java.util.HashSet<>());
                modeloTemas.clear();
            });
            panelBotones.add(btnEliminarTema);
            panelBotones.add(btnLimpiarTemas);
            panelAcciones.add(panelBotones);
            
            panelGestionTemas.add(panelAcciones, BorderLayout.CENTER);
            panelTemas.add(panelGestionTemas, BorderLayout.SOUTH);

            panelUsuario.add(panelTemas);

            // Panel de estado y guardado
            JPanel panelEstado = new JPanel(new BorderLayout(5, 5));
            panelEstado.setBorder(BorderFactory.createTitledBorder("Estado y Guardar"));
            panelEstado.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
            
            JLabel lblEstadoCarrera = new JLabel("Carrera: " + (usuario.getCarrera() != null && !usuario.getCarrera().isEmpty() ? usuario.getCarrera() : "No seleccionada"));
            JLabel lblEstadoTemas = new JLabel("Temas: " + modeloTemas.size());
            
            JPanel panelEstadoInfo = new JPanel(new GridLayout(1, 2, 5, 5));
            panelEstadoInfo.add(lblEstadoCarrera);
            panelEstadoInfo.add(lblEstadoTemas);
            panelEstado.add(panelEstadoInfo, BorderLayout.CENTER);
            
            JButton btnGuardarCambios = new JButton("💾 GUARDAR CAMBIOS");
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
            
            // Agregar scroll al panel de usuario si es necesario
            JScrollPane scrollUsuario = new JScrollPane(panelUsuario);
            scrollUsuario.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

            // Agregar panel de usuario como pestaña
            tabbedPane.addTab("👤 Usuario", scrollUsuario);

            // Panel comunidades
            ModeloComunidades modeloComunidades = new ModeloComunidades();
            // Comunidades por defecto: una por cada carrera
            List<Comunidad> comunidadesPorDefecto = new java.util.ArrayList<>();
            for (String carrera : carreraManager.getCarreras()) {
                Comunidad comunidadCarrera = new Comunidad(carrera, usuario); // El usuario actual como creador
                comunidadCarrera.setDescripcion("Comunidad de la carrera " + carrera);
                comunidadCarrera.agregarTema(carrera); // Asignar el nombre de la carrera como tema principal
                comunidadesPorDefecto.add(comunidadCarrera);
            }
            modeloComunidades.setComunidades(comunidadesPorDefecto);

            JPanel panelComunidades = new JPanel(new BorderLayout());
            panelComunidades.setBorder(BorderFactory.createTitledBorder("Comunidades"));
            DefaultListModel<Comunidad> modeloLista = new DefaultListModel<>();
            for (Comunidad c : modeloComunidades.obtenerComunidades()) modeloLista.addElement(c);
            JList<Comunidad> listaComunidades = new JList<>(modeloLista);
            panelComunidades.add(new JScrollPane(listaComunidades), BorderLayout.CENTER);

            // Panel para crear comunidad
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

            JButton btnRecomendar = new JButton("Recomendar comunidades");
            panelComunidades.add(btnRecomendar, BorderLayout.SOUTH);
            btnRecomendar.addActionListener(e -> {
                GrafoSimilitud grafo = modeloComunidades.getGrafo();
                grafo.construir(modeloComunidades.obtenerUsuarios());
                DetectorComunidades detector = new DetectorComunidades(grafo);
                ServicioRecomendaciones servicio = new ServicioRecomendaciones(detector, grafo);
                List<Comunidad> recomendadas = servicio.recomendarComunidades(usuario, modeloComunidades.obtenerComunidades(), 5);
                StringBuilder sb = new StringBuilder();
                for (Comunidad com : recomendadas) {
                    sb.append(com.getNombre()).append("\n");
                }
                JOptionPane.showMessageDialog(frame, "Recomendadas:\n" + sb.toString());
            });

            // Agregar panel de comunidades como pestaña
            tabbedPane.addTab("👥 Comunidades", panelComunidades);

            // Agregar tabbedPane al frame
            frame.add(tabbedPane, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
