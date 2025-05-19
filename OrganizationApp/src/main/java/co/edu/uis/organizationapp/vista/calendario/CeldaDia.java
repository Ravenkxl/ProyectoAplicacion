package co.edu.uis.organizationapp.vista.calendario;

import java.awt.*;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;

public class CeldaDia extends JPanel {
    private int dia;
    private boolean tieneEvento;
    private boolean esHoy;
    private boolean isHovered = false;
    private static final Color COLOR_HOY = new Color(232, 242, 255);
    private static final Color COLOR_EVENTO = new Color(25, 118, 210);
    private static final Color HOVER_COLOR = new Color(245, 245, 245);
    private Color originalBackground;

    public CeldaDia() {
        setPreferredSize(new Dimension(0, 0));  // Dejar que el layout manager decida el tamaño
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(new Color(218, 220, 224)));
        setOpaque(true);
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (dia > 0) {
                    isHovered = true;
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    setBackground(HOVER_COLOR);
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (dia > 0) {
                    isHovered = false;
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if (esHoy) {
                        setBackground(COLOR_HOY);
                    } else {
                        setBackground(Color.WHITE);
                    }
                    repaint();
                }
            }
        });
    }
    
    // Necesario para pintar las celdas
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (dia <= 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fondo para hover
        if (isHovered) {
            g2.setColor(HOVER_COLOR);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Pintar fondo si es hoy
        if (esHoy) {
            g2.setColor(COLOR_HOY);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }

        // Número del día en la esquina superior izquierda
        if (esHoy) {
            g2.setColor(COLOR_EVENTO);
            int size = 26;
            g2.fillOval(8, 8, size, size);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            String diaStr = String.valueOf(dia);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(diaStr, 8 + (size - fm.stringWidth(diaStr)) / 2,
                    8 + ((size - fm.getHeight()) / 2) + fm.getAscent());
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            g2.drawString(String.valueOf(dia), 10, 25);
        }

        // Indicador de evento
        if (tieneEvento) {
            g2.setColor(COLOR_EVENTO);
            int y = getHeight() / 2;
            g2.fillRoundRect(10, y, getWidth() - 20, 6, 3, 3);
        }
    }
    
    // Coloca el dia

    public void setDay(int dia, boolean tieneEvento, boolean esHoy) {
        this.dia = dia;
        this.tieneEvento = tieneEvento;
        this.esHoy = esHoy;

        // Establecer el color de fondo original
        if (esHoy) {
            originalBackground = COLOR_HOY;
        } else {
            originalBackground = Color.WHITE;
        }
        setBackground(originalBackground);

        repaint();
    }
}
