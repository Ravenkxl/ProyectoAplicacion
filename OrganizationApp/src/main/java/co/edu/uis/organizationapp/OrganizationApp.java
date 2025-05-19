package co.edu.uis.organizationapp;

import co.edu.uis.organizationapp.vista.calendario.CalendarioDashboard;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.JFrame;

public class OrganizationApp {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
        
        SwingUtilities.invokeLater(() -> {
            CalendarioDashboard dashboard = new CalendarioDashboard();
            dashboard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            dashboard.setExtendedState(JFrame.MAXIMIZED_BOTH);
            dashboard.setVisible(true);
        });
    }
}
