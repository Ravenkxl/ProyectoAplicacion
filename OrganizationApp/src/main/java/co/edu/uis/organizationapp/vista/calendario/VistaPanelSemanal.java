package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.JScrollBar;
import javax.swing.Timer;
import java.time.format.TextStyle;

public class VistaPanelSemanal extends JPanel {
    private static final int HOUR_HEIGHT = 60;
    private static final int HEADER_HEIGHT = 50;
    private static final Color GRID_COLOR = new Color(230, 230, 230);
    private static final Color CURRENT_TIME_COLOR = new Color(255, 0, 0, 128);
    private static final Color WORK_HOURS_COLOR = new Color(246, 248, 250);
    private static final Color TODAY_COLUMN_COLOR = new Color(255, 245, 245);

    private LocalDate weekStart;
    private ModeloCalendario model;
    private JLayeredPane layer;
    private JScrollPane mainScroll;  // Añadir como campo de clase

    public VistaPanelSemanal(ModeloCalendario m) {
        this.model = m;
        setLayout(new BorderLayout());
        
        LocalDate today = LocalDate.now();
        this.weekStart = today.with(DayOfWeek.MONDAY);

        inicializarComponentes();

        // Configurar scroll inicial a las 8 AM
        SwingUtilities.invokeLater(() -> {
            mainScroll.getVerticalScrollBar().setValue(8 * HOUR_HEIGHT);
        });

        // Agregar listener para el redimensionamiento
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                SwingUtilities.invokeLater(() -> repintarSemana());
            }
        });

        // Establecer color de fondo
        setBackground(Color.WHITE);
    }

    private void inicializarComponentes() {
        // Crear un panel contenedor para el timePanel y su espaciador
        JPanel timeContainer = new JPanel(new BorderLayout());
        timeContainer.setBackground(Color.WHITE);
        
        // Agregar un panel espaciador que coincida con el header
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(60, HEADER_HEIGHT));
        spacer.setBackground(Color.WHITE);
        timeContainer.add(spacer, BorderLayout.NORTH);

        // Panel de horas
        PanelDeTiempo timePanel = new PanelDeTiempo();
        JScrollPane timeScroll = new JScrollPane(timePanel);
        timeScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        timeScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        timeScroll.setBorder(null);
        timeContainer.add(timeScroll, BorderLayout.CENTER);
        
        add(timeContainer, BorderLayout.WEST);

        // Panel principal con grid de días
        layer = new JLayeredPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getParent() != null ? getParent().getWidth() - 60 : 800,
                        HOUR_HEIGHT * 24);
            }
        };
        layer.setBackground(Color.WHITE);
        layer.setOpaque(true);

        mainScroll = new JScrollPane(layer);
        mainScroll.getVerticalScrollBar().setUnitIncrement(30);
        actualizarEncabezado();  // Llamar al método separado
        mainScroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GRID_COLOR));

        // Sincronizar scrolls
        mainScroll.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (timeScroll != null) {
                timeScroll.getVerticalScrollBar().setValue(e.getValue());
            }
        });

        add(mainScroll, BorderLayout.CENTER);
    }

    public void establecerSemana(LocalDate startMon) {
        this.weekStart = startMon;
        actualizarEncabezado();
        repintarSemana();
    }

    private void actualizarEncabezado() {
        if (mainScroll != null) {
            mainScroll.setColumnHeaderView(CrearPanelDelEncabezado());
            mainScroll.revalidate();
        }
    }

    private JPanel CrearPanelDelEncabezado() {
        
        // Se aplican las dimensiones, esto se puede modificar dependiendo el gusto o diseño de la interfaz que se nos ocurra
        
        JPanel header = new JPanel(new GridLayout(1, 7));
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));
        
        // Se colocan los dias en un String de Objetos

        String[] nombresDias = {"Domingo",  "Lunes", "Martes","Miércoles" , "Jueves","Viernes", "Sábado"};
        LocalDate primerDia = weekStart;
        
        for (int i = 0; i < 7; i++) {
            LocalDate fecha = primerDia.plusDays(i);
            JPanel dayHeader = new JPanel();
            dayHeader.setLayout(new BorderLayout());
            dayHeader.setBackground(Color.WHITE);
            dayHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, GRID_COLOR));

            // Panel para el nombre del día y número
            JPanel labelPanel = new JPanel(new GridLayout(2, 1, 0, 2));
            labelPanel.setOpaque(false);

            // Nombre del día
            JLabel nombreDia = new JLabel(nombresDias[fecha.getDayOfWeek().getValue() % 7]);
            nombreDia.setHorizontalAlignment(SwingConstants.CENTER);
            nombreDia.setFont(new Font("Segoe UI", Font.BOLD, 13));
            nombreDia.setForeground(new Color(60, 64, 67));

            // Número del día
            JLabel numeroDia = new JLabel(String.valueOf(fecha.getDayOfMonth()));
            numeroDia.setHorizontalAlignment(SwingConstants.CENTER);
            numeroDia.setFont(new Font("Segoe UI", Font.PLAIN, 24));
            
            // Si es hoy, resaltar
            if (fecha.equals(LocalDate.now())) {
                numeroDia.setForeground(new Color(25, 118, 210));
                numeroDia.setFont(numeroDia.getFont().deriveFont(Font.BOLD));
            }

            labelPanel.add(nombreDia);
            labelPanel.add(numeroDia);
            
            // Centrar el panel de etiquetas en el header
            dayHeader.add(Box.createVerticalStrut(8), BorderLayout.NORTH);
            dayHeader.add(labelPanel, BorderLayout.CENTER);
            
            // Añadir listener de clic
            final LocalDate fechaFinal = fecha;
            dayHeader.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    // Buscar el CalendarioDashboard padre
                    Container parent = SwingUtilities.getWindowAncestor(VistaPanelSemanal.this);
                    if (parent instanceof CalendarioDashboard) {
                        ((CalendarioDashboard) parent).actualizarListaEventos(fechaFinal);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    dayHeader.setBackground(new Color(245, 245, 245));
                    dayHeader.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    dayHeader.setBackground(Color.WHITE);
                    dayHeader.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            header.add(dayHeader);
        }
        
        return header;
    }
    
    public void repintarSemana() {
        layer.removeAll();
        int ancho = layer.getWidth();
        if (ancho == 0) {
            ancho = getWidth() - 60;
        }
        int anchoDia = Math.max(ancho / 7, 100);
        layer.setPreferredSize(new Dimension(anchoDia * 7, HOUR_HEIGHT * 24));

        // 1. Fondo base blanco
        JPanel panelBase = new JPanel();
        panelBase.setBackground(Color.WHITE);
        panelBase.setBounds(0, 0, ancho, HOUR_HEIGHT * 24);
        layer.add(panelBase, Integer.valueOf(0));

        // 2. Columna del día actual
        if (weekStart != null) {
            LocalDate hoy = LocalDate.now();
            int indiceDiaActual = -1;
            for (int i = 0; i < 7; i++) {
                if (weekStart.plusDays(i).equals(hoy)) {
                    indiceDiaActual = i;
                    break;
                }
            }
            if (indiceDiaActual >= 0) {
                JPanel columnaDiaActual = new JPanel();
                columnaDiaActual.setBackground(TODAY_COLUMN_COLOR);
                columnaDiaActual.setBounds(indiceDiaActual * anchoDia, 0, anchoDia, HOUR_HEIGHT * 24);
                layer.add(columnaDiaActual, Integer.valueOf(1));
            }
        }

        // 3. Cuadrícula - Líneas verticales y horizontales
        JPanel cuadricula = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(GRID_COLOR);

                // Líneas verticales
                for (int dia = 0; dia <= 7; dia++) {
                    int x = dia * anchoDia;
                    g2.drawLine(x, 0, x, getHeight());
                }

                // Líneas horizontales
                for (int hora = 0; hora <= 24; hora++) {
                    int y = hora * HOUR_HEIGHT;
                    g2.drawLine(0, y, getWidth(), y);
                    
                    // Líneas de media hora (más claras)
                    if (hora < 24) {
                        g2.setColor(new Color(240, 240, 240));
                        g2.drawLine(0, y + HOUR_HEIGHT/2, getWidth(), y + HOUR_HEIGHT/2);
                        g2.setColor(GRID_COLOR);
                    }
                }
            }
        };
        cuadricula.setOpaque(false);
        cuadricula.setBounds(0, 0, ancho, HOUR_HEIGHT * 24);
        layer.add(cuadricula, Integer.valueOf(2));

        // 4. Horario laboral con transparencia
        JPanel horarioLaboral = new JPanel();
        horarioLaboral.setBackground(new Color(246, 248, 250, 100));
        horarioLaboral.setOpaque(false);
        horarioLaboral.setBounds(0, 9 * HOUR_HEIGHT, ancho, 8 * HOUR_HEIGHT);
        layer.add(horarioLaboral, Integer.valueOf(3));

        // 5. Eventos
        if (weekStart != null) {
            mostrarEventos(anchoDia);
        }

        // 6. Línea de hora actual
        pintarLineaTiempoActual(ancho);

        layer.revalidate();
        layer.repaint();
    }

    private void mostrarEventos(int anchoDia) {
        for (int dia = 0; dia < 7; dia++) {
            LocalDate fecha = weekStart.plusDays(dia);
            List<Evento> eventos = model.getEventos(fecha);

            for (Evento evento : eventos) {
                int inicioY = evento.getInicio().getHour() * HOUR_HEIGHT +
                           (evento.getInicio().getMinute() * HOUR_HEIGHT / 60);
                int altura = (int) Duration.between(evento.getInicio(), evento.getFin())
                                        .toMinutes() * HOUR_HEIGHT / 60;

                BloqueEvento bloque = new BloqueEvento(evento, model);
                bloque.setBounds(dia * anchoDia + 1, inicioY, anchoDia - 2, altura);
                layer.add(bloque, Integer.valueOf(4));
            }
        }
    }

    private void pintarLineaTiempoActual(int ancho) {
        LocalTime ahora = LocalTime.now();
        LocalDate hoy = LocalDate.now();
        
        for (int i = 0; i < 7; i++) {
            if (weekStart.plusDays(i).equals(hoy)) {
                int posicionY = ahora.getHour() * HOUR_HEIGHT + 
                             (ahora.getMinute() * HOUR_HEIGHT / 60);
                
                JPanel lineaTiempo = new JPanel();
                lineaTiempo.setBackground(new Color(255, 0, 0));
                lineaTiempo.setBounds(0, posicionY, ancho, 2);
                layer.add(lineaTiempo, Integer.valueOf(5));
                
                // Círculo indicador
                pintarCirculoTiempo(posicionY);
                break;
            }
        }
    }

    private void pintarCirculoTiempo(int posicionY) {
        JPanel circulo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                                  RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 0, 0));
                g2.fillOval(0, 0, 12, 12);
            }
        };
        circulo.setOpaque(false);
        circulo.setBounds(0, posicionY - 5, 12, 12);
        layer.add(circulo, Integer.valueOf(5));
    }
    
    public LocalDate getSemanaActual() {
    return this.weekStart;
}

    @Override
    public void addNotify() {
        super.addNotify();
        // Asegurar que el repintado ocurra después de que el componente esté visible
        SwingUtilities.invokeLater(() -> {
            // Establecer el tamaño inicial
            int width = getWidth() - 60;
            if (width > 0) {
                layer.setPreferredSize(new Dimension(width, HOUR_HEIGHT * 24));
            }
            repintarSemana();
        });
    }
}
