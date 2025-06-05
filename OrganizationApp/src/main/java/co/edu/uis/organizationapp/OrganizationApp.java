package co.edu.uis.organizationapp;

import co.edu.uis.organizationapp.vista.calendario.CalendarioDashboard;
import co.edu.uis.Notas.Nota;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import co.edu.uis.organizationapp.cronometro.Cronometro;

public class OrganizationApp extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private CalendarioDashboard calendarioDashboard;
    private Nota notasPanel;
    private JPanel calendarioYCronometroPanel;

    public OrganizationApp() {
        setTitle("Organizador Académico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(1200, 800));
        initUI();
    }

    private void initUI() {
        // Menú superior con iconos (robusto y tamaño reducido)
        JMenuBar menuBar = new JMenuBar();
        JButton btnNotas = new JButton("Notas", loadIcon("/icons/notas.png", 32, 32));
        JButton btnCalendario = new JButton("Calendario", loadIcon("/icons/calendario.png", 32, 32));
        JButton btnCronometro = new JButton("Cronómetro", loadIcon("/icons/cronometro.png", 32, 32));
        menuBar.add(btnNotas);
        menuBar.add(btnCalendario);
        menuBar.add(btnCronometro);
        setJMenuBar(menuBar);

        // Panel central con CardLayout
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Panel de Notas
        notasPanel = new Nota();
        mainPanel.add(notasPanel.getContentPane(), "NOTAS");

        // Panel de Calendario + Cronómetro
        calendarioDashboard = new CalendarioDashboard();
        calendarioYCronometroPanel = new JPanel(new BorderLayout());
        calendarioYCronometroPanel.add(calendarioDashboard.getContentPane(), BorderLayout.CENTER);
        // Aquí puedes agregar el cronómetro debajo del calendario
        // Por ejemplo: calendarioYCronometroPanel.add(new CronometroPanel(), BorderLayout.SOUTH);
        mainPanel.add(calendarioYCronometroPanel, "CALENDARIO");

        add(mainPanel, BorderLayout.CENTER);

        // Listeners de menú
        btnNotas.addActionListener(e -> cardLayout.show(mainPanel, "NOTAS"));
        btnCalendario.addActionListener(e -> cardLayout.show(mainPanel, "CALENDARIO"));
        btnCronometro.addActionListener(e -> mostrarCronometroPopUp());

        // Mostrar por defecto Notas
        cardLayout.show(mainPanel, "NOTAS");
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        java.net.URL location = getClass().getResource(path);
        if (location != null) {
            ImageIcon icon = new ImageIcon(location);
            Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }

    private void mostrarCronometroPopUp() {
        Cronometro cronometro = new Cronometro();
        JDialog dialog = new JDialog(this, "Cronómetro", false);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setContentPane(cronometro.getContentPane());
        dialog.setVisible(true);
    }

    /**
     * Punto de entrada principal de la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrganizationApp().setVisible(true));
    }
} 