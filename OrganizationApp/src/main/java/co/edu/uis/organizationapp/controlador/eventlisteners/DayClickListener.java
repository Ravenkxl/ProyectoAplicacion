package co.edu.uis.organizationapp.controlador.eventlisteners; // Asumiendo que este es el paquete correcto

import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
// Importa la clase EventoDialogo desde donde la tengas definida
import co.edu.uis.organizationapp.vista.calendario.EventoDialogo; // <<-- Importación correcta de tu EventoDialogo
import co.edu.uis.organizationapp.vista.calendario.VistaPanelMensual; // Importa tu clase de vista mensual

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import javax.swing.SwingUtilities; // Importa SwingUtilities
import javax.swing.JFrame; // Importa JFrame

// La clase DayClickListener en sí misma hereda de MouseAdapter
public class DayClickListener extends MouseAdapter {

    private LocalDate fechaDia;
    private ModeloCalendario modeloCalendario;
    private VistaPanelMensual vistaPanelMensual; // Referencia a la vista para refrescar

    public DayClickListener(LocalDate fechaDia, ModeloCalendario modeloCalendario, VistaPanelMensual vistaPanelMensual) {
        this.fechaDia = fechaDia;
        this.modeloCalendario = modeloCalendario;
        this.vistaPanelMensual = vistaPanelMensual;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Verificar si fue un clic izquierdo
        if (SwingUtilities.isLeftMouseButton(e)) {

            // --- Obtener la ventana padre (JFrame) que contiene vistaPanelMensual ---
            // SwingUtilities.getWindowAncestor() encuentra la ventana de nivel superior que contiene este componente.
            JFrame ownerFrame = (JFrame) SwingUtilities.getWindowAncestor(vistaPanelMensual);

            // Verificar que encontramos una ventana padre
            if (ownerFrame != null) {
                // --- Abrir el EventoDialogo, pasando el JFrame padre, la fecha y el modelo ---
                // Llama al constructor de EventoDialogo
                EventoDialogo dialog = new EventoDialogo(ownerFrame, modeloCalendario, fechaDia);

                // Muestra el diálogo (esto bloquea el hilo actual hasta que el diálogo se cierre)
                dialog.setVisible(true);

            } else {
                System.err.println("Error: No se pudo encontrar la ventana principal (JFrame) del panel de calendario.");
            }
        }
    }
}   