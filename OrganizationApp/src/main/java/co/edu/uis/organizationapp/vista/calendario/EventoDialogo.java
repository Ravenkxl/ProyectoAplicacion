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
    private final ModeloCalendario model;
    private final LocalDate fecha;
    private JTextField txtTitulo;
    private JTextArea txtDesc;
    private JDateChooser dateChooser;
    private JSpinner spinnerHoraInicio;
    private JSpinner spinnerHoraFin;
    private boolean modificado = false;
    private Evento eventoActual = null;

    public EventoDialogo(JFrame owner, ModeloCalendario model, LocalDate fecha, boolean isTarea) {
        super(owner, "Nuevo Evento", true);
        this.model = model;
        this.fecha = fecha;
        crearUI();
    }

    public boolean fueModificado() {
        return modificado;
    }

    public void setEvento(Evento evento) {
        this.eventoActual = evento;
        if (evento != null) {
            txtTitulo.setText(evento.getTitulo());
            txtDesc.setText(evento.getDescripcion());
            dateChooser.setDate(java.sql.Date.valueOf(evento.getFecha()));
            
            Calendar calInicio = Calendar.getInstance();
            calInicio.set(Calendar.HOUR_OF_DAY, evento.getInicio().getHour());
            calInicio.set(Calendar.MINUTE, evento.getInicio().getMinute());
            spinnerHoraInicio.setValue(calInicio.getTime());
            
            Calendar calFin = Calendar.getInstance();
            calFin.set(Calendar.HOUR_OF_DAY, evento.getFin().getHour());
            calFin.set(Calendar.MINUTE, evento.getFin().getMinute());
            spinnerHoraFin.setValue(calFin.getTime());
        }
    }

    public void editarTarea(Tarea tarea) {
        if (tarea != null && eventoActual != null) {
            setTitle("Editar Tarea");
            txtTitulo.setText(tarea.getTitulo());
            if (tarea.getDescripcion() != null) {
                txtDesc.setText(tarea.getDescripcion());
            }
        }
    }

    private void crearUI() {
        // Configuración básica de la ventana
        setTitle("Nuevo Evento");
        setSize(400, 500);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout(10, 10));
        setResizable(false);
        getRootPane().setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new MigLayout("insets 15, wrap 2", "[][grow,fill]", "[]10[]10[]10[]10[]"));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Título
        mainPanel.add(new JLabel("Título:"), "align label");
        txtTitulo = new JTextField();
        txtTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(txtTitulo, "grow, wrap");

        // Fecha
        mainPanel.add(new JLabel("Fecha:"), "align label");
        dateChooser = new JDateChooser();
        dateChooser.setDate(java.sql.Date.valueOf(fecha));
        dateChooser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(dateChooser, "grow, wrap");

        // Hora de inicio
        mainPanel.add(new JLabel("Hora de inicio:"), "align label");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, LocalTime.now().getHour());
        cal.set(Calendar.MINUTE, LocalTime.now().getMinute());
        
        SpinnerDateModel modeloInicio = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        spinnerHoraInicio = new JSpinner(modeloInicio);
        spinnerHoraInicio.setEditor(new JSpinner.DateEditor(spinnerHoraInicio, "HH:mm"));
        spinnerHoraInicio.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(spinnerHoraInicio, "grow, wrap");

        // Hora de fin
        mainPanel.add(new JLabel("Hora de fin:"), "align label");
        cal.add(Calendar.HOUR_OF_DAY, 1);
        SpinnerDateModel modeloFin = new SpinnerDateModel(cal.getTime(), null, null, Calendar.MINUTE);
        spinnerHoraFin = new JSpinner(modeloFin);
        spinnerHoraFin.setEditor(new JSpinner.DateEditor(spinnerHoraFin, "HH:mm"));
        spinnerHoraFin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(spinnerHoraFin, "grow, wrap");

        // Descripción
        mainPanel.add(new JLabel("Descripción:"), "align label");
        txtDesc = new JTextArea(5, 20);
        txtDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(txtDesc);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mainPanel.add(scrollPane, "grow, h 100!");

        add(mainPanel, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");

        btnGuardar.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Estilizar botones
        btnGuardar.putClientProperty("JButton.buttonType", "roundRect");
        btnCancelar.putClientProperty("JButton.buttonType", "roundRect");
        
        // Color especial para el botón guardar
        btnGuardar.setBackground(new Color(0, 120, 212));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);        btnGuardar.addActionListener(e -> handleGuardar());
        
        btnCancelar.addActionListener(e -> dispose());

        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private boolean validarCampos() {
        if (txtTitulo.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese un título para el evento",
                "Campo requerido",
                JOptionPane.WARNING_MESSAGE);
            txtTitulo.requestFocus();
            return false;
        }

        Date horaInicio = (Date) spinnerHoraInicio.getValue();
        Date horaFin = (Date) spinnerHoraFin.getValue();
        if (horaInicio.after(horaFin)) {
            JOptionPane.showMessageDialog(this,
                "La hora de inicio debe ser anterior a la hora de fin",
                "Error en horas",
                JOptionPane.WARNING_MESSAGE);
            spinnerHoraInicio.requestFocus();
            return false;
        }

        return true;
    }

    private void guardarEvento() {
        Evento nuevoEvento = new Evento();
        nuevoEvento.setTitulo(txtTitulo.getText().trim());
        nuevoEvento.setDescripcion(txtDesc.getText().trim());
        
        // Convertir la fecha seleccionada
        LocalDate fechaSeleccionada = dateChooser.getDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        nuevoEvento.setFecha(fechaSeleccionada);
        
        // Convertir las horas seleccionadas
        nuevoEvento.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
        nuevoEvento.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
        
        // Guardar en el modelo
        model.addEvento(nuevoEvento);
        modificado = true;
        dispose();
    }

    private LocalTime toLocalTime(Date date) {
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalTime();
    }

    private void handleGuardar() {
        if (validarCampos()) {
            guardarEvento();
        }
    }
}
