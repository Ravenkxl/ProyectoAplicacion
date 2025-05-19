package co.edu.uis.organizationapp.vista.calendario;

import javax.swing.JPanel;
import javax.swing.BorderFactory;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.RenderingHints;

public class PanelDeTiempo extends JPanel {
    private static final int HOUR_HEIGHT = 60;
    private static final Color TEXT_COLOR = new Color(100, 100, 100);
    private static final Color LINE_COLOR = new Color(230, 230, 230);
    
    public PanelDeTiempo() {
        setPreferredSize(new Dimension(60, HOUR_HEIGHT * 24));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, LINE_COLOR));
    }
    
    // Necesario para pintar el panel de tiempo
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        g2.setColor(TEXT_COLOR);
        
        FontMetrics fm = g2.getFontMetrics();
        
        for (int hour = 0; hour < 24; hour++) {
            String timeText = String.format("%02d:00", hour);
            int y = hour * HOUR_HEIGHT;
            int textWidth = fm.stringWidth(timeText);
            
            // Posicionar el texto alineado a la derecha con un margen
            int textX = getWidth() - textWidth - 8;
            int textY = y + 15; // Alinear el texto con las líneas de la cuadrícula
            
            g2.drawString(timeText, textX, textY);
        }
    }
}
