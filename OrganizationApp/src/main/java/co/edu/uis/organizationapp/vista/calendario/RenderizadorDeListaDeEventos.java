package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class RenderizadorDeListaDeEventos extends JPanel implements ListCellRenderer<Evento> {
    private final JLabel lblTitulo = new JLabel();
    private final JLabel lblHora = new JLabel();
    private final DateTimeFormatter formatoHora = DateTimeFormatter.ofPattern("HH:mm");

    public RenderizadorDeListaDeEventos() {
        setLayout(new BorderLayout(5, 0));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        
        add(lblTitulo, BorderLayout.CENTER);
        add(lblHora, BorderLayout.EAST);
    }

    @Override
    public Component getListCellRendererComponent(
            JList<? extends Evento> list, 
            Evento evento, 
            int index,
            boolean isSelected, 
            boolean cellHasFocus) {
        
        lblTitulo.setText(evento.getTitulo());
        lblHora.setText(evento.getInicio().format(formatoHora));

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
            lblTitulo.setForeground(list.getSelectionForeground());
            lblHora.setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
            lblTitulo.setForeground(list.getForeground());
            lblHora.setForeground(Color.GRAY);
        }

        return this;
    }
}
