package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.Date;

public class EventoDialogo extends JDialog {
    private ModeloCalendario model;
    private LocalDate fecha;
    private JTextField txtTitulo = new JTextField(20);
    private JTextArea txtDesc = new JTextArea(5, 20);
    private JSpinner spnHoraInicio, spnHoraFin;

    public EventoDialogo(JFrame owner, ModeloCalendario model, LocalDate fecha) {
        super(owner, "Evento para " + fecha, true);
        this.model = model;
        this.fecha = fecha;
        crearUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void crearUI() {
        setLayout(new BorderLayout());
        JPanel panel = new JPanel(new GridLayout(0,2,5,5));
        panel.add(new JLabel("Título:")); panel.add(txtTitulo);
        panel.add(new JLabel("Descripción:")); panel.add(new JScrollPane(txtDesc));
        panel.add(new JLabel("Hora inicio:"));
        spnHoraInicio = new JSpinner(new SpinnerDateModel());
        panel.add(spnHoraInicio);
        panel.add(new JLabel("Hora fin:"));
        spnHoraFin = new JSpinner(new SpinnerDateModel());
        panel.add(spnHoraFin);
        add(panel, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            Evento ev = new Evento();
            ev.setFecha(fecha);
            ev.setTitulo(txtTitulo.getText());
            ev.setDescripcion(txtDesc.getText());
            ev.setInicio(toLocalTime((Date)spnHoraInicio.getValue()));
            ev.setFin(toLocalTime((Date)spnHoraFin.getValue()));
            model.addEvento(ev);
            dispose();
        });
        add(btnGuardar, BorderLayout.SOUTH);
    }

    private LocalTime toLocalTime(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalTime()
                   .withSecond(0).withNano(0);
    }
}
