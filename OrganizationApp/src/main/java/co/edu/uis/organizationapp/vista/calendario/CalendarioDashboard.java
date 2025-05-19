package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelMensual;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelSemanal;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelDiaria;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelAnual;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.miginfocom.swing.MigLayout;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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

    public CalendarioDashboard() {
        // Al inicio del constructor
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        getContentPane().setLayout(new BorderLayout());

        // Inicializar el modelo y las listas primero
        this.modelo = new ModeloCalendario();
        inicializarListaEventos();

        // Inicializar el label de fecha seleccionada
        lblFechaSeleccionada = new JLabel("No hay día seleccionado");
        lblFechaSeleccionada.setHorizontalAlignment(SwingConstants.CENTER);

        // Inicializar las vistas
        vistaMensual = new VistaPanelMensual(modelo);
        vistaSemanal = new VistaPanelSemanal(modelo);
        vistaDiaria = new VistaPanelDiaria(modelo);
        vistaAnual = new VistaPanelAnual(modelo);

        // Inicializar y configurar el tabbedPane
        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Día", vistaDiaria);
        tabbedPane.addTab("Semana", vistaSemanal);
        tabbedPane.addTab("Mes", vistaMensual);
        tabbedPane.addTab("Año", vistaAnual);

        // Establecer el día actual por defecto al iniciar
        LocalDate hoy = LocalDate.now();
        fechaSeleccionada = hoy;

        // Configurar fechas iniciales
        vistaMensual.setMonth(YearMonth.from(hoy));
        vistaSemanal.establecerSemana(hoy.with(DayOfWeek.MONDAY));
        vistaDiaria.establecerDia(hoy);
        vistaAnual.establecerAño(Year.now());

        // Configurar el look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            SwingUtilities.updateComponentTreeUI(this);
        } catch (UnsupportedLookAndFeelException e) {
        }

        // Now it's safe to call these methods
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
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        searchPanel.setOpaque(false);
        
        // Campo de búsqueda sin ícono
        JTextField buscadorField = new JTextField(15);
        buscadorField.putClientProperty("JTextField.placeholderText", "🔍 Buscar eventos...");
        
        searchPanel.add(buscadorField);
        toolBar.add(searchPanel);
        
        getContentPane().add(toolBar, BorderLayout.NORTH);
        
        // Configurar acciones
        btnHoy.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0 -> vistaDiaria.establecerDia(hoy);
                case 1 -> vistaSemanal.establecerSemana(hoy.with(DayOfWeek.MONDAY));
                case 2 -> vistaMensual.setMonth(YearMonth.from(hoy));
                case 3 -> vistaAnual.establecerAño(Year.now());
            }
            actualizarInterfaz();
        });

        // Configurar navegación
        btnAnterior.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0: // Día
                    LocalDate diaAnterior = vistaDiaria.getDiaActual().minusDays(1);
                    vistaDiaria.establecerDia(diaAnterior);
                    lblTitulo.setText(formatearTituloDiario(diaAnterior));
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
        
        btnSiguiente.addActionListener(e -> {
            switch (tabbedPane.getSelectedIndex()) {
                case 0: // Día
                    LocalDate diaSiguiente = vistaDiaria.getDiaActual().plusDays(1);
                    vistaDiaria.establecerDia(diaSiguiente);
                    lblTitulo.setText(formatearTituloDiario(diaSiguiente));
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

        // Add the change listener after tabbedPane is initialized
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
                    // No actualizar fechaSeleccionada al cambiar a vista semanal
                    actualizarListaEventos(fechaSeleccionada);
                }
                case 2 -> {
                    lblTitulo.setText(formatearTituloMensual(vistaMensual.getMesActual()));
                    // Mantener la fecha seleccionada actual
                }
                case 3 -> {
                    lblTitulo.setText(String.valueOf(vistaAnual.getAñoActual().getValue()));
                    // Mantener la fecha seleccionada actual
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

        // Panel inferior para detalles del evento seleccionado
        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBorder(BorderFactory.createTitledBorder("Detalles del evento"));
        JTextArea txtDetalles = new JTextArea(5, 0);
        txtDetalles.setEditable(false);
        JScrollPane scrollDetalles = new JScrollPane(txtDetalles);
        panelDetalles.add(scrollDetalles);
        panelDerechoSplit.add(panelDetalles, BorderLayout.SOUTH);

        // Configurar eventos
        eventList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                Evento eventoSeleccionado = eventList.getSelectedValue();
                if (eventoSeleccionado != null) {
                    String detalles = String.format("""
                        Título: %s
                        Fecha: %s
                        Hora: %s - %s
                        Descripción: %s""",
                        eventoSeleccionado.getTitulo(),
                        eventoSeleccionado.getFecha(),
                        eventoSeleccionado.getInicio(),
                        eventoSeleccionado.getFin(),
                        eventoSeleccionado.getDescripcion());
                    txtDetalles.setText(detalles);
                } else {
                    txtDetalles.setText("");
                }
            }
        });

        btnAgregar.addActionListener(e -> {
            EventoDialogo dialogo = new EventoDialogo(this, modelo, fechaSeleccionada != null ? fechaSeleccionada : LocalDate.now());
            dialogo.setVisible(true);
            actualizarListaEventos(fechaSeleccionada);
        });

        btnEliminar.addActionListener(e -> {
            Evento eventoSeleccionado = eventList.getSelectedValue();
            if (eventoSeleccionado != null) {
                int confirmacion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Está seguro de que desea eliminar el evento '" + eventoSeleccionado.getTitulo() + "'?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirmacion == JOptionPane.YES_OPTION) {
                    modelo.eliminarEvento(eventoSeleccionado);
                    actualizarListaEventos(fechaSeleccionada);
                    actualizarVistas();
                }
            }
        });

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
    }

    private void inicializarListaEventos() {
        eventListModel = new DefaultListModel<>();
        eventList = new JList<>(eventListModel);
        eventList.setCellRenderer(new RenderizadorDeListaDeEventos());  // Usar nuestro renderizador personalizado
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
            fechaSeleccionada = fecha;
            actualizarFechaSeleccionada();
            eventListModel.clear();
            modelo.getEventos(fecha).forEach(eventListModel::addElement);
        }
    }

    private void actualizarFechaSeleccionada() {
        if (fechaSeleccionada != null) {
            lblFechaSeleccionada.setText(formatearTituloDiario(fechaSeleccionada));
        } else {
            lblFechaSeleccionada.setText("No hay día seleccionado");
        }
    }

    private void actualizarVistas() {
        vistaDiaria.actualizarVista();
        vistaSemanal.repintarSemana();
        vistaMensual.repintarMes();
        vistaAnual.actualizarVista();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {
        
        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CalendarioDashboard().setVisible(true);
            }
        });
    }
}
