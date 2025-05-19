package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.*;
import java.util.List;
import javax.swing.*;
import java.time.format.TextStyle;
import java.util.Locale;

public class VistaPanelDiaria extends JPanel {

    private static final int HOUR_HEIGHT = 60;
    private static final int HEADER_HEIGHT = 50;
    private static final Color GRID_COLOR = new Color(230, 230, 230);
    private static final Color CURRENT_TIME_COLOR = new Color(255, 0, 0);
    private static final Color WORK_HOURS_COLOR = new Color(246, 248, 250, 100);

    private LocalDate diaActual;
    private ModeloCalendario modelo;
    private JLayeredPane capaContenido;
    private JScrollPane scrollPrincipal;  // Añadir como campo de clase

    public VistaPanelDiaria(ModeloCalendario modelo) {
        this.modelo = modelo;
        this.diaActual = LocalDate.now();
//        initComponents();
        inicializarComponentes();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout());

        // Panel contenedor para el timePanel y su espaciador
        JPanel timeContainer = new JPanel(new BorderLayout());
        timeContainer.setBackground(Color.WHITE);

        // Agregar un panel espaciador que coincida con el header
        JPanel spacer = new JPanel();
        spacer.setPreferredSize(new Dimension(60, HEADER_HEIGHT));
        spacer.setBackground(Color.WHITE);
        timeContainer.add(spacer, BorderLayout.NORTH);

        // Panel de horas
        PanelDeTiempo panelTiempo = new PanelDeTiempo();
        JScrollPane scrollTiempo = new JScrollPane(panelTiempo);
        scrollTiempo.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrollTiempo.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollTiempo.setBorder(null);
        timeContainer.add(scrollTiempo, BorderLayout.CENTER);

        add(timeContainer, BorderLayout.WEST);

        // Panel principal
        capaContenido = new JLayeredPane() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(getParent() != null ? getParent().getWidth() - 60 : 800,
                        HOUR_HEIGHT * 24);
            }
        };
        capaContenido.setBackground(Color.WHITE);
        capaContenido.setOpaque(true);

        scrollPrincipal = new JScrollPane(capaContenido);
        scrollPrincipal.getVerticalScrollBar().setUnitIncrement(30);
        scrollPrincipal.setColumnHeaderView(crearEncabezadoDia());
        scrollPrincipal.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, GRID_COLOR));

        // Sincronizar scrolls
        scrollPrincipal.getVerticalScrollBar().addAdjustmentListener(e -> {
            if (scrollTiempo != null) {
                scrollTiempo.getVerticalScrollBar().setValue(e.getValue());
            }
        });

        add(scrollPrincipal, BorderLayout.CENTER);

        // Scroll inicial a las 8 AM
        SwingUtilities.invokeLater(() -> {
            scrollPrincipal.getVerticalScrollBar().setValue(8 * HOUR_HEIGHT);
        });

        actualizarVista();
    }

    private JPanel crearEncabezadoDia() {
        JPanel header = new JPanel(new BorderLayout());
        header.setPreferredSize(new Dimension(0, HEADER_HEIGHT));
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRID_COLOR));

        // Nombre del día y fecha
        String nombreDia = diaActual.getDayOfWeek().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        JLabel labelDia = new JLabel(nombreDia + " " + diaActual.getDayOfMonth());
        labelDia.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelDia.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(labelDia, BorderLayout.CENTER);

        // Hacer el header clickeable
        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Buscar el CalendarioDashboard padre
                Container parent = SwingUtilities.getWindowAncestor(VistaPanelDiaria.this);
                if (parent instanceof CalendarioDashboard) {
                    ((CalendarioDashboard) parent).actualizarListaEventos(diaActual);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                header.setBackground(new Color(245, 245, 245));
                header.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                header.setBackground(Color.WHITE);
                header.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });

        return header;
    }

    public void establecerDia(LocalDate fecha) {
        this.diaActual = fecha;
        actualizarEncabezado();
        actualizarVista();
    }

    private void actualizarEncabezado() {
        if (scrollPrincipal != null) {
            scrollPrincipal.setColumnHeaderView(crearEncabezadoDia());
            scrollPrincipal.revalidate();
        }
    }

    public void actualizarVista() {
        capaContenido.removeAll();
        int ancho = capaContenido.getWidth();
        if (ancho == 0) {
            ancho = getWidth() - 60;
        }
        capaContenido.setPreferredSize(new Dimension(ancho, HOUR_HEIGHT * 24));

        // 1. Panel base blanco
        JPanel fondo = new JPanel();
        fondo.setBackground(Color.WHITE);
        fondo.setOpaque(true);
        fondo.setBounds(0, 0, ancho, HOUR_HEIGHT * 24);
        capaContenido.add(fondo, Integer.valueOf(1));

        // 2. Cuadrícula como panel personalizado
        JPanel cuadricula = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Líneas horizontales
                for (int hora = 0; hora <= 24; hora++) {
                    int y = hora * HOUR_HEIGHT;

                    // Línea de hora
                    g2.setColor(GRID_COLOR);
                    g2.drawLine(0, y, getWidth(), y);

                    // Línea de media hora
                    if (hora < 24) {
                        g2.setColor(new Color(240, 240, 240));
                        g2.drawLine(0, y + HOUR_HEIGHT / 2, getWidth(), y + HOUR_HEIGHT / 2);
                    }
                }
            }
        };
        cuadricula.setOpaque(false);
        cuadricula.setBounds(0, 0, ancho, HOUR_HEIGHT * 24);
        capaContenido.add(cuadricula, Integer.valueOf(2));

        // 3. Horario laboral semitransparente
        JPanel horarioLaboral = new JPanel();
        horarioLaboral.setBackground(new Color(246, 248, 250, 50));
        horarioLaboral.setOpaque(false);
        horarioLaboral.setBounds(0, 9 * HOUR_HEIGHT, ancho, 8 * HOUR_HEIGHT);
        capaContenido.add(horarioLaboral, Integer.valueOf(3));

        // 4. Eventos
        pintarEventos(ancho);

        // 5. Línea de tiempo actual con capa más alta
        if (diaActual.equals(LocalDate.now())) {
            LocalTime ahora = LocalTime.now();
            int posicionY = ahora.getHour() * HOUR_HEIGHT
                    + (ahora.getMinute() * HOUR_HEIGHT / 60);

            // Línea roja - usando una capa más alta que POPUP_LAYER
            JPanel lineaTiempo = new JPanel();
            lineaTiempo.setBackground(new Color(255, 0, 0));
            lineaTiempo.setBounds(0, posicionY, ancho, 2);
            capaContenido.add(lineaTiempo, JLayeredPane.DRAG_LAYER);

            // Círculo indicador
            JPanel circulo = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(255, 0, 0));
                    g2.fillOval(0, 0, 12, 12);
                }
            };
            circulo.setOpaque(false);
            circulo.setBounds(0, posicionY - 5, 12, 12);
            capaContenido.add(circulo, JLayeredPane.DRAG_LAYER);

            // Timer para actualizar cada minuto
            Timer timer = new Timer(60000, e -> actualizarVista());
            timer.setRepeats(true);
            timer.start();
        }

        capaContenido.revalidate();
        capaContenido.repaint();
    }

    private void pintarEventos(int ancho) {
        List<Evento> eventos = modelo.getEventos(diaActual);
        for (Evento evento : eventos) {
            int inicioY = evento.getInicio().getHour() * HOUR_HEIGHT
                    + (evento.getInicio().getMinute() * HOUR_HEIGHT / 60);
            int altura = (int) Duration.between(evento.getInicio(), evento.getFin())
                    .toMinutes() * HOUR_HEIGHT / 60;

            BloqueEvento bloque = new BloqueEvento(evento, modelo);
            bloque.setOpaque(true);
            altura = Math.max(altura, 25);
            bloque.setBounds(10, inicioY, ancho - 20, altura);
            
            capaContenido.add(bloque, JLayeredPane.PALETTE_LAYER);
        }
    }

    public LocalDate getDiaActual() {
        return this.diaActual;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(() -> {
            int width = getWidth() - 60;
            if (width > 0) {
                capaContenido.setPreferredSize(new Dimension(width, HOUR_HEIGHT * 24));
            }
            actualizarVista();
        });
    }
}
