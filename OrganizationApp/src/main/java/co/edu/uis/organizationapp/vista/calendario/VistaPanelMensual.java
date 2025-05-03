package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.controlador.eventlisteners.DayClickListener;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import static java.awt.FlowLayout.CENTER;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class VistaPanelMensual extends JPanel {

    private YearMonth mes;
    private ModeloCalendario modelo;

    public VistaPanelMensual(ModeloCalendario m) {
        this.modelo = m;
        setLayout(new GridLayout(0, 7));
        repaintMonth();
    }

    public void setMonth(YearMonth mesAño) {
        this.mes = mesAño;
        repaintMonth();
    }

    private void repaintMonth() {
        removeAll();
        // 7 encabezados: L, M, X, J, V, S, D
        for (String d : List.of("L", "M", "X", "J", "V", "S", "D")) {
            add(new JLabel(d, CENTER));
        }
        // calcula offset y añade 42 celdas:
        int off = (mes.atDay(1).getDayOfWeek().getValue() + 6) % 7;
        for (int i = 0; i < 42; i++) {
            CeldaDia cell = new CeldaDia();
            int dia = i - off + 1;
            if (dia >= 1 && dia <= mes.lengthOfMonth()) {
                LocalDate fecha = mes.atDay(dia);
                cell.setDay(dia, !modelo.getEventos(fecha).isEmpty());
                cell.addMouseListener(new DayClickListener(fecha, modelo, this));
            }
            add(cell);
        }
        revalidate();
        repaint();
    }
}
