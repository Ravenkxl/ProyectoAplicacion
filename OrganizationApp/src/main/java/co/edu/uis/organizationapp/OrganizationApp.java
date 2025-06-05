package co.edu.uis.organizationapp;

import co.edu.uis.organizationapp.vista.calendario.CalendarioDashboard;
import co.edu.uis.Notas.Nota;
import javax.swing.*;
import java.awt.*;
import co.edu.uis.organizationapp.cronometro.Cronometro;
import co.edu.uis.organizationapp.modelo.Usuario;
import co.edu.uis.organizationapp.modelo.UsuarioManager;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

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
        JButton btnExportar = new JButton("Exportar Puntos", loadIcon("/icons/export.png", 32, 32));
        menuBar.add(btnExportar);
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
        btnExportar.addActionListener(e -> exportarPuntosImagen());

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

    private void exportarPuntosImagen() {
        Usuario usuario = UsuarioManager.cargarUsuario();
        int puntos = usuario.getPuntos();
        try {
            // 1. Cargar imagen de fondo
            java.net.URL fondoUrl = getClass().getResource("/icons/banner.png");
            if (fondoUrl == null) {
                JOptionPane.showMessageDialog(this, "No se encontró la imagen de fondo.");
                return;
            }
            BufferedImage fondo = ImageIO.read(fondoUrl);
            int width = fondo.getWidth();
            int height = fondo.getHeight();
            // 2. Crear copia para dibujar encima
            BufferedImage imagenConPuntaje = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = imagenConPuntaje.createGraphics();
            g.drawImage(fondo, 0, 0, null);

            // 3. Dibujar el puntaje (centrado, grande, con sombra)
            String texto = "Puntaje: " + puntos;
            g.setFont(new Font("Serif", Font.BOLD, 56));
            FontMetrics fm = g.getFontMetrics();
            int x = (width - fm.stringWidth(texto)) / 2;
            int y = 100; // Puedes ajustar la altura según el diseño
            // Sombra
            g.setColor(new Color(0,0,0,120));
            g.drawString(texto, x+4, y+4);
            // Texto principal
            g.setColor(new Color(255, 215, 0)); // Dorado
            g.drawString(texto, x, y);
            g.dispose();

            // 4. Mostrar preview en un JDialog
            JLabel previewLabel = new JLabel(new ImageIcon(imagenConPuntaje));
            JOptionPane.showMessageDialog(this, previewLabel, "Preview de tu puntaje", JOptionPane.PLAIN_MESSAGE);

            // 5. Preguntar si desea exportar
            int confirm = JOptionPane.showConfirmDialog(this, "¿Deseas guardar esta imagen?", "Exportar Puntaje", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("PNG Image","png"));
                int option = chooser.showSaveDialog(this);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    if (!file.getName().toLowerCase().endsWith(".png")) {
                        file = new File(file.getParentFile(), file.getName() + ".png");
                    }
                    try {
                        ImageIO.write(imagenConPuntaje, "png", file);
                        JOptionPane.showMessageDialog(this, "Imagen guardada en: " + file.getAbsolutePath());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Error al guardar imagen: " + ex.getMessage());
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar imagen: " + ex.getMessage());
        }
    }

    /**
     * Punto de entrada principal de la aplicación.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OrganizationApp().setVisible(true));
    }
} 