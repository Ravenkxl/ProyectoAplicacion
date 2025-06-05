package co.edu.uis.organizationapp.modelo.calendario;

import javax.swing.*;
import java.awt.*;

public class TareaRenderer extends JCheckBox implements ListCellRenderer<TareaCheckBox> {
    
    public TareaRenderer() {
        setOpaque(true);
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends TareaCheckBox> list,
            TareaCheckBox value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        
        setText(value.toString());
        setSelected(value.isCompletada());
        
        if (value.isSubtarea()) {
            // Indentar subtareas
            setBorder(BorderFactory.createEmptyBorder(2, 20, 2, 2));
        } else {
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        }
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        // Tachar el texto si está completada
        if (value.isCompletada()) {
            setText("<html><strike>" + getText() + "</strike></html>");
        }
        
        return this;
    }
}
