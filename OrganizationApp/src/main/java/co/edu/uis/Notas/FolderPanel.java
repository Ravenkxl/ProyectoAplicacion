package co.edu.uis.Notas;

import javax.swing.*;
import java.awt.*;

public class FolderPanel extends JPanel {
    private String name;
    private Color color;
    private boolean isAddButton;
    
    public FolderPanel(String name, Color color, boolean isAddButton) {
        this.name = name;
        this.color = color;
        this.isAddButton = isAddButton;
        
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(120, 100));
        setBorder(BorderFactory.createLineBorder(Color.GRAY));
        setBackground(color != null ? color : Color.WHITE);
        
        Icon icon = isAddButton ? 
            UIManager.getIcon("FileView.directoryIcon") :
            UIManager.getIcon("FileView.fileIcon");
            
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
        
        add(iconLabel, BorderLayout.CENTER);
        add(nameLabel, BorderLayout.SOUTH);
    }
}
