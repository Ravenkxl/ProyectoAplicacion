package co.edu.uis.organizationapp;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class OrganizationApp {

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());
    }
}
