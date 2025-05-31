package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import co.edu.uis.organizationapp.modelo.calendario.ModeloCalendario;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Container;

public class BloqueEvento extends JPanel {
    private final Evento evento;
    private final ModeloCalendario modelo;

    public BloqueEvento(Evento ev, ModeloCalendario modelo) {
        this.evento = ev;
        this.modelo = modelo;
        setBackground(ev.getColor() != null ? ev.getColor() : new Color(25, 118, 210));
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(new Color(13, 71, 161), 1));
        setLayout(new BorderLayout(2, 2));

        // Panel para el título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(getBackground());
        headerPanel.setOpaque(true);
        
        JLabel titulo = new JLabel(ev.getTitulo());
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        headerPanel.add(titulo, BorderLayout.CENTER);
        
        // Hora del evento
        JLabel hora = new JLabel(ev.getInicio().format(DateTimeFormatter.ofPattern("HH:mm")));
        hora.setForeground(Color.WHITE);
        hora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        headerPanel.add(hora, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Descripción
        if (ev.getDescripcion() != null && !ev.getDescripcion().isEmpty()) {
            JLabel desc = new JLabel(ev.getDescripcion());
            desc.setForeground(Color.WHITE);
            desc.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            desc.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));
            add(desc, BorderLayout.CENTER);
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(getBackground().brighter());
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                setBorder(BorderFactory.createLineBorder(new Color(13, 71, 161), 2));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(evento.getColor() != null ? evento.getColor() : new Color(25, 118, 210));
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                setBorder(BorderFactory.createLineBorder(new Color(13, 71, 161), 1));
            }
            
            @Override
            public void mouseClicked(MouseEvent evt) {
                // Buscar el CalendarioDashboard padre
                Container parent = SwingUtilities.getWindowAncestor(BloqueEvento.this);
                if (parent instanceof CalendarioDashboard dashboard) {
                    dashboard.actualizarListaEventos(evento.getFecha());
                    dashboard.mostrarDetallesEvento(evento);
                }
            }
        });
    }
}
