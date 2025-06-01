package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Subtarea;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.awt.font.TextAttribute;

public class TareaRenderer extends JPanel implements ListCellRenderer<TareaCheckBox> {
    private final JCheckBox checkbox;
    private final JLabel label;
    private final JLabel fechaLimite;
    private static final Color COLOR_VENCIDA = new Color(255, 99, 71);
    private static final Color COLOR_COMPLETADA = new Color(144, 238, 144);
    private static final Font FONT_VENCIDA = new Font("Segoe UI", Font.BOLD, 11);
    
    public TareaRenderer() {
        setLayout(new BorderLayout(5, 0));
        checkbox = new JCheckBox();
        
        JPanel contenido = new JPanel(new BorderLayout(5, 0));
        contenido.setOpaque(false);
        
        label = new JLabel();
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        fechaLimite = new JLabel();
        fechaLimite.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        fechaLimite.setForeground(Color.GRAY);
        
        contenido.add(label, BorderLayout.CENTER);
        contenido.add(fechaLimite, BorderLayout.EAST);
        
        add(checkbox, BorderLayout.WEST);
        add(contenido, BorderLayout.CENTER);
        setOpaque(true);
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends TareaCheckBox> list, 
            TareaCheckBox value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            
        if (value.isSubtarea()) {
            // Estilo para subtareas - Usar solo indentación
            setBorder(BorderFactory.createEmptyBorder(1, 35, 1, 1));
            label.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            label.setText(value.getSubtarea().getTitulo());
            setBackground(new Color(250, 250, 250));
            checkbox.setBackground(getBackground());
            fechaLimite.setText("");
        } else {
            // Estilo para tareas principales
            setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 1));
            label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            label.setText(value.getTarea().getTitulo());
            setBackground(Color.WHITE);
            checkbox.setBackground(getBackground());
            
            // Solo mostrar fecha límite para tareas principales
            if (value.getTarea() != null && value.getTarea().getFechaLimite() != null) {
                fechaLimite.setText(formatearFechaLimite(value.getTarea().getFechaLimite()));
            } else {
                fechaLimite.setText("");
            }
        }
        
        // Estado del checkbox
        checkbox.setSelected(value.isCompletada());
        
        // Modificar la lógica de estado vencido/completado
        if (value.isCompletada()) {
            Map<TextAttribute, Object> attributes = new HashMap<>();
            attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
            label.setFont(label.getFont().deriveFont(attributes));
            setBackground(COLOR_COMPLETADA);
            fechaLimite.setText("Completada");
        } else if (!value.isSubtarea() && value.getTarea() != null && value.getTarea().estaVencida()) {
            setBackground(COLOR_VENCIDA);
            fechaLimite.setText("¡VENCIDA!");
            fechaLimite.setFont(FONT_VENCIDA);
            fechaLimite.setForeground(Color.WHITE);
        } else {
            setBackground(list.getBackground());
            fechaLimite.setFont(new Font("Segoe UI", Font.ITALIC, 11));
            fechaLimite.setForeground(Color.GRAY);
        }
        
        // Resaltar selección
        if (isSelected) {
            setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(list.getSelectionBackground(), 1),
                getBorder()
            ));
        }
        
        return this;
    }
    
    private String formatearFechaLimite(LocalDateTime fecha) {
        if (fecha == null) return "";
        LocalDateTime ahora = LocalDateTime.now();
        long diasDiferencia = ChronoUnit.DAYS.between(ahora, fecha);
        
        if (diasDiferencia == 0) {
            return "Hoy " + fecha.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (diasDiferencia == 1) {
            return "Mañana " + fecha.format(DateTimeFormatter.ofPattern("HH:mm"));
        } else if (diasDiferencia > 1 && diasDiferencia < 7) {
            return fecha.format(DateTimeFormatter.ofPattern("EEEE HH:mm", new Locale("es")));
        } else {
            return fecha.format(DateTimeFormatter.ofPattern("d MMM HH:mm", new Locale("es")));
        }
    }

    // Agregar método auxiliar para mostrar subtareas
    private void mostrarSubtareas(TareaCheckBox value, DefaultListModel<TareaCheckBox> model, int index) {
        if (value.getTarea().getSubtareas() != null) {
            for (Subtarea subtarea : value.getTarea().getSubtareas()) {
                TareaCheckBox subtareaCheckBox = new TareaCheckBox(null);
                subtareaCheckBox.setSubtarea(subtarea);
                model.add(index + 1, subtareaCheckBox);
            }
        }
    }
}
