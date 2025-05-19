package co.edu.uis.organizationapp.vista.calendario;

import javax.swing.*;
import java.awt.*;

public class TareaRenderer extends JPanel implements ListCellRenderer<TareaCheckBox> {
    private final JCheckBox checkbox;
    private final JLabel label;
    
    public TareaRenderer() {
        setLayout(new BorderLayout(5, 0));
        checkbox = new JCheckBox();
        label = new JLabel();
        add(checkbox, BorderLayout.WEST);
        add(label, BorderLayout.CENTER);
        setOpaque(true);
    }
    
    @Override
    public Component getListCellRendererComponent(
            JList<? extends TareaCheckBox> list, 
            TareaCheckBox value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
            
        checkbox.setSelected(value.isCompletada());
        label.setText(value.getTextoMostrar());
        
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        return this;
    }
}
