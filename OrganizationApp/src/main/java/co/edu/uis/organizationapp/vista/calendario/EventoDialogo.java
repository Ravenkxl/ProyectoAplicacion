package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import co.edu.uis.organizationapp.modelo.calendario.Tarea;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.util.Date;
import java.util.Calendar;
import net.miginfocom.swing.MigLayout;

public class EventoDialogo extends JDialog {

    private ModeloCalendario model;
    private LocalDate fecha;
    private JTextField txtTitulo;
    private JTextField txtNombre;
    private JTextArea txtDesc;
    private JDateChooser dateChooser;
    private JSpinner spinnerHoraInicio;
    private JSpinner spinnerHoraFin;
    private boolean modificado = false;

    public EventoDialogo(JFrame owner, ModeloCalendario model, LocalDate fecha, boolean isTarea) {
        super(owner, "Evento para " + fecha, true);
        if (isTarea) {
            this.model = model;
            this.fecha = fecha;
            crearUITarea();
        }
    }

    public EventoDialogo(JFrame owner, ModeloCalendario model, LocalDate fecha) {
        super(owner, "Evento para " + fecha, true);
        this.model = model;
        this.fecha = fecha;
        crearUI();
    }

    public EventoDialogo(Window parent, ModeloCalendario modelo, Evento eventoExistente) {
        super(parent, "Editar Evento", ModalityType.APPLICATION_MODAL);
        this.model = modelo;
        this.fecha = eventoExistente.getFecha();

        crearUI();

        // Rellenar campos con datos del evento
        txtTitulo.setText(eventoExistente.getTitulo());
        txtDesc.setText(eventoExistente.getDescripcion());
        dateChooser.setDate(java.sql.Date.valueOf(eventoExistente.getFecha()));

        // Convertir LocalTime a Date para los spinners
        Date inicioDate = Date.from(eventoExistente.getInicio().atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault()).toInstant());
        Date finDate = Date.from(eventoExistente.getFin().atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault()).toInstant());

        spinnerHoraInicio.setValue(inicioDate);
        spinnerHoraFin.setValue(finDate);

        // Modificar el comportamiento del botón guardar
        Container buttonsPanel = (Container) getContentPane().getComponent(1);
        if (buttonsPanel instanceof JPanel) {
            JButton btnGuardar = (JButton) ((JPanel) buttonsPanel).getComponent(0);
            btnGuardar.removeActionListener(btnGuardar.getActionListeners()[0]);
            btnGuardar.addActionListener(e -> {
                eventoExistente.setTitulo(txtTitulo.getText());
                eventoExistente.setDescripcion(txtDesc.getText());
                eventoExistente.setFecha(dateChooser.getDate().toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDate());
                eventoExistente.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
                eventoExistente.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
                modificado = true;
                dispose();
            });
        }
    }

    public boolean fueModificado() {
        return modificado;
    }

    private void crearUI() {
        setLayout(new BorderLayout(10, 10));

        // Panel principal con MigLayout
        JPanel mainPanel = new JPanel(new MigLayout("insets 15, wrap 2", "[][grow,fill]"));

        // Título
        mainPanel.add(new JLabel("Título:"));
        txtTitulo = new JTextField(20);
        mainPanel.add(txtTitulo);

        // Fecha
        mainPanel.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(fecha));
        mainPanel.add(dateChooser);

        // Hora inicio
        mainPanel.add(new JLabel("Hora inicio:"));
        spinnerHoraInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm");
        spinnerHoraInicio.setEditor(editorInicio);
        mainPanel.add(spinnerHoraInicio);

        // Hora fin
        mainPanel.add(new JLabel("Hora fin:"));
        spinnerHoraFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinnerHoraFin, "HH:mm");
        spinnerHoraFin.setEditor(editorFin);
        mainPanel.add(spinnerHoraFin);

        // Descripción
        mainPanel.add(new JLabel("Descripción:"), "top");
        txtDesc = new JTextArea(5, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        mainPanel.add(scrollDesc);

        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        btnGuardar.addActionListener(e -> {
            guardarEvento();
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        add(buttonPanel, BorderLayout.SOUTH);

        // Establecer tamaño y posición
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void guardarEvento() {
        Evento ev = new Evento();
        ev.setFecha(dateChooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        ev.setTitulo(txtTitulo.getText());
        ev.setDescripcion(txtDesc.getText());
        ev.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
        ev.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
        model.addEvento(ev);
        modificado = true;
    }
    
   private void guardarTarea() {
        Tarea task = new Tarea();
        task.setFecha(dateChooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate());
        task.setTitulo(txtNombre.getText());
        task.setDescripcion(txtDesc.getText());
        task.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
        task.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
        modificado = true;
    }

    private LocalTime toLocalTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime()
                .withSecond(0).withNano(0);
    }

    private void crearUITarea() {
        setLayout(new BorderLayout(10, 10));

        // Panel principal con MigLayout
        JPanel mainPanel = new JPanel(new MigLayout("insets 15, wrap 2", "[][grow,fill]"));

        // Título
        mainPanel.add(new JLabel("Nombre de la tarea:"));
        txtNombre = new JTextField(20);
        mainPanel.add(txtNombre);

        // Fecha
        mainPanel.add(new JLabel("Fecha:"));
        dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(fecha));
        mainPanel.add(dateChooser);

        // Hora inicio
        mainPanel.add(new JLabel("Hora inicio:"));
        spinnerHoraInicio = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorInicio = new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm");
        spinnerHoraInicio.setEditor(editorInicio);
        mainPanel.add(spinnerHoraInicio);

        // Hora fin
        mainPanel.add(new JLabel("Hora fin:"));
        spinnerHoraFin = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor editorFin = new JSpinner.DateEditor(spinnerHoraFin, "HH:mm");
        spinnerHoraFin.setEditor(editorFin);
        mainPanel.add(spinnerHoraFin);

        // Descripción
        mainPanel.add(new JLabel("Descripción:"), "top");
        txtDesc = new JTextArea(5, 20);
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        mainPanel.add(scrollDesc);

        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);

        btnGuardar.addActionListener(e -> {
            guardarTarea();
            dispose();
        });

        btnCancelar.addActionListener(e -> dispose());

        add(buttonPanel, BorderLayout.SOUTH);

        // Establecer tamaño y posición
        pack();
        setLocationRelativeTo(getOwner());
    }
}
