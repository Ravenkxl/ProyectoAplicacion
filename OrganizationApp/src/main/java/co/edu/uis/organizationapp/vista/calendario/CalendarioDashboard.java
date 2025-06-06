package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.*;
import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.UsuarioManager;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import com.formdev.flatlaf.FlatLightLaf;

public class CalendarioDashboard extends JFrame {
    private ModeloCalendario modelo;
    private VistaPanelMensual vistaMensual;
    private VistaPanelSemanal vistaSemanal;
    private VistaPanelDiaria vistaDiaria;
    private VistaPanelAnual vistaAnual;
    private JTabbedPane tabbedPane;
    private JList<Evento> eventList;
    private DefaultListModel<Evento> eventListModel;
    private JLabel lblFechaSeleccionada;
    private LocalDate fechaSeleccionada;
    private JTextArea txtDetalles;

    // Search components
    private JTextField buscadorField;
    private JWindow popupWindow;
    private DefaultListModel<Evento> modeloBusqueda;

    // Toolbar
    private JToolBar toolBar;

    // --- TAREAS Y SUBTAREAS ---
    private DefaultListModel<Object> modeloTareasYSubtareas;
    private JList<Object> listaTareasYSubtareas;
    private JButton btnAgregarTarea, btnEliminarTarea, btnAgregarSubtarea;
    private Evento eventoActual;

    private Usuario usuario;
    private JLabel lblPuntosUsuario;

    // Constructor and initial setup
    public CalendarioDashboard() {
        setupLookAndFeel();
        setupWindow();
        initializeModels();
        usuario = UsuarioManager.cargarUsuario();
        initializeComponents();
        setupLayout();
        loadInitialData();
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
          
        }
    }

    private void setupWindow() {
        setTitle("Calendario");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
    }

    private void initializeModels() {
        modelo = new ModeloCalendario();
        fechaSeleccionada = LocalDate.now();
        
        eventListModel = new DefaultListModel<>();
        modeloBusqueda = new DefaultListModel<>();
    }

    private void initializeComponents() {
        setupLists();
        setupCalendarViews();
    }

    private void setupLists() {
        eventList = new JList<>(eventListModel);
        
        setupEventList();
    }

    private void setupEventList() {
        eventList.setCellRenderer(new RenderizadorDeListaDeEventos());
        eventList.addListSelectionListener(_ -> {
            Evento eventoSeleccionado = eventList.getSelectedValue();
            if (eventoSeleccionado != null) {
                mostrarDetallesEvento(eventoSeleccionado);
                actualizarVistaTareas(eventoSeleccionado);
                listaTareasYSubtareas.repaint();
            }
        });
    }

    private void setupCalendarViews() {
        vistaDiaria = new VistaPanelDiaria(modelo);
        vistaSemanal = new VistaPanelSemanal(modelo);
        vistaMensual = new VistaPanelMensual(modelo);
        vistaAnual = new VistaPanelAnual(modelo);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Día", vistaDiaria);
        tabbedPane.addTab("Semana", vistaSemanal);
        tabbedPane.addTab("Mes", vistaMensual);
        tabbedPane.addTab("Año", vistaAnual);

        tabbedPane.addChangeListener(_ -> actualizarVistaSeleccionada());
    }

    private void actualizarVistaSeleccionada() {
        int selectedIndex = tabbedPane.getSelectedIndex();
        switch (selectedIndex) {
            case 0 -> {
                fechaSeleccionada = vistaDiaria.getDiaActual();
                actualizarListaEventos(fechaSeleccionada);
            }
            case 1 -> {
                LocalDate inicioSemana = vistaSemanal.getSemanaActual();
                fechaSeleccionada = inicioSemana;
                actualizarListaEventos(fechaSeleccionada);
            }
            case 2 -> {
                YearMonth mes = vistaMensual.getMesActual();
                fechaSeleccionada = mes.atDay(1);
                actualizarListaEventos(fechaSeleccionada);
            }
            case 3 -> {
                Year año = vistaAnual.getAñoActual();
                fechaSeleccionada = año.atDay(1);
                actualizarListaEventos(fechaSeleccionada);
            }
        }
        actualizarFechaSeleccionada();
    }

    private void setupLayout() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation((int)(getWidth() * 0.8));
        splitPane.setResizeWeight(0.8);

        splitPane.setLeftComponent(tabbedPane);
        
        JPanel panelDerechoSplit = createRightPanel();
        splitPane.setRightComponent(panelDerechoSplit);
        
        add(createToolbar(), BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createRightPanel() {
        JPanel panelDerechoSplit = new JPanel();
        panelDerechoSplit.setPreferredSize(new Dimension(280, 0));
        panelDerechoSplit.setLayout(new BorderLayout(0, 10));
        panelDerechoSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel vertical para tareas y puntos
        JPanel panelTareasYPuntos = new JPanel();
        panelTareasYPuntos.setLayout(new BoxLayout(panelTareasYPuntos, BoxLayout.Y_AXIS));
        panelTareasYPuntos.add(createTareasPanel());
        lblPuntosUsuario = new JLabel("Puntos: " + usuario.getPuntos());
        lblPuntosUsuario.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblPuntosUsuario.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPuntosUsuario.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        panelTareasYPuntos.add(lblPuntosUsuario);

        // Add day info panel
        panelDerechoSplit.add(createDateInfoPanel(), BorderLayout.NORTH);
        // Add events panel
        panelDerechoSplit.add(createEventsPanel(), BorderLayout.CENTER);
        // Add tasks and points panel
        panelDerechoSplit.add(panelTareasYPuntos, BorderLayout.SOUTH);
        return panelDerechoSplit;
    }

    private JPanel createDateInfoPanel() {
        JPanel panelInfoDia = new JPanel(new BorderLayout());
        panelInfoDia.setBorder(BorderFactory.createTitledBorder("Día seleccionado"));
        lblFechaSeleccionada = new JLabel("No hay día seleccionado");
        lblFechaSeleccionada.setHorizontalAlignment(SwingConstants.CENTER);
        panelInfoDia.add(lblFechaSeleccionada, BorderLayout.CENTER);
        return panelInfoDia;
    }

    private JPanel createEventsPanel() {
        JPanel panelEventos = new JPanel(new BorderLayout());
        panelEventos.setBorder(BorderFactory.createTitledBorder("Eventos"));
        
        JScrollPane scrollEventos = new JScrollPane(eventList);
        panelEventos.add(scrollEventos, BorderLayout.CENTER);
        panelEventos.add(createEventButtonsPanel(), BorderLayout.SOUTH);
        
        return panelEventos;
    }

    private JPanel createTareasPanel() {
        JPanel panelTareas = new JPanel(new BorderLayout());
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas y subtareas del evento"));
        modeloTareasYSubtareas = new DefaultListModel<>();
        listaTareasYSubtareas = new JList<>(modeloTareasYSubtareas);
        listaTareasYSubtareas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTareasYSubtareas.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tarea tarea) {
                    String fecha = tarea.getFecha() != null ? tarea.getFecha().toString() : "";
                    String hora = tarea.getInicio() != null ? tarea.getInicio().toString() : "";
                    String horaFin = tarea.getFin() != null ? tarea.getFin().toString() : "";
                    boolean vencida = false;
                    boolean completada = tarea.isCompletada();
                    // Determinar si está vencida
                    if (!completada && tarea.getFecha() != null && tarea.getFin() != null) {
                        LocalDateTime fin = LocalDateTime.of(tarea.getFecha(), tarea.getFin());
                        if (LocalDateTime.now().isAfter(fin)) {
                            vencida = true;
                        }
                    }
                    String texto = "\u25A0 " + tarea.getTitulo() + (fecha.isEmpty() ? "" : "  [" + fecha + (hora.isEmpty() ? "" : (" " + hora)) + (horaFin.isEmpty() ? "" : (" - " + horaFin)) + "]");
                    if (vencida) {
                        texto += "  (vencida)";
                        label.setForeground(Color.RED);
                    } else if (completada) {
                        label.setForeground(new Color(0, 153, 0));
                    } else {
                        label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                    }
                    label.setText(texto);
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                    label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 2));
                } else if (value instanceof Subtarea subtarea) {
                    label.setText("    \u25CB " + subtarea.getTitulo());
                    label.setFont(label.getFont().deriveFont(Font.PLAIN));
                    label.setBorder(BorderFactory.createEmptyBorder(2, 32, 2, 2));
                    if (subtarea.isCompletada()) {
                        label.setForeground(new Color(0, 153, 0));
                    } else {
                        label.setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
                    }
                }
                return label;
            }
        });
        // Asegurarse de que el menú contextual se asocie correctamente
        setupTareaListContextMenu();
        listaTareasYSubtareas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    Object sel = listaTareasYSubtareas.getSelectedValue();
                    if (sel instanceof Tarea tarea) {
                        editarTarea(tarea);
                    } else if (sel instanceof Subtarea subtarea) {
                        editarSubtarea(subtarea);
                    }
                }
            }
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = listaTareasYSubtareas.locationToIndex(e.getPoint());
                    listaTareasYSubtareas.setSelectedIndex(row);
                    listaTareasYSubtareas.getComponentPopupMenu().show(listaTareasYSubtareas, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = listaTareasYSubtareas.locationToIndex(e.getPoint());
                    listaTareasYSubtareas.setSelectedIndex(row);
                    listaTareasYSubtareas.getComponentPopupMenu().show(listaTareasYSubtareas, e.getX(), e.getY());
                }
            }
        });
        JScrollPane scrollTareas = new JScrollPane(listaTareasYSubtareas);
        panelTareas.add(scrollTareas, BorderLayout.CENTER);
        panelTareas.add(createTareasButtonsPanel(), BorderLayout.SOUTH);
        return panelTareas;
    }

    private JPanel createTareasButtonsPanel() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        btnAgregarTarea = new JButton("Agregar tarea");
        btnAgregarTarea.addActionListener(_ -> agregarTarea());
        btnAgregarSubtarea = new JButton("Agregar subtarea");
        btnAgregarSubtarea.addActionListener(_ -> agregarSubtarea());
        btnEliminarTarea = new JButton("Eliminar");
        btnEliminarTarea.addActionListener(_ -> eliminarTareaOSubtarea());
        panelBotones.add(btnAgregarTarea);
        panelBotones.add(btnAgregarSubtarea);
        panelBotones.add(btnEliminarTarea);
        return panelBotones;
    }

    private JPanel createEventButtonsPanel() {
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        
        setupEventButtons(btnAgregar, btnEliminar);
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        
        return panelBotones;
    }

    private void setupEventButtons(JButton btnAgregar, JButton btnEliminar) {
        btnAgregar.addActionListener(_ -> {
            EventoDialogo dialogo = new EventoDialogo(this, modelo, fechaSeleccionada, false);
            dialogo.setVisible(true);
            if (dialogo.fueModificado()) {
                actualizarListaEventos(fechaSeleccionada);
                actualizarVistasCalendario();
            }
        });

        btnEliminar.addActionListener(_ -> {
            Evento eventoSeleccionado = eventList.getSelectedValue();
            if (eventoSeleccionado != null) {
                if (confirmarEliminacionEvento()) {
                    modelo.eliminarEvento(eventoSeleccionado);
                    actualizarListaEventos(fechaSeleccionada);
                }
            } else {
                mostrarMensajeSeleccionarEvento();
            }
        });
    }

    private boolean confirmarEliminacionEvento() {
        return JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar este evento?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    private void mostrarMensajeSeleccionarEvento() {
        JOptionPane.showMessageDialog(this,
            "Seleccione un evento para eliminar",
            "Aviso",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarVistasCalendario() {
        switch (tabbedPane.getSelectedIndex()) {
            case 0 -> vistaDiaria.establecerDia(fechaSeleccionada);
            case 1 -> vistaSemanal.establecerSemana(fechaSeleccionada);
            case 2 -> vistaMensual.setMonth(YearMonth.from(fechaSeleccionada));
            case 3 -> vistaAnual.establecerAño(Year.from(fechaSeleccionada));
        }
        actualizarInterfaz();
    }

    // Actualizar la etiqueta de fecha seleccionada
    private void actualizarFechaSeleccionada() {
        if (lblFechaSeleccionada != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM 'de' yyyy")
                .withLocale(Locale.forLanguageTag("es-ES"));
            String fecha = fechaSeleccionada.format(formatter);
            lblFechaSeleccionada.setText(fecha);
            actualizarTitulo(formatearTituloDiario(fechaSeleccionada));
        }
    }

    // Helper method to update all calendar views
    private void actualizarInterfaz() {
        actualizarFechaSeleccionada();
        actualizarListaEventos(fechaSeleccionada);
        repaint();
    }

    public void actualizarListaEventos(LocalDate fecha) {
        if (fecha != null && modelo != null) {
            eventListModel.clear();
            for (Evento evento : modelo.getEventos(fecha)) {
                eventListModel.addElement(evento);
            }
        }
    }

    // Format and display utilities
    public void mostrarDetallesEvento(Evento evento) {
        this.eventoActual = evento;
        if (evento != null && txtDetalles != null) {
            StringBuilder detalles = new StringBuilder();
            detalles.append("Título: ").append(evento.getTitulo()).append("\n");
            detalles.append("Fecha: ").append(
                evento.getFecha().format(
                    DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", 
                    Locale.forLanguageTag("es-ES"))
                )
            ).append("\n");
            detalles.append("Descripción: ").append(evento.getDescripcion());
            txtDetalles.setText(detalles.toString());
            actualizarVistaTareas(evento);
        } else {
            if (txtDetalles != null) {
                txtDetalles.setText("");
            }
        }
    }

    // Helper method for task updates
    private void actualizarVistaTareas(Evento evento) {
        modeloTareasYSubtareas.clear();
        if (evento != null && evento.getTareas() != null) {
            for (Tarea tarea : evento.getTareas()) {
                modeloTareasYSubtareas.addElement(tarea);
                if (tarea.getSubtareas() != null) {
                    for (Subtarea subtarea : tarea.getSubtareas()) {
                        modeloTareasYSubtareas.addElement(subtarea);
                    }
                }
            }
        }
    }

    // Date formatting methods
    private String formatearTituloDiario(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-ES")));
    }

    private String formatearTituloSemanal(LocalDate inicioSemana) {
        LocalDate finSemana = inicioSemana.plusDays(6);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM", Locale.forLanguageTag("es-ES"));
        return inicioSemana.format(formatter) + " - " + finSemana.format(formatter);
    }

    private String formatearTituloMensual(YearMonth mes) {
        return mes.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.forLanguageTag("es-ES")));
    }

    // Component initialization methods
    private void loadInitialData() {
        fechaSeleccionada = LocalDate.now();
        actualizarListaEventos(fechaSeleccionada);
        actualizarFechaSeleccionada();
        // Seleccionar automáticamente el primer evento del día y mostrar detalles
        if (eventListModel.getSize() > 0) {
            eventList.setSelectedIndex(0);
            Evento primerEvento = eventListModel.getElementAt(0);
            mostrarDetallesEvento(primerEvento);
        }
    }

    private void setupSearchPanel(JToolBar toolBar) {
        JPanel searchPanel = new JPanel(new FlowLayout());
        buscadorField = new JTextField(20);
        searchPanel.add(buscadorField);

        // Inicializar popupWindow y su contenido si no está inicializado
        if (popupWindow == null) {
            popupWindow = new JWindow(this);
            popupWindow.setFocusableWindowState(false); // No roba el foco
            JList<Evento> listaResultados = new JList<>(modeloBusqueda);
            listaResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listaResultados.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    Evento seleccionado = listaResultados.getSelectedValue();
                    if (seleccionado != null) {
                        mostrarDetallesEvento(seleccionado);
                        popupWindow.setVisible(false);
                    }
                }
            });
            // --- NUEVO: Doble clic para navegar al evento ---
            listaResultados.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() >= 1) { // Permite selección con un solo clic
                        Evento seleccionado = listaResultados.getSelectedValue();
                        if (seleccionado != null) {
                            tabbedPane.setSelectedIndex(0);
                            fechaSeleccionada = seleccionado.getFecha();
                            if (vistaDiaria != null) {
                                vistaDiaria.establecerDia(fechaSeleccionada);
                            }
                            actualizarListaEventos(fechaSeleccionada);
                            SwingUtilities.invokeLater(() -> {
                                for (int i = 0; i < eventListModel.size(); i++) {
                                    if (eventListModel.get(i).equals(seleccionado)) {
                                        eventList.setSelectedIndex(i);
                                        eventList.ensureIndexIsVisible(i);
                                        break;
                                    }
                                }
                                mostrarDetallesEvento(seleccionado);
                            });
                            popupWindow.setVisible(false);
                        }
                    }
                }
            });
            popupWindow.getContentPane().add(new JScrollPane(listaResultados));
            popupWindow.setSize(300, 200);
        }

        buscadorField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { actualizarBusqueda(); }
            public void removeUpdate(DocumentEvent e) { actualizarBusqueda(); }
            public void changedUpdate(DocumentEvent e) { actualizarBusqueda(); }
        });
        
        toolBar.add(searchPanel);
    }

    // Search functionality methods
    private void actualizarBusqueda() {
        String textoBusqueda = buscadorField.getText().trim();
        if (textoBusqueda.isEmpty()) {
            popupWindow.setVisible(false);
            return;
        }
        popupWindow.setVisible(false); // Ocultar antes de actualizar el modelo
        modeloBusqueda.clear();
        // Búsqueda en eventos del mes actual
        YearMonth mesActual = YearMonth.from(fechaSeleccionada);
        LocalDate inicio = mesActual.atDay(1);
        LocalDate fin = mesActual.atEndOfMonth();
        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            for (Evento evento : modelo.getEventos(fecha)) {
                if (evento.getTitulo().toLowerCase().contains(textoBusqueda.toLowerCase())) {
                    modeloBusqueda.addElement(evento);
                }
            }
        }
        if (!modeloBusqueda.isEmpty()) {
            mostrarVentanaBusqueda();
        }
    }

    private void mostrarVentanaBusqueda() {
        if (!popupWindow.isVisible()) {
            Point p = buscadorField.getLocationOnScreen();
            popupWindow.setLocation(p.x, p.y + buscadorField.getHeight());
            popupWindow.setVisible(true);
        }
    }

    // UI update methods
    private void actualizarTitulo(String nuevoTitulo) {
        if (toolBar != null) {
            for (Component comp : toolBar.getComponents()) {
                if (comp instanceof JLabel label && label.getFont().isBold()) {
                    label.setText(nuevoTitulo);
                    break;
                }
            }
        }
    }

    // Toolbar and navigation methods
    private JToolBar createToolbar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Add navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        navPanel.setOpaque(false);
        
        JButton btnAnterior = createNavigationButton("\u25C0");
        JButton btnHoy = createNavigationButton("Hoy");
        JButton btnSiguiente = createNavigationButton("\u25B6");
        JButton btnNuevoEvento = createNewEventButton();
        
        setupNavigationActions(btnHoy, btnAnterior, btnSiguiente);
        
        navPanel.add(btnAnterior);
        navPanel.add(btnHoy);
        navPanel.add(btnSiguiente);
        navPanel.add(Box.createHorizontalStrut(20));
        navPanel.add(btnNuevoEvento);
        
        toolBar.add(navPanel);
        toolBar.addSeparator();
        
        // Add title label
        JLabel lblTitulo = new JLabel("Calendario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(lblTitulo);
        toolBar.add(Box.createHorizontalGlue());
        
        // Add search panel
        setupSearchPanel(toolBar);
        
        return toolBar;
    }

    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.putClientProperty("JButton.buttonType", "roundRect");
        return button;
    }

    private JButton createNewEventButton() {
        JButton btnNuevoEvento = new JButton("+ Nuevo Evento");
        btnNuevoEvento.putClientProperty("JButton.buttonType", "roundRect");
        btnNuevoEvento.setBackground(new Color(0, 120, 212));
        btnNuevoEvento.setForeground(Color.WHITE);
        btnNuevoEvento.setFocusPainted(false);
        
        btnNuevoEvento.addActionListener(_ -> {
            EventoDialogo dialogo = new EventoDialogo(this, modelo, fechaSeleccionada, false);
            dialogo.setVisible(true);
            if (dialogo.fueModificado()) {
                actualizarListaEventos(fechaSeleccionada);
                actualizarVistasCalendario();
            }
        });
        
        return btnNuevoEvento;
    }

    private void setupNavigationActions(JButton btnHoy, JButton btnAnterior, JButton btnSiguiente) {
        btnHoy.addActionListener(_ -> {
            fechaSeleccionada = LocalDate.now();
            actualizarVistasYEventos(fechaSeleccionada);
        });

        btnAnterior.addActionListener(_ -> navegarAnterior());
        btnSiguiente.addActionListener(_ -> navegarSiguiente());
    }

    private void actualizarVistasYEventos(LocalDate fecha) {
        switch (tabbedPane.getSelectedIndex()) {
            case 0 -> vistaDiaria.establecerDia(fecha);
            case 1 -> vistaSemanal.establecerSemana(fecha.with(DayOfWeek.MONDAY));
            case 2 -> vistaMensual.setMonth(YearMonth.from(fecha));
            case 3 -> vistaAnual.establecerAño(Year.from(fecha));
        }
        actualizarListaEventos(fecha);
        actualizarInterfaz();
    }

    // Navigation methods
    private void navegarAnterior() {
        switch (tabbedPane.getSelectedIndex()) {
            case 0 -> { // Día
                fechaSeleccionada = fechaSeleccionada.minusDays(1);
                vistaDiaria.establecerDia(fechaSeleccionada);
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloDiario(fechaSeleccionada));
            }
            case 1 -> { // Semana
                fechaSeleccionada = fechaSeleccionada.minusWeeks(1);
                vistaSemanal.establecerSemana(fechaSeleccionada);
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloSemanal(fechaSeleccionada));
            }
            case 2 -> { // Mes
                fechaSeleccionada = fechaSeleccionada.minusMonths(1);
                vistaMensual.setMonth(YearMonth.from(fechaSeleccionada));
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloMensual(YearMonth.from(fechaSeleccionada)));
            }
            case 3 -> { // Año
                fechaSeleccionada = fechaSeleccionada.minusYears(1);
                vistaAnual.establecerAño(Year.from(fechaSeleccionada));
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(String.valueOf(Year.from(fechaSeleccionada).getValue()));
            }
        }
    }

    private void navegarSiguiente() {
        switch (tabbedPane.getSelectedIndex()) {
            case 0 -> { // Día
                fechaSeleccionada = fechaSeleccionada.plusDays(1);
                vistaDiaria.establecerDia(fechaSeleccionada);
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloDiario(fechaSeleccionada));
            }
            case 1 -> { // Semana
                fechaSeleccionada = fechaSeleccionada.plusWeeks(1);
                vistaSemanal.establecerSemana(fechaSeleccionada);
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloSemanal(fechaSeleccionada));
            }
            case 2 -> { // Mes
                fechaSeleccionada = fechaSeleccionada.plusMonths(1);
                vistaMensual.setMonth(YearMonth.from(fechaSeleccionada));
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(formatearTituloMensual(YearMonth.from(fechaSeleccionada)));
            }
            case 3 -> { // Año
                fechaSeleccionada = fechaSeleccionada.plusYears(1);
                vistaAnual.establecerAño(Year.from(fechaSeleccionada));
                actualizarListaEventos(fechaSeleccionada);
                actualizarTitulo(String.valueOf(Year.from(fechaSeleccionada).getValue()));
            }
        }
    }

    private void agregarTarea() {
        if (eventoActual == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un evento primero.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Tarea tarea = mostrarDialogoTarea(null);
        if (tarea != null) {
            eventoActual.agregarTarea(tarea);
            modelo.actualizarEvento(eventoActual);
            actualizarVistaTareas(eventoActual);
        }
    }

    private void editarTarea(Tarea tarea) {
        if (tarea == null) return;
        mostrarDialogoTarea(tarea);
        modelo.actualizarEvento(eventoActual);
        actualizarVistaTareas(eventoActual);
    }

    private void agregarSubtarea() {
        Object sel = listaTareasYSubtareas.getSelectedValue();
        Tarea tareaPadre = null;
        if (sel instanceof Tarea) {
            tareaPadre = (Tarea) sel;
        } else if (sel instanceof Subtarea) {
            int idx = listaTareasYSubtareas.getSelectedIndex();
            for (int i = idx - 1; i >= 0; i--) {
                Object obj = modeloTareasYSubtareas.get(i);
                if (obj instanceof Tarea) {
                    tareaPadre = (Tarea) obj;
                    break;
                }
            }
        }
        if (tareaPadre == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una tarea para agregar la subtarea.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String titulo = JOptionPane.showInputDialog(this, "Ingrese el título de la subtarea:", "Nueva subtarea", JOptionPane.PLAIN_MESSAGE);
        if (titulo != null && !titulo.trim().isEmpty()) {
            Subtarea subtarea = new Subtarea(titulo.trim());
            tareaPadre.agregarSubtarea(subtarea);
            modelo.actualizarEvento(eventoActual);
            actualizarVistaTareas(eventoActual);
        }
    }

    private void editarSubtarea(Subtarea subtarea) {
        if (subtarea == null) return;
        String nuevoTitulo = JOptionPane.showInputDialog(this, "Editar subtarea:", subtarea.getTitulo());
        if (nuevoTitulo != null && !nuevoTitulo.trim().isEmpty()) {
            subtarea.setTitulo(nuevoTitulo.trim());
            modelo.actualizarEvento(eventoActual);
            actualizarVistaTareas(eventoActual);
        }
    }

    private void eliminarTareaOSubtarea() {
        Object sel = listaTareasYSubtareas.getSelectedValue();
        if (sel instanceof Tarea tarea) {
            int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar tarea seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                eventoActual.eliminarTarea(tarea);
                modelo.actualizarEvento(eventoActual);
                actualizarVistaTareas(eventoActual);
            }
        } else if (sel instanceof Subtarea subtarea) {
            Tarea tareaPadre = null;
            int idx = listaTareasYSubtareas.getSelectedIndex();
            for (int i = idx - 1; i >= 0; i--) {
                Object obj = modeloTareasYSubtareas.get(i);
                if (obj instanceof Tarea) {
                    tareaPadre = (Tarea) obj;
                    break;
                }
            }
            if (tareaPadre != null) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Eliminar subtarea seleccionada?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tareaPadre.eliminarSubtarea(subtarea);
                    modelo.actualizarEvento(eventoActual);
                    actualizarVistaTareas(eventoActual);
                }
            }
        }
    }

    private Tarea mostrarDialogoTarea(Tarea tarea) {
        JTextField txtTitulo = new JTextField(tarea != null ? tarea.getTitulo() : "");
        JTextField txtDescripcion = new JTextField(tarea != null ? tarea.getDescripcion() : "");

        // JDateChooser para la fecha
        com.toedter.calendar.JDateChooser dateChooser = new com.toedter.calendar.JDateChooser();
        if (tarea != null && tarea.getFecha() != null) {
            dateChooser.setDate(java.sql.Date.valueOf(tarea.getFecha()));
        } else {
            dateChooser.setDate(java.sql.Date.valueOf(fechaSeleccionada));
        }
        // JSpinner para la hora de inicio
        java.util.Calendar cal = java.util.Calendar.getInstance();
        if (tarea != null && tarea.getInicio() != null) {
            cal.set(java.util.Calendar.HOUR_OF_DAY, tarea.getInicio().getHour());
            cal.set(java.util.Calendar.MINUTE, tarea.getInicio().getMinute());
        }
        javax.swing.SpinnerDateModel horaModel = new javax.swing.SpinnerDateModel(cal.getTime(), null, null, java.util.Calendar.MINUTE);
        javax.swing.JSpinner spinnerHora = new javax.swing.JSpinner(horaModel);
        spinnerHora.setEditor(new javax.swing.JSpinner.DateEditor(spinnerHora, "HH:mm"));

        // JSpinner para la hora de fin
        java.util.Calendar calFin = java.util.Calendar.getInstance();
        if (tarea != null && tarea.getFin() != null) {
            calFin.set(java.util.Calendar.HOUR_OF_DAY, tarea.getFin().getHour());
            calFin.set(java.util.Calendar.MINUTE, tarea.getFin().getMinute());
        } else if (tarea != null && tarea.getInicio() != null) {
            calFin.set(java.util.Calendar.HOUR_OF_DAY, tarea.getInicio().getHour() + 1);
            calFin.set(java.util.Calendar.MINUTE, tarea.getInicio().getMinute());
        }
        javax.swing.SpinnerDateModel horaFinModel = new javax.swing.SpinnerDateModel(calFin.getTime(), null, null, java.util.Calendar.MINUTE);
        javax.swing.JSpinner spinnerHoraFin = new javax.swing.JSpinner(horaFinModel);
        spinnerHoraFin.setEditor(new javax.swing.JSpinner.DateEditor(spinnerHoraFin, "HH:mm"));

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Título:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);
        panel.add(new JLabel("Fecha:"));
        panel.add(dateChooser);
        panel.add(new JLabel("Hora inicio (HH:MM):"));
        panel.add(spinnerHora);
        panel.add(new JLabel("Hora fin (HH:MM):"));
        panel.add(spinnerHoraFin);
        int result = JOptionPane.showConfirmDialog(this, panel, tarea == null ? "Nueva tarea" : "Editar tarea", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (tarea == null) {
                Tarea nueva = new Tarea(txtTitulo.getText().trim());
                nueva.setDescripcion(txtDescripcion.getText().trim());
                try {
                    java.util.Date fechaDate = dateChooser.getDate();
                    if (fechaDate != null) {
                        nueva.setFecha(fechaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                    }
                } catch (Exception ex) {}
                try {
                    java.util.Date horaDate = (java.util.Date) spinnerHora.getValue();
                    if (horaDate != null) {
                        java.time.LocalTime hora = horaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
                        nueva.setInicio(hora);
                    }
                } catch (Exception ex) {}
                try {
                    java.util.Date horaFinDate = (java.util.Date) spinnerHoraFin.getValue();
                    if (horaFinDate != null) {
                        java.time.LocalTime horaFin = horaFinDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
                        nueva.setFin(horaFin);
                    }
                } catch (Exception ex) {}
                // Establecer fechaLimite si hay fecha y hora de fin
                if (nueva.getFecha() != null && nueva.getFin() != null) {
                    nueva.setFechaLimite(java.time.LocalDateTime.of(nueva.getFecha(), nueva.getFin()));
                }
                return nueva;
            } else {
                tarea.setTitulo(txtTitulo.getText().trim());
                tarea.setDescripcion(txtDescripcion.getText().trim());
                try {
                    java.util.Date fechaDate = dateChooser.getDate();
                    if (fechaDate != null) {
                        tarea.setFecha(fechaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
                    }
                } catch (Exception ex) {}
                try {
                    java.util.Date horaDate = (java.util.Date) spinnerHora.getValue();
                    if (horaDate != null) {
                        java.time.LocalTime hora = horaDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
                        tarea.setInicio(hora);
                    }
                } catch (Exception ex) {}
                try {
                    java.util.Date horaFinDate = (java.util.Date) spinnerHoraFin.getValue();
                    if (horaFinDate != null) {
                        java.time.LocalTime horaFin = horaFinDate.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalTime().withSecond(0).withNano(0);
                        tarea.setFin(horaFin);
                    }
                } catch (Exception ex) {}
                // Establecer fechaLimite si hay fecha y hora de fin
                if (tarea.getFecha() != null && tarea.getFin() != null) {
                    tarea.setFechaLimite(java.time.LocalDateTime.of(tarea.getFecha(), tarea.getFin()));
                }
                return tarea;
            }
        }
        return null;
    }

    private void setupTareaListContextMenu() {
        JPopupMenu menu = new JPopupMenu();
        JMenuItem marcarRealizada = new JMenuItem("Marcar como realizada");
        JMenuItem desmarcarRealizada = new JMenuItem("Desmarcar como realizada");
        JMenuItem editar = new JMenuItem("Editar");
        menu.add(marcarRealizada);
        menu.add(desmarcarRealizada);
        menu.addSeparator();
        menu.add(editar);

        marcarRealizada.addActionListener(_ -> {
            Object sel = listaTareasYSubtareas.getSelectedValue();
            if (sel instanceof Tarea tarea && !tarea.isCompletada()) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de marcar la tarea como realizada?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tarea.setCompletada(true);
                    // Otorgar puntos si se completa antes de la fecha límite
                    if (tarea.getFechaLimite() != null && tarea.getFechaCompletada() != null && tarea.getFechaCompletada().isBefore(tarea.getFechaLimite())) {
                        long minutosRestantes = ChronoUnit.MINUTES.between(tarea.getFechaCompletada(), tarea.getFechaLimite());
                        int puntos = (int) Math.max(1, minutosRestantes / 10); // 1 punto mínimo, 1 punto por cada 10 minutos de anticipación
                        usuario.sumarPuntos(puntos);
                        tarea.setPuntosOtorgados(puntos);
                        actualizarPuntosUsuario();
                        JOptionPane.showMessageDialog(this, "+" + puntos + " puntos por completar antes de la fecha límite!", "¡Puntos ganados!", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        tarea.setPuntosOtorgados(0);
                    }
                    actualizarVistaTareas(eventoActual);
                    actualizarInterfaz();
                }
            }
        });
        desmarcarRealizada.addActionListener(_ -> {
            Object sel = listaTareasYSubtareas.getSelectedValue();
            if (sel instanceof Tarea tarea && tarea.isCompletada()) {
                int confirm = JOptionPane.showConfirmDialog(this, "¿Desea desmarcar la tarea como realizada?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    tarea.setCompletada(false);
                    // Quitar puntos si se desmarca
                    if (tarea.getPuntosOtorgados() > 0) {
                        usuario.setPuntos(Math.max(0, usuario.getPuntos() - tarea.getPuntosOtorgados()));
                        JOptionPane.showMessageDialog(this, "-" + tarea.getPuntosOtorgados() + " puntos retirados.", "Puntos retirados", JOptionPane.WARNING_MESSAGE);
                        tarea.setPuntosOtorgados(0);
                        actualizarPuntosUsuario();
                    }
                    actualizarVistaTareas(eventoActual);
                    actualizarInterfaz();
                }
            }
        });
        editar.addActionListener(_ -> {
            Object sel = listaTareasYSubtareas.getSelectedValue();
            if (sel instanceof Tarea tarea) {
                editarTarea(tarea);
            } else if (sel instanceof Subtarea subtarea) {
                editarSubtarea(subtarea);
            }
        });

        listaTareasYSubtareas.setComponentPopupMenu(menu);
        listaTareasYSubtareas.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = listaTareasYSubtareas.locationToIndex(e.getPoint());
                    listaTareasYSubtareas.setSelectedIndex(row);
                    Object sel = listaTareasYSubtareas.getSelectedValue();
                    marcarRealizada.setVisible(sel instanceof Tarea tarea && !tarea.isCompletada());
                    desmarcarRealizada.setVisible(sel instanceof Tarea tarea && tarea.isCompletada());
                    editar.setVisible(sel instanceof Tarea || sel instanceof Subtarea);
                    listaTareasYSubtareas.getComponentPopupMenu().show(listaTareasYSubtareas, e.getX(), e.getY());
                }
            }
            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = listaTareasYSubtareas.locationToIndex(e.getPoint());
                    listaTareasYSubtareas.setSelectedIndex(row);
                    Object sel = listaTareasYSubtareas.getSelectedValue();
                    marcarRealizada.setVisible(sel instanceof Tarea tarea && !tarea.isCompletada());
                    desmarcarRealizada.setVisible(sel instanceof Tarea tarea && tarea.isCompletada());
                    editar.setVisible(sel instanceof Tarea || sel instanceof Subtarea);
                    listaTareasYSubtareas.getComponentPopupMenu().show(listaTareasYSubtareas, e.getX(), e.getY());
                }
            }
        });
    }

    private void actualizarPuntosUsuario() {
        lblPuntosUsuario.setText("Puntos: " + usuario.getPuntos());
        UsuarioManager.guardarUsuario(usuario);
    }
}



