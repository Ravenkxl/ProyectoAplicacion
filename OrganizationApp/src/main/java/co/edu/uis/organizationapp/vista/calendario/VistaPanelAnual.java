package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import java.awt.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.Locale;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class VistaPanelAnual extends JPanel {
    private Year añoActual;
    private ModeloCalendario modelo;
    private JPanel contenedorMeses;
    private JLabel labelAño;
    private JTabbedPane parentTabbedPane;

    public VistaPanelAnual(ModeloCalendario modelo) {
        this.modelo = modelo;
        this.añoActual = Year.now();
        inicializarComponentes();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Encabezado con el año
        labelAño = new JLabel(String.valueOf(añoActual.getValue()));
        labelAño.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelAño.setHorizontalAlignment(SwingConstants.CENTER);
        add(labelAño, BorderLayout.NORTH);
        
        // Grid de meses (4x3)
        contenedorMeses = new JPanel(new GridLayout(4, 3, 10, 10));
        add(new JScrollPane(contenedorMeses), BorderLayout.CENTER);
        
        actualizarVista();
    }
    
    public void establecerAño(Year año) {
        if (!this.añoActual.equals(año)) {
            this.añoActual = año;
            labelAño.setText(String.valueOf(año.getValue()));
            actualizarVista();
            revalidate();
            repaint();
        }
    }
    
    public void actualizarVista() {
        contenedorMeses.removeAll();
        
        for (Month mes : Month.values()) {
            contenedorMeses.add(crearPanelMes(mes));
        }
        
        contenedorMeses.revalidate();
        contenedorMeses.repaint();
    }
    
    public Year getAñoActual() {
        return this.añoActual;
    }
    
    private JPanel crearPanelMes(Month mes) {
        JPanel panelMes = new JPanel(new BorderLayout());
        panelMes.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        
        // Encabezado del mes
        JLabel labelMes = new JLabel(mes.getDisplayName(TextStyle.FULL, new Locale("es", "ES")));
        labelMes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelMes.setHorizontalAlignment(SwingConstants.CENTER);
        labelMes.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        panelMes.add(labelMes, BorderLayout.NORTH);
        
        // Grid de días
        JPanel gridDias = new JPanel(new GridLayout(0, 7, 1, 1));
        gridDias.setBackground(new Color(230, 230, 230));
        
        // Nombres cortos de los días
        String[] nombresDias = {"D", "L", "M", "M", "J", "V", "S"};
        for (String dia : nombresDias) {
            JLabel l = new JLabel(dia, SwingConstants.CENTER);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            gridDias.add(l);
        }
        
        // Calcular días del mes
        YearMonth yearMonth = YearMonth.of(añoActual.getValue(), mes);
        LocalDate firstOfMonth = yearMonth.atDay(1);
        int offset = firstOfMonth.getDayOfWeek().getValue() % 7;
        
        // Días vacíos
        for (int i = 0; i < offset; i++) {
            gridDias.add(new JLabel());
        }
        
        // Días del mes
        for (int i = 1; i <= yearMonth.lengthOfMonth(); i++) {
            JLabel dayLabel = new JLabel(String.valueOf(i), SwingConstants.CENTER);
            LocalDate fecha = yearMonth.atDay(i);
            
            if (fecha.equals(LocalDate.now())) {
                dayLabel.setForeground(new Color(25, 118, 210));
                dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD));
            }
            
            if (!modelo.getEventos(fecha).isEmpty()) {
                dayLabel.setForeground(new Color(76, 175, 80));
            }
            
            gridDias.add(dayLabel);
        }
        
        panelMes.add(gridDias, BorderLayout.CENTER);
        
        // Hacer el panel clickeable
        panelMes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Buscar el TabbedPane padre
                Container parent = VistaPanelAnual.this.getParent();
                while (parent != null && !(parent instanceof JTabbedPane)) {
                    parent = parent.getParent();
                }
                
                if (parent instanceof JTabbedPane) {
                    JTabbedPane tabbedPane = (JTabbedPane) parent;
                    // Buscar el índice de la vista mensual
                    for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                        if (tabbedPane.getComponentAt(i) instanceof VistaPanelMensual) {
                            VistaPanelMensual vistaMensual = (VistaPanelMensual) tabbedPane.getComponentAt(i);
                            // Establecer el mes seleccionado
                            vistaMensual.setMonth(YearMonth.of(añoActual.getValue(), mes));
                            // Cambiar a la vista mensual
                            tabbedPane.setSelectedIndex(i);
                            break;
                        }
                    }
                }
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                panelMes.setBackground(new Color(245, 245, 245));
                panelMes.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panelMes.setBackground(UIManager.getColor("Panel.background"));
                panelMes.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        
        return panelMes;
    }
}
