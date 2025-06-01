package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelMensual;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelSemanal;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelDiaria;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelAnual;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javax.swing.Box;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import co.edu.uis.organizationapp.modelo.calendario.Tarea;
import co.edu.uis.organizationapp.modelo.calendario.Subtarea;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JWindow;
import javax.swing.Timer;

public class CalendarioDashboard extends javax.swing.JFrame {
    
    private ModeloCalendario modelo;
    private VistaPanelMensual vistaMensual;
    private VistaPanelSemanal vistaSemanal;
    private VistaPanelDiaria vistaDiaria;
    private VistaPanelAnual vistaAnual;
    private JTabbedPane tabbedPane;
    private JList<Evento> eventList; 
    private DefaultListModel<Evento> eventListModel; // Modelo de datos para la lista
    private JLabel lblFechaSeleccionada;
    private LocalDate fechaSeleccionada;
    private JTabbedPane pestañasDerecha;
    private DefaultListModel<TareaCheckBox> modeloTareas;
    private JTextArea txtDetalles;
    private EventoDialogo eventoD;

    // Agregar estas variables de clase para el panel de búsqueda
    private JWindow popupWindow;
    private JList<Evento> resultadosBusqueda;
    private JLabel noResultsLabel;
    
    // Agregar esta variable de clase
    private final LocalDate fechaActual = LocalDate.now();
    
    public CalendarioDashboard() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        getContentPane().setLayout(new BorderLayout());

        // Inicializar el modelo
        this.modelo = new ModeloCalendario();
        
        // Inicializar fecha seleccionada y label
        LocalDate hoy = LocalDate.now();
        fechaSeleccionada = hoy;
        lblFechaSeleccionada = new JLabel("No hay día seleccionado");
        lblFechaSeleccionada.setHorizontalAlignment(SwingConstants.CENTER);

        // Inicializar las listas
        inicializarListaEventos();
        
        // Inicializar vistas
        inicializarVistas(hoy);
        
        // Configurar look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
        }

        // Ahora es seguro llamar a estos métodos
        actualizarFechaSeleccionada();
        actualizarListaEventos(hoy);

        // --- 1. Establecer el administrador de diseño a BorderLayout ---
        getContentPane().setLayout(new BorderLayout());

        // --- 2. Añadir componentes a las diferentes regiones del BorderLayout ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Panel izquierdo de la toolbar para navegación
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        navPanel.setOpaque(false);
        
        // Botones de navegación con íconos
        JButton btnAnterior = new JButton("\u25C0");  // Triángulo izquierdo
        JButton btnHoy = new JButton("Hoy");
        JButton btnSiguiente = new JButton("\u25B6");  // Triángulo derecho
        
        // Estilizar botones
        btnAnterior.putClientProperty("JButton.buttonType", "roundRect");
        btnHoy.putClientProperty("JButton.buttonType", "roundRect");
        btnSiguiente.putClientProperty("JButton.buttonType", "roundRect");
        
        navPanel.add(btnAnterior);
        navPanel.add(btnHoy);
        navPanel.add(btnSiguiente);
        
        toolBar.add(navPanel);
        toolBar.addSeparator();
        
        // Panel central para el título (se actualizará según la vista)
        JLabel lblTitulo = new JLabel("Calendario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        toolBar.add(Box.createHorizontalGlue());
        toolBar.add(lblTitulo);
        toolBar.add(Box.createHorizontalGlue());
        
        // Panel derecho para el buscador
        setupSearchPanel(toolBar);

        getContentPane().add(toolBar, BorderLayout.NORTH);
        
        // Configurar acciones
        btnHoy.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0 -> vistaDiaria.establecerDia(fechaActual);
                case 1 -> vistaSemanal.establecerSemana(fechaActual.with(DayOfWeek.MONDAY));
                case 2 -> vistaMensual.setMonth(YearMonth.from(fechaActual));
                case 3 -> vistaAnual.establecerAño(Year.now());
            }
            actualizarListaEventos(fechaActual);
            actualizarInterfaz();
        });

        // Modificar botón anterior
        btnAnterior.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0: // Día
                    LocalDate diaAnterior = vistaDiaria.getDiaActual().minusDays(1);
                    vistaDiaria.establecerDia(diaAnterior);
                    lblTitulo.setText(formatearTituloDiario(diaAnterior));
                    actualizarListaEventos(diaAnterior); // Agregar esta línea
                    break;
                case 1: // Semana
                    LocalDate semanaAnterior = vistaSemanal.getSemanaActual().minusWeeks(1);
                    vistaSemanal.establecerSemana(semanaAnterior);
                    lblTitulo.setText(formatearTituloSemanal(semanaAnterior));
                    break;
                case 2: // Mes
                    YearMonth mesAnterior = vistaMensual.getMesActual().minusMonths(1);
                    vistaMensual.setMonth(mesAnterior);
                    lblTitulo.setText(formatearTituloMensual(mesAnterior));
                    break;
                case 3: // Año
                    Year añoAnterior = vistaAnual.getAñoActual().minusYears(1);
                    vistaAnual.establecerAño(añoAnterior);
                    lblTitulo.setText(String.valueOf(añoAnterior.getValue()));
                    break;
            }
        });
        
        // Modificar botón siguiente
        btnSiguiente.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0: // Día
                    LocalDate diaSiguiente = vistaDiaria.getDiaActual().plusDays(1);
                    vistaDiaria.establecerDia(diaSiguiente);
                    lblTitulo.setText(formatearTituloDiario(diaSiguiente));
                    actualizarListaEventos(diaSiguiente); // Agregar esta línea
                    break;
                case 1: // Semana
                    LocalDate semanaSiguiente = vistaSemanal.getSemanaActual().plusWeeks(1);
                    vistaSemanal.establecerSemana(semanaSiguiente);
                    lblTitulo.setText(formatearTituloSemanal(semanaSiguiente));
                    break;
                case 2: // Mes
                    YearMonth mesSiguiente = vistaMensual.getMesActual().plusMonths(1);
                    vistaMensual.setMonth(mesSiguiente);
                    lblTitulo.setText(formatearTituloMensual(mesSiguiente));
                    break;
                case 3: // Año
                    Year añoSiguiente = vistaAnual.getAñoActual().plusYears(1);
                    vistaAnual.establecerAño(añoSiguiente);
                    lblTitulo.setText(String.valueOf(añoSiguiente.getValue()));
                    break;
            }
        });

        // Modificar el listener del tabbedPane para que actualice la lista de eventos
        tabbedPane.addChangeListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0 -> {
                    lblTitulo.setText(formatearTituloDiario(vistaDiaria.getDiaActual()));
                    fechaSeleccionada = vistaDiaria.getDiaActual();
                    actualizarListaEventos(fechaSeleccionada);
                }
                case 1 -> {
                    LocalDate inicioSemana = vistaSemanal.getSemanaActual();
                    lblTitulo.setText(formatearTituloSemanal(inicioSemana));
                }
                case 2 -> {
                    lblTitulo.setText(formatearTituloMensual(vistaMensual.getMesActual()));
                }
                case 3 -> {
                    lblTitulo.setText(String.valueOf(vistaAnual.getAñoActual().getValue()));
                }
            }
            actualizarFechaSeleccionada();
        });

        // Modificar la posición donde se establece el tamaño del splitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation((int)(getWidth() * 0.8)); // 80% del ancho para el calendario
        splitPane.setResizeWeight(0.8); // mantiene la proporción al redimensionar

        // Establecer el TabbedPane como el componente IZQUIERDO del JSplitPane
        splitPane.setLeftComponent(tabbedPane);
        
        JPanel panelDerechoSplit = new JPanel();
        panelDerechoSplit.setPreferredSize(new Dimension(280, 0));
        panelDerechoSplit.setLayout(new BorderLayout(0, 10));
        panelDerechoSplit.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior para información del día seleccionado
        JPanel panelInfoDia = new JPanel(new BorderLayout());
        panelInfoDia.setBorder(BorderFactory.createTitledBorder("Día seleccionado"));
        lblFechaSeleccionada = new JLabel("No hay día seleccionado");
        lblFechaSeleccionada.setHorizontalAlignment(SwingConstants.CENTER);
        panelInfoDia.add(lblFechaSeleccionada, BorderLayout.CENTER);
        panelDerechoSplit.add(panelInfoDia, BorderLayout.NORTH);

        // Panel central para lista de eventos
        JPanel panelEventos = new JPanel(new BorderLayout());
        panelEventos.setBorder(BorderFactory.createTitledBorder("Eventos"));
        
        inicializarListaEventos();
        JScrollPane scrollEventos = new JScrollPane(eventList);
        panelEventos.add(scrollEventos, BorderLayout.CENTER);

        // Botones de acciones
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        
        btnAgregar.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminar.putClientProperty("JButton.buttonType", "roundRect");
        
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);
        
        panelEventos.add(panelBotones, BorderLayout.SOUTH);
        panelDerechoSplit.add(panelEventos, BorderLayout.CENTER);

        // Modificar el panel derecho para usar pestañas
        pestañasDerecha = new JTabbedPane();
        
        setupDetallesPanel(panelDerechoSplit);

        splitPane.setRightComponent(panelDerechoSplit);
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        // Panel en la región SUR (inferior)
        JPanel panelSur = new JPanel();
        getContentPane().add(panelSur, BorderLayout.SOUTH); // Añade el panel al SUR

        // Panel en la región ESTE (derecha)
        JPanel panelEste = new JPanel();
        getContentPane().add(panelEste, BorderLayout.EAST); // Añade el panel al ESTE

        // Panel en la región OESTE (izquierda)
        JPanel panelOeste = new JPanel();
        getContentPane().add(panelOeste, BorderLayout.WEST); // Añade el panel al OESTE

        // --- 3. Ajustar el tamaño de la ventana ---
        pack();

        // Opcional: Centrar la ventana en la pantalla
        setLocationRelativeTo(null);

        // En el constructor, después de inicializar todas las vistas
        actualizarListaEventos(hoy); // Asegurarse que el día actual se seleccione al inicio
    }

    private void inicializarListaEventos() {
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setCellRenderer(new RenderizadorDeListaDeEventos());
        
        // Agregar selección automática
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Evento eventoSeleccionado = eventList.getSelectedValue();
                if (eventoSeleccionado != null) {
                    mostrarDetallesEvento(eventoSeleccionado);
                }
            }
        });
    }

    private void actualizarInterfaz() {
        tabbedPane.revalidate();
        tabbedPane.repaint();
    }

    private String formatearTituloDiario(LocalDate fecha) {
        return fecha.format(DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", new Locale("es", "ES")));
    }

    private String formatearTituloSemanal(LocalDate inicioSemana) {
        LocalDate finSemana = inicioSemana.plusDays(6);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d 'de' MMMM", new Locale("es", "ES"));
        return fmt.format(inicioSemana) + " - " + fmt.format(finSemana);
    }

    private String formatearTituloMensual(YearMonth mes) {
        return mes.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("es", "ES")));
    }

    public void actualizarListaEventos(LocalDate fecha) {
        if (fecha != null) {
            Evento eventoSeleccionadoActual = eventList.getSelectedValue();
            fechaSeleccionada = fecha;
            actualizarFechaSeleccionada();
            
            // Desactivar temporalmente el listener de selección
            var listeners = eventList.getListSelectionListeners();
            for (var listener : listeners) {
                eventList.removeListSelectionListener(listener);
            }
            
            // Actualizar lista
            eventListModel.clear();
            List<Evento> eventosDelDia = modelo.getEventos(fecha);
            eventosDelDia.forEach(eventListModel::addElement);
            
            // Mantener la selección del evento actual si existe
            if (eventoSeleccionadoActual != null) {
                // Buscar el mismo evento en la lista actualizada
                for (int i = 0; i < eventListModel.size(); i++) {
                    if (eventListModel.get(i).equals(eventoSeleccionadoActual)) {
                        eventList.setSelectedIndex(i);
                        mostrarDetallesEvento(eventoSeleccionadoActual, false);
                        break;
                    }
                }
            } else if (!eventosDelDia.isEmpty()) {
                eventList.setSelectedIndex(0);
                mostrarDetallesEvento(eventosDelDia.get(0), false);
            } else {
                limpiarDetalles();
            }
            
            // Reactivar el listener
            for (var listener : listeners) {
                eventList.addListSelectionListener(listener);
            }
        }
    }

    private void limpiarDetalles() {
        if (txtDetalles != null) {
            txtDetalles.setText("");
        }
        if (modeloTareas != null) {
            modeloTareas.clear();
        }
    }

    // Método actualizado para mostrar detalles
    public void mostrarDetallesEvento(Evento evento) {
        mostrarDetallesEvento(evento, true);
    }

    // Método privado con flag para controlar la actualización
    private void mostrarDetallesEvento(Evento evento, boolean actualizarLista) {
        if (evento != null && txtDetalles != null && modeloTareas != null) {
            // Actualizar fecha seleccionada
            if (actualizarLista && !evento.getFecha().equals(fechaSeleccionada)) {
                fechaSeleccionada = evento.getFecha();
                actualizarFechaSeleccionada();
                actualizarListaEventos(fechaSeleccionada);
                return;
            }

            // Mostrar detalles del evento
            String hora = String.format("%s - %s",
                evento.getInicio().format(DateTimeFormatter.ofPattern("HH:mm")),
                evento.getFin().format(DateTimeFormatter.ofPattern("HH:mm")));

            txtDetalles.setText(String.format("""
                \u2B50 %s
                \u231B %s
                
                %s""",
                evento.getTitulo().toUpperCase(),
                hora,
                evento.getDescripcion()));
            
            // Actualizar tareas
            modeloTareas.clear();
            if (evento.getTareas() != null) {
                for (Tarea tarea : evento.getTareas()) {
                    // Agregar la tarea principal
                    TareaCheckBox tareaCheckBox = new TareaCheckBox(tarea);
                    modeloTareas.addElement(tareaCheckBox);
                    
                    // Agregar sus subtareas
                    if (tarea.getSubtareas() != null) {
                        for (Subtarea subtarea : tarea.getSubtareas()) {
                            TareaCheckBox subtareaCheckBox = new TareaCheckBox(null);
                            subtareaCheckBox.setSubtarea(subtarea);
                            subtareaCheckBox.setTareaPadre(tareaCheckBox);
                            modeloTareas.addElement(subtareaCheckBox);
                        }
                    }
                }
            }
        }
    }

    // Métodos para gestionar tareas
    private void agregarNuevaTarea() {
        // TODO: Implementar diálogo para nueva tarea
    }

    private void editarTarea(TareaCheckBox tarea) {
        // TODO: Implementar diálogo para editar tarea
    }

    private void eliminarTarea(TareaCheckBox tarea) {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar esta tarea?",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            modeloTareas.removeElement(tarea);
            // TODO: Actualizar el modelo de datos
        }
    }

    private boolean coincideConBusqueda(Evento evento, String textoBusqueda) {
        // Buscar en título
        if (evento.getTitulo().toLowerCase().contains(textoBusqueda)) {
            return true;
        }
        
        // Buscar en descripción
        if (evento.getDescripcion() != null && 
            evento.getDescripcion().toLowerCase().contains(textoBusqueda)) {
            return true;
        }
        
        // Buscar en fecha (formato dd/MM/yyyy)
        String fechaStr = evento.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        if (fechaStr.toLowerCase().contains(textoBusqueda)) {
            return true;
        }
        
        // Buscar en hora (formato HH:mm)
        String horaStr = evento.getInicio().format(DateTimeFormatter.ofPattern("HH:mm")) + " - " +
                        evento.getFin().format(DateTimeFormatter.ofPattern("HH:mm"));
        return horaStr.toLowerCase().contains(textoBusqueda);
    }

    private void actualizarFechaSeleccionada() {
        if (lblFechaSeleccionada != null && fechaSeleccionada != null) {
            lblFechaSeleccionada.setText(formatearTituloDiario(fechaSeleccionada));
        }
    }

    private void inicializarVistas(LocalDate hoy) {
        vistaMensual = new VistaPanelMensual(modelo);
        vistaSemanal = new VistaPanelSemanal(modelo);
        vistaDiaria = new VistaPanelDiaria(modelo);
        vistaAnual = new VistaPanelAnual(modelo);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Día", vistaDiaria);
        tabbedPane.addTab("Semana", vistaSemanal);
        tabbedPane.addTab("Mes", vistaMensual);
        tabbedPane.addTab("Año", vistaAnual);

        vistaMensual.setMonth(YearMonth.from(hoy));
        vistaSemanal.establecerSemana(hoy.with(DayOfWeek.MONDAY));
        vistaDiaria.establecerDia(hoy);
        vistaAnual.establecerAño(Year.now());
    }

    private void setupDetallesPanel(JPanel panelDerechoSplit) {
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
        
        // Panel de detalles
        JPanel panelDetalles = new JPanel(new BorderLayout(0, 5));
        txtDetalles = new JTextArea(6, 0);
        txtDetalles.setEditable(false);
        txtDetalles.setLineWrap(true);
        txtDetalles.setWrapStyleWord(true);
        txtDetalles.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDetalles.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        scrollDetalles.setBorder(BorderFactory.createTitledBorder("Detalles del evento"));
        panelDetalles.add(scrollDetalles, BorderLayout.CENTER);
        
        // Panel de tareas
        JPanel panelTareas = new JPanel(new BorderLayout(0, 5));
        panelTareas.setBorder(BorderFactory.createTitledBorder("Tareas del evento"));
        
        modeloTareas = new DefaultListModel<>();
        JList<TareaCheckBox> listaTareas = new JList<>(modeloTareas);
        listaTareas.setCellRenderer(new TareaRenderer());
        JScrollPane scrollTareas = new JScrollPane(listaTareas);
        
        // Botones de tareas
        JPanel panelBotonesTareas = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregarTarea = new JButton("Nueva");
        JButton btnEditarTarea = new JButton("Editar");
        JButton btnEliminarTarea = new JButton("Eliminar");
        
        // Estilizar botones
        btnAgregarTarea.putClientProperty("JButton.buttonType", "roundRect");
        btnEditarTarea.putClientProperty("JButton.buttonType", "roundRect");
        btnEliminarTarea.putClientProperty("JButton.buttonType", "roundRect");
        
        panelBotonesTareas.add(btnAgregarTarea);
        panelBotonesTareas.add(btnEditarTarea);
        panelBotonesTareas.add(btnEliminarTarea);
        
        panelTareas.add(scrollTareas, BorderLayout.CENTER);
        panelTareas.add(panelBotonesTareas, BorderLayout.SOUTH);
        
        panelPrincipal.add(panelDetalles, BorderLayout.NORTH);
        panelPrincipal.add(panelTareas, BorderLayout.CENTER);
        
        panelDerechoSplit.add(panelPrincipal, BorderLayout.CENTER);
        
        // Configurar acciones de los botones
        btnAgregarTarea.addActionListener(e -> {
            Evento eventoSeleccionado = eventList.getSelectedValue();
            if (eventoSeleccionado != null) {
                EventoDialogo dialogo = new EventoDialogo(this, modelo, eventoSeleccionado.getFecha(), true);
                dialogo.setEvento(eventoSeleccionado);
                dialogo.setVisible(true);
                if (dialogo.fueModificado()) {
                    // Actualizar la lista de eventos y forzar la actualización de detalles
                    actualizarListaEventos(fechaSeleccionada);
                    mostrarDetallesEvento(eventoSeleccionado, false);
                    eventList.setSelectedValue(eventoSeleccionado, true);
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un evento para agregar una tarea",
                    "Aviso",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });

        btnEditarTarea.addActionListener(e -> {
            TareaCheckBox tareaSeleccionada = listaTareas.getSelectedValue();
            if (tareaSeleccionada != null) {
                Evento eventoSeleccionado = eventList.getSelectedValue();
                
                if (tareaSeleccionada.isSubtarea()) {
                    // Si es una subtarea, mostrar diálogo simple para editar nombre
                    JTextField txtSubtarea = new JTextField(20);
                    txtSubtarea.setText(tareaSeleccionada.getSubtarea().getTitulo());
                    Object[] message = {
                        "Nombre de la Subtarea:", txtSubtarea
                    };
                    
                    int option = JOptionPane.showConfirmDialog(
                        this, 
                        message, 
                        "Editar Subtarea", 
                        JOptionPane.OK_CANCEL_OPTION, 
                        JOptionPane.PLAIN_MESSAGE
                    );
                    
                    if (option == JOptionPane.OK_OPTION && !txtSubtarea.getText().trim().isEmpty()) {
                        tareaSeleccionada.getSubtarea().setTitulo(txtSubtarea.getText().trim());
                        modelo.actualizarEvento(eventoSeleccionado);
                        actualizarListaEventos(fechaSeleccionada);
                        mostrarDetallesEvento(eventoSeleccionado, false);
                    }
                } else {
                    // Si es una tarea principal, mostrar el diálogo completo
                    EventoDialogo dialogo = new EventoDialogo(this, modelo, fechaSeleccionada, true);
                    dialogo.setEvento(eventoSeleccionado);
                    dialogo.editarTarea(tareaSeleccionada.getTarea());
                    dialogo.setVisible(true);
                    if (dialogo.fueModificado()) {
                        actualizarListaEventos(fechaSeleccionada);
                        mostrarDetallesEvento(eventoSeleccionado, false);
                    }
                }
            }
        });

        btnEliminarTarea.addActionListener(e -> {
            TareaCheckBox tareaSeleccionada = listaTareas.getSelectedValue();
            if (tareaSeleccionada != null) {
                Evento eventoSeleccionado = eventList.getSelectedValue();
                if (eventoSeleccionado != null) {
                    String mensaje;
                    if (tareaSeleccionada.isSubtarea()) {
                        mensaje = "¿Está seguro de eliminar esta subtarea?";
                    } else {
                        mensaje = "¿Está seguro de eliminar esta tarea?";
                    }

                    int confirmacion = JOptionPane.showConfirmDialog(
                        this,
                        mensaje,
                        "Confirmar eliminación",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirmacion == JOptionPane.YES_OPTION) {
                        if (tareaSeleccionada.isSubtarea()) {
                            // Eliminar subtarea
                            Tarea tareaPadre = tareaSeleccionada.getSubtarea().getTareaPadre();
                            tareaPadre.eliminarSubtarea(tareaSeleccionada.getSubtarea());
                        } else {
                            // Eliminar tarea principal
                            eventoSeleccionado.eliminarTarea(tareaSeleccionada.getTarea());
                        }
                        modeloTareas.removeElement(tareaSeleccionada);
                        modelo.actualizarEvento(eventoSeleccionado);
                        mostrarDetallesEvento(eventoSeleccionado, false);
                    }
                }
            }
        });
        
        // Agregar listener para el checkbox de las tareas
        listaTareas.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int index = listaTareas.locationToIndex(e.getPoint());
                if (index >= 0) {
                    TareaCheckBox tarea = modeloTareas.getElementAt(index);
                    Rectangle bounds = listaTareas.getCellBounds(index, index);
                    if (bounds != null) {
                        // Ajustar la zona de click según si es subtarea o no
                        int checkboxX = tarea.isSubtarea() ? bounds.x + 35 : bounds.x + 5;
                        if (e.getX() >= checkboxX && e.getX() <= checkboxX + 20) {
                            tarea.setCompletada(!tarea.isCompletada());
                            // Guardar el cambio en el modelo
                            Evento eventoSeleccionado = eventList.getSelectedValue();
                            if (eventoSeleccionado != null) {
                                modelo.actualizarEvento(eventoSeleccionado);
                            }
                            listaTareas.repaint();
                        }
                    }
                }
            }
        });
    }

    private void setupSearchPanel(JToolBar toolBar) {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));
        searchPanel.setOpaque(false);

        JPanel searchFieldPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchFieldPanel.setOpaque(false);
        JTextField buscadorField = new JTextField(20);
        buscadorField.putClientProperty("JTextField.placeholderText", "Buscar eventos...");

        // Inicializar las variables de clase
        resultadosBusqueda = new JList<>(new DefaultListModel<>());
        noResultsLabel = new JLabel("No se encontraron eventos", SwingConstants.CENTER);
        popupWindow = new JWindow(this);
        
        // Lista de resultados
        JScrollPane scrollResultados = new JScrollPane(resultadosBusqueda);
        scrollResultados.setPreferredSize(new Dimension(300, 200));

        // Panel popup
        JPanel popupPanel = new JPanel(new BorderLayout());
        popupPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        noResultsLabel.setForeground(Color.GRAY);
        noResultsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        noResultsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        popupPanel.add(scrollResultados, BorderLayout.CENTER);
        popupPanel.add(noResultsLabel, BorderLayout.SOUTH);

        // Configurar la ventana emergente
        popupWindow.setFocusableWindowState(false);
        popupWindow.getContentPane().add(popupPanel);
        popupWindow.setSize(300, 200);

        // Listener del campo de búsqueda
        buscadorField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { buscarEventos(buscadorField.getText()); }
            @Override
            public void removeUpdate(DocumentEvent e) { buscarEventos(buscadorField.getText()); }
            @Override
            public void changedUpdate(DocumentEvent e) { buscarEventos(buscadorField.getText()); }
        });

        searchFieldPanel.add(buscadorField);
        searchPanel.add(searchFieldPanel);
        toolBar.add(searchPanel);
    }

    private void buscarEventos(String texto) {
        DefaultListModel<Evento> modeloResultados = (DefaultListModel<Evento>) resultadosBusqueda.getModel();
        modeloResultados.clear();
        noResultsLabel.setVisible(true);

        if (texto == null || texto.trim().isEmpty()) {
            popupWindow.setVisible(false);
            return;
        }

        String textoBusqueda = texto.toLowerCase().trim();
        List<Evento> resultados = modelo.getTodosLosEventos().stream()
            .filter(evento -> coincideConBusqueda(evento, textoBusqueda))
            .toList();

        if (!resultados.isEmpty()) {
            resultados.forEach(modeloResultados::addElement);
            noResultsLabel.setVisible(false);
            resultadosBusqueda.setVisible(true);
            popupWindow.setVisible(true);
        } else {
            resultadosBusqueda.setVisible(false);
        }
    }
}
