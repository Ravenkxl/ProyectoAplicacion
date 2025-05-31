package co.edu.uis.organizationapp.controlador.eventlisteners; // Asumiendo que este es el paquete correcto

import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import co.edu.uis.organizationapp.vista.calendario.CalendarioDashboard;
import co.edu.uis.organizationapp.vista.calendario.VistaPanelMensual; // Importa tu clase de vista mensual

import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import javax.swing.SwingUtilities; // Importa SwingUtilities

// La clase DayClickListener en sí misma hereda de MouseAdapter
public class DayClickListener extends MouseAdapter {

    private final LocalDate fecha;
    private final ModeloCalendario modelo;
    private final VistaPanelMensual vistaMensual;

    public DayClickListener(LocalDate fecha, ModeloCalendario modelo, VistaPanelMensual vista) {
        this.fecha = fecha;
        this.modelo = modelo;
        this.vistaMensual = vista;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Buscar el CalendarioDashboard padre
        Container parent = SwingUtilities.getWindowAncestor(vistaMensual);
        if (parent instanceof CalendarioDashboard) {
            CalendarioDashboard dashboard = (CalendarioDashboard) parent;
            dashboard.actualizarListaEventos(fecha);
        }
    }
}