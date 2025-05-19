package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.controlador.eventlisteners.DayClickListener;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class VistaPanelMensual extends JPanel {

    private YearMonth mes;
    private ModeloCalendario modelo;

    public VistaPanelMensual(ModeloCalendario m) {
        this.modelo = m;
        this.mes = YearMonth.now();
        setLayout(new GridLayout(7, 7, 2, 2)); // 7x7 grid con 2px de espacio
        setBackground(new Color(218, 220, 224));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        repintarMes();
    }

    public void setMonth(YearMonth mesAño) {
        this.mes = mesAño;
        repintarMes();
    }
    
    public YearMonth  getMesActual() {
        return this.mes;
    }
    
    // Necesario para pintar el apartado del Mes

    public void repintarMes() {
        removeAll();
        
        // Encabezados de días
        String[] diasSemana = {"Domingo", "Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado"};
        for (String dia : diasSemana) {
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            
            JLabel label = new JLabel(dia, SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setForeground(new Color(60, 64, 67));
            label.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
            
            headerPanel.add(label, BorderLayout.CENTER);
            headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(218, 220, 224)));
            add(headerPanel);
        }
        
        // Calcular el primer día del mes
        int off = (mes.atDay(1).getDayOfWeek().getValue() + 6) % 7;
        LocalDate hoy = LocalDate.now();
        
        // Añadir celdas de días
        for (int i = 0; i < 42; i++) {
            CeldaDia cell = new CeldaDia();
            int dia = i - off + 1;
            
            if (dia >= 1 && dia <= mes.lengthOfMonth()) {
                LocalDate fecha = mes.atDay(dia);
                boolean esHoy = fecha.equals(hoy);
                cell.setDay(dia, !modelo.getEventos(fecha).isEmpty(), esHoy);
                cell.addMouseListener(new DayClickListener(fecha, modelo, this));
                cell.setBackground(Color.WHITE);
            } else {
                cell.setBackground(new Color(245, 245, 245));
            }
            
            add(cell);
        }
        
        revalidate();
        repaint();
    }
}
