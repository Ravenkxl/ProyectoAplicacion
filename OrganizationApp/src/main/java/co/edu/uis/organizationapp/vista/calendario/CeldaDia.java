package co.edu.uis.organizationapp.vista.calendario;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;

public class CeldaDia extends JPanel {

    private int dia;
    private boolean tieneEvento;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tieneEvento) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0, new Color(200, 230, 255),
                    getWidth(), getHeight(), new Color(120, 180, 220)));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
        }
        g.setColor(Color.DARK_GRAY);
        g.drawString(dia > 0 ? String.valueOf(dia) : "", 5, 15);
    }

    public void setDay(int d, boolean evt) {
        this.dia = d;
        this.tieneEvento = evt;
    }
}
