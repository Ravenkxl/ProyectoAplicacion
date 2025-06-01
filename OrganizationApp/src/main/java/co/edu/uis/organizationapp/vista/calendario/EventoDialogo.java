package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import co.edu.uis.organizationapp.modelo.calendario.Tarea;
import co.edu.uis.organizationapp.modelo.calendario.Subtarea;
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
    private Evento evento; // Add this field
    private JTextField txtTitulo;
    private JTextField txtNombre;
    private JTextField txtNombreSubtarea;
    private JTextArea txtDesc;
    private JDateChooser dateChooser;
    private JSpinner spinnerHoraInicio;
    private JSpinner spinnerHoraFin;
    private boolean modificado = false;
    private Tarea tareaEditando = null; // Agregar esta variable de clase

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
        String nombreTarea = txtNombre.getText().trim();
        
        if (nombreTarea.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "El nombre de la tarea no puede estar vacío",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si estamos editando, actualizar la tarea existente
        if (tareaEditando != null) {
            tareaEditando.setTitulo(nombreTarea);
            tareaEditando.setDescripcion(txtDesc.getText());
            tareaEditando.setFecha(dateChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            tareaEditando.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
            tareaEditando.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
            tareaEditando.setFechaLimite(LocalDateTime.of(
                tareaEditando.getFecha(),
                tareaEditando.getFin()
            ));
            
            // Actualizar el evento en el modelo
            model.actualizarEvento(evento);
        } else {
            // Crear nueva tarea
            Tarea nuevaTarea = new Tarea();
            nuevaTarea.setTitulo(nombreTarea);
            nuevaTarea.setDescripcion(txtDesc.getText());
            nuevaTarea.setFecha(dateChooser.getDate().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate());
            nuevaTarea.setInicio(toLocalTime((Date) spinnerHoraInicio.getValue()));
            nuevaTarea.setFin(toLocalTime((Date) spinnerHoraFin.getValue()));
            nuevaTarea.setFechaLimite(LocalDateTime.of(
                nuevaTarea.getFecha(),
                nuevaTarea.getFin()
            ));

            if (evento != null) {
                evento.agregarTarea(nuevaTarea);
                model.actualizarEvento(evento);
            } else {
                evento = new Evento();
                evento.setFecha(nuevaTarea.getFecha());
                evento.setTitulo("Evento: " + nuevaTarea.getTitulo());
                evento.setDescripcion(nuevaTarea.getDescripcion());
                evento.setInicio(nuevaTarea.getInicio());
                evento.setFin(nuevaTarea.getFin());
                evento.agregarTarea(nuevaTarea);
                model.addEvento(evento);
            }
        }
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
        JButton btnAgregarSubtarea = new JButton("Nueva Subtarea");
        
        // Ocultar botón de subtarea inicialmente - solo se muestra al editar
        btnAgregarSubtarea.setVisible(false);
        
        if (evento != null) {
            // Si estamos editando, mostrar el botón de subtarea
            btnAgregarSubtarea.setVisible(true);
        }

        buttonPanel.add(btnAgregarSubtarea);
        buttonPanel.add(btnGuardar);
        buttonPanel.add(btnCancelar);
        
        btnAgregarSubtarea.addActionListener(e -> {
        if (evento != null) {
            JTextField txtSubtarea = new JTextField(20);
            Object[] message = {
                "Nombre de la Subtarea:", txtSubtarea
            };
            
            int option = JOptionPane.showConfirmDialog(
                this, 
                message, 
                "Nueva Subtarea", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
            );
                
            if (option == JOptionPane.OK_OPTION && !txtSubtarea.getText().trim().isEmpty()) {
                // Crear la nueva subtarea
                Subtarea subtarea = new Subtarea();
                subtarea.setTitulo(txtSubtarea.getText().trim());
                
                // Obtener la tarea actual que se está editando
                if (tareaEditando != null) {
                    // Agregar subtarea a la tarea existente
                    subtarea.setTareaPadre(tareaEditando);
                    tareaEditando.agregarSubtarea(subtarea);
                    
                    // Actualizar el evento en el modelo
                    model.actualizarEvento(evento);
                    modificado = true;
                }
            }
        }
    });

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

    public void editarTarea(Tarea tarea) {
        if (tarea != null) {
            this.tareaEditando = tarea; // Guardar referencia a la tarea que estamos editando
            
            txtNombre.setText(tarea.getTitulo());
            txtDesc.setText(tarea.getDescripcion());
            if (tarea.getFecha() != null) {
                dateChooser.setDate(java.sql.Date.valueOf(tarea.getFecha()));
            }
            if (tarea.getInicio() != null) {
                Date inicioDate = Date.from(tarea.getInicio().atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault()).toInstant());
                spinnerHoraInicio.setValue(inicioDate);
            }
            if (tarea.getFin() != null) {
                Date finDate = Date.from(tarea.getFin().atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault()).toInstant());
                spinnerHoraFin.setValue(finDate);
            }
            
            // Mostrar botón de subtarea
            for (Component comp : getContentPane().getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel)comp;
                    for (Component btn : panel.getComponents()) {
                        if (btn instanceof JButton && ((JButton)btn).getText().equals("Nueva Subtarea")) {
                            btn.setVisible(true);
                            break;
                        }
                    }
                }
            }
        }
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
        // Pre-fill fields with event data
        if (evento != null) {
            txtNombre.setText(evento.getTitulo());
            txtDesc.setText(evento.getDescripcion());
            if (evento.getFecha() != null) {
                dateChooser.setDate(java.sql.Date.valueOf(evento.getFecha()));
            }
            if (evento.getInicio() != null) {
                Date inicioDate = Date.from(evento.getInicio().atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault()).toInstant());
                spinnerHoraInicio.setValue(inicioDate);
            }
            if (evento.getFin() != null) {
                Date finDate = Date.from(evento.getFin().atDate(LocalDate.now())
                    .atZone(ZoneId.systemDefault()).toInstant());
                spinnerHoraFin.setValue(finDate);
            }
        }
    }
}
