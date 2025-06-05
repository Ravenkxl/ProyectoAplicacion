package co.edu.uis.Notas;

/**
 *
 * @author Karol Hernandez
 */
import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.StyledEditorKit;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonToken;
import co.edu.uis.Notas.DataManager;

public class Nota extends javax.swing.JFrame{
    private JTree navegacionTree;
    private DefaultTreeModel treeModel;
    private JTextPane contenidoPane;
    private JToolBar edicionToolbar;
    private DefaultMutableTreeNode root;
    private JPanel notasPanel;
    private JPanel apuntesPanel;
    private CardLayout cardLayout;
    private JPanel contentCards;
    private JPanel mainContentPanel;
    private JPanel carpetasPanel;
    private Color selectedColor;
    // Modificar las estructuras de datos
    private Map<String, MateriaData> materiasData;
    
    private final Gson gson;
    private static final String DATA_FILE = "notas_data.json";

    private Gson createGson() {
        return new GsonBuilder()
            .registerTypeAdapter(Color.class, new TypeAdapter<Color>() {
                @Override
                public void write(JsonWriter writer, Color color) throws IOException {
                    if (color == null) {
                        writer.nullValue();
                        return;
                    }
                    writer.beginObject();
                    writer.name("rgb").value(color.getRGB());
                    writer.endObject();
                }

                @Override
                public Color read(JsonReader reader) throws IOException {
                    if (reader.peek() == JsonToken.NULL) {
                        reader.nextNull();
                        return Color.WHITE;
                    }
                    reader.beginObject();
                    reader.nextName(); // "rgb"
                    Color color = new Color(reader.nextInt(), true);
                    reader.endObject();
                    return color;
                }
            })
            .setPrettyPrinting()
            .create();
    }

    // Clase interna para mantener los datos de cada materia
    private static class MateriaData implements Serializable {
        private static final long serialVersionUID = 1L;
        private Color color;
        private Map<String, String> temas;
        private ArrayList<NotaData> notas; // Changed from List to ArrayList
        private double acumulado;
        
        public MateriaData(Color color) {
            this.color = color != null ? color : Color.WHITE;
            this.temas = new HashMap<>();
            this.notas = new ArrayList<>();
            this.acumulado = 0.0;
        }
        
        public void addTema(String nombre) {
            if (!temas.containsKey(nombre)) {
                temas.put(nombre, "");
            }
        }
        
        public void setContenidoTema(String tema, String contenido) {
            temas.put(tema, contenido);
        }
        
        public String getContenidoTema(String tema) {
            return temas.getOrDefault(tema, "");
        }
        
        public void addNota(String titulo, double nota, double porcentaje) {
            notas.add(new NotaData(titulo, nota, porcentaje));
            calcularAcumulado();
        }
        
        private void calcularAcumulado() {
            acumulado = 0.0;
            double totalPorcentaje = 0.0;
            
            for (NotaData nota : notas) {
                acumulado += (nota.nota * nota.porcentaje / 100);
                totalPorcentaje += nota.porcentaje;
            }
            
            // Ajustar el acumulado si el total de porcentajes es diferente de 100
            if (totalPorcentaje > 0) {
                acumulado = acumulado * (100 / totalPorcentaje);
            }
        }
        
        public java.util.List<NotaData> getNotas() {
            return Collections.unmodifiableList(notas);
        }
        
        public double getNotaTotal() {
            return acumulado;
        }
        
        public Set<String> getTemas() {
            return Collections.unmodifiableSet(temas.keySet());
        }
    }

    private static class NotaData implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String titulo;
        private final double nota;
        private final double porcentaje;
        
        public NotaData(String titulo, double nota, double porcentaje) {
            this.titulo = titulo;
            this.nota = nota;
            this.porcentaje = porcentaje;
        }
        
        public Object[] toTableRow() {
            return new Object[]{
                titulo,
                nota,
                porcentaje,
                (nota * porcentaje / 100)
            };
        }
    }
    

    /**
     * Creates new form Nota
     */
    public Nota() {
        initComponents();
        this.gson = createGson();
        this.materiasData = cargarDatos();
        contenidoPane = new JTextPane();
        edicionToolbar = new JToolBar();
        configurarVentana();
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                guardarDatos();
            }
        });
    }
    
    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Materias");
    }

    private void configurarVentana() {
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 153, 255));
        JLabel titleLabel = new JLabel("Sistema de Notas", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        // Main content with cards
        mainContentPanel = new JPanel(new CardLayout());
        mainContentPanel.setBackground(Color.WHITE);
        carpetasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        carpetasPanel.setBackground(Color.WHITE);
        
        // Agregar botón de nueva materia
        JPanel addMateriaPanel = createFolderPanel("+ Nueva Materia", null);
        addMateriaPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                crearNuevaMateria();
            }
        });
        carpetasPanel.add(addMateriaPanel);

        // AGREGAR TODAS LAS MATERIAS GUARDADAS AL INICIAR
        for (Map.Entry<String, MateriaData> entry : materiasData.entrySet()) {
            String nombre = entry.getKey();
            Color color = entry.getValue().color;
            JPanel materiaPanel = createFolderPanel(nombre, color);
            materiaPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrarContenidoMateria(nombre);
                }
            });
            // Insertar antes del botón '+ Nueva Materia'
            carpetasPanel.add(materiaPanel, carpetasPanel.getComponentCount() - 1);
        }
        
        JScrollPane scrollPane = new JScrollPane(carpetasPanel);
        mainContentPanel.add(scrollPane, "MATERIAS");
        
        // Configurar paneles de contenido
        contentCards = new JPanel(new CardLayout());
        apuntesPanel = createApuntesPanel();
        notasPanel = createNotasPanel();
        
        mainContentPanel.add(apuntesPanel, "APUNTES");
        mainContentPanel.add(notasPanel, "NOTAS");
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        setContentPane(mainPanel);
    }

    private JPanel createFolderPanel(String nombre, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(120, 100));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        panel.setBackground(color != null ? color : Color.WHITE);
        
        JLabel iconLabel = new JLabel(UIManager.getIcon("FileView.directoryIcon"));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel nameLabel = new JLabel(nombre, SwingConstants.CENTER);
        
        panel.add(iconLabel, BorderLayout.CENTER);
        panel.add(nameLabel, BorderLayout.SOUTH);
        return panel;
    }

    private void crearNuevaMateria() {
        JTextField nombreField = new JTextField();
        JButton colorButton = new JButton("Seleccionar Color");
        selectedColor = Color.WHITE;
        
        colorButton.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Seleccionar Color", selectedColor);
            if (newColor != null) {
                selectedColor = newColor;
                colorButton.setBackground(selectedColor);
            }
        });
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nombre:"));
        panel.add(nombreField);
        panel.add(new JLabel("Color:"));
        panel.add(colorButton);
        
        int result = JOptionPane.showConfirmDialog(this, panel, "Nueva Materia", 
            JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION && !nombreField.getText().trim().isEmpty()) {
            String nombre = nombreField.getText().trim();
            // Validar duplicados (ignorando mayúsculas/minúsculas y espacios)
            boolean existe = materiasData.keySet().stream()
                .anyMatch(existing -> existing.trim().equalsIgnoreCase(nombre));
            if (existe) {
                JOptionPane.showMessageDialog(this, "Ya existe una materia con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JPanel materiaPanel = createFolderPanel(nombre, selectedColor);
            materiasData.put(nombre, new MateriaData(selectedColor));
            materiaPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    mostrarContenidoMateria(nombre);
                }
            });
            carpetasPanel.add(materiaPanel, carpetasPanel.getComponentCount() - 1);
            carpetasPanel.revalidate();
            carpetasPanel.repaint();
            guardarDatos(); // Guardar después de crear materia
        }
    }

    private JPanel createApuntesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Panel superior que contendrá toolbar y botón volver
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Toolbar de edición
        edicionToolbar = new JToolBar();
        edicionToolbar.setFloatable(false);
        JButton boldBtn = new JButton("Negrita");
        JButton italicBtn = new JButton("Cursiva");
        JButton colorBtn = new JButton("Color");
        JComboBox<String> sizeCombo = new JComboBox<>(new String[]{"12", "14", "16", "18", "20", "24"});
        
        // Configurar botones
        boldBtn.addActionListener(e -> {
            StyledDocument doc = contenidoPane.getStyledDocument();
            int start = contenidoPane.getSelectionStart();
            int end = contenidoPane.getSelectionEnd();
            if (start != end) {
                javax.swing.text.Style style = contenidoPane.addStyle("Bold", null);
                boolean isBold = StyleConstants.isBold(contenidoPane.getCharacterAttributes());
                StyleConstants.setBold(style, !isBold);
                doc.setCharacterAttributes(start, end - start, style, false);
            }
        });

        italicBtn.addActionListener(e -> {
            StyledDocument doc = contenidoPane.getStyledDocument();
            int start = contenidoPane.getSelectionStart();
            int end = contenidoPane.getSelectionEnd();
            if (start != end) {
                javax.swing.text.Style style = contenidoPane.addStyle("Italic", null);
                boolean isItalic = StyleConstants.isItalic(contenidoPane.getCharacterAttributes());
                StyleConstants.setItalic(style, !isItalic);
                doc.setCharacterAttributes(start, end - start, style, false);
            }
        });

        colorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Seleccionar Color", Color.BLACK);
            if (newColor != null) {
                StyledDocument doc = contenidoPane.getStyledDocument();
                int start = contenidoPane.getSelectionStart();
                int end = contenidoPane.getSelectionEnd();
                if (start != end) {
                    javax.swing.text.Style style = contenidoPane.addStyle("Color", null);
                    StyleConstants.setForeground(style, newColor);
                    doc.setCharacterAttributes(start, end - start, style, false);
                }
            }
        });

        sizeCombo.addActionListener(e -> {
            int start = contenidoPane.getSelectionStart();
            int end = contenidoPane.getSelectionEnd();
            if (start != end) {
                StyledDocument doc = contenidoPane.getStyledDocument();
                javax.swing.text.Style style = contenidoPane.addStyle("Size", null);
                StyleConstants.setFontSize(style, Integer.parseInt((String)sizeCombo.getSelectedItem()));
                doc.setCharacterAttributes(start, end - start, style, false);
            }
        });
        
        edicionToolbar.add(boldBtn);
        edicionToolbar.add(italicBtn);
        edicionToolbar.add(colorBtn);
        edicionToolbar.add(new JLabel("Tamaño: "));
        edicionToolbar.add(sizeCombo);
        
        // Agregar botón volver en un panel separado
        JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton volverBtn = new JButton("Volver");  // Changed from "Volver a Temas" to "Volver"
        volverBtn.addActionListener(e -> {
            guardarDatos();
            ((CardLayout)mainContentPanel.getLayout()).first(mainContentPanel);
        });
        volverPanel.add(volverBtn);
        
        // Organizar toolbar y botón volver
        topPanel.add(edicionToolbar, BorderLayout.CENTER);
        topPanel.add(volverPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(contenidoPane), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createNotasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Evaluación", "Nota", "Porcentaje", "Ponderado"}, 0
        );
        JTable table = new JTable(model);
        
        JButton addBtn = new JButton("Agregar Nota");
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(addBtn);
        
        panel.add(controlPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private void mostrarContenidoMateria(String materia) {
        JPanel materiaPanel = new JPanel(new BorderLayout());
        materiaPanel.setBackground(Color.WHITE);

        // Panel que contiene temas y notas lado a lado
        JPanel horizontalPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        horizontalPanel.setBackground(Color.WHITE);

        // Panel izquierdo para temas
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        JPanel carpetasTemasPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        carpetasTemasPanel.setBackground(Color.WHITE);

        MateriaData data = materiasData.get(materia);
        if (data != null) {
            // Mostrar temas existentes
            for (String tema : data.getTemas()) {
                JPanel temaPanel = createFolderPanel(tema, data.color);
                temaPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        mostrarContenidoTema(materia, tema);
                    }
                });
                carpetasTemasPanel.add(temaPanel);
            }
            
            // Agregar botón de nuevo tema
            JPanel addTemaPanel = createFolderPanel("+ Nuevo Tema", null);
            addTemaPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    crearNuevoTema(materia);
                }
            });
            carpetasTemasPanel.add(addTemaPanel);
        }
        
        leftPanel.add(new JScrollPane(carpetasTemasPanel), BorderLayout.CENTER);
        
        // Panel derecho para notas
        JPanel rightPanel = createNotasPanelForMateria(materia);
        
        horizontalPanel.add(leftPanel);
        horizontalPanel.add(rightPanel);
        
        materiaPanel.add(horizontalPanel, BorderLayout.CENTER);
        
        // Botón volver
        JButton volverBtn = new JButton("Volver");
        volverBtn.addActionListener(e -> 
            ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIAS"));
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(volverBtn);
        materiaPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        mainContentPanel.add(materiaPanel, "MATERIA_" + materia);
        ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIA_" + materia);
    }

    private JPanel createNotasPanelForMateria(String materia) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Notas de " + materia);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tabla y botón
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Evaluación", "Nota", "Porcentaje", "Ponderado"}, 0
        );
        JTable table = new JTable(model);
        
        JButton addBtn = new JButton("Agregar Nota");
        addBtn.addActionListener(e -> agregarNota(materia));
        
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(addBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(controlPanel, BorderLayout.SOUTH);
        
        // Cargar notas existentes
        MateriaData data = materiasData.get(materia);
        if (data != null) {
            for (NotaData nota : data.getNotas()) {
                model.addRow(nota.toTableRow());
            }
        }
        
        return panel;
    }

    private void mostrarContenidoTema(String materia, String tema) {
        MateriaData data = materiasData.get(materia);
        if (data != null) {
            contenidoPane.setText(data.getContenidoTema(tema));
            ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "APUNTES");
            
            // Guardar texto cuando cambie
            contenidoPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { save(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { save(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { save(); }
                
                private void save() {
                    data.setContenidoTema(tema, contenidoPane.getText());
                }
            });
        }
    }

    private void mostrarNotasMateria(String materia) {
        MateriaData data = materiasData.get(materia);
        if (data != null) {
            // Crear nuevo panel específico para notas de la materia
            JPanel notasMateriaPanel = new JPanel(new BorderLayout());
            
            // Tabla de notas
            DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Evaluación", "Nota", "Porcentaje", "Ponderado"}, 0
            ) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable table = new JTable(model);
            
            // Panel superior
            JPanel topPanel = new JPanel(new BorderLayout());
            JLabel materiaLabel = new JLabel("Notas de " + materia);
            materiaLabel.setFont(new Font("Arial", Font.BOLD, 16));
            JLabel acumuladoLabel = new JLabel(String.format("Nota Final: %.2f", data.getNotaTotal()));
            acumuladoLabel.setFont(new Font("Arial", Font.BOLD, 14));
            
            topPanel.add(materiaLabel, BorderLayout.WEST);
            topPanel.add(acumuladoLabel, BorderLayout.EAST);
            
            // Botón agregar nota
            JButton addNotaBtn = new JButton("Agregar Nota");
            addNotaBtn.addActionListener(e -> agregarNota(materia));
            
            // Botón volver
            JButton volverBtn = new JButton("Volver");
            volverBtn.addActionListener(e -> 
                ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIA_" + materia));
            
            // Panel de botones
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(addNotaBtn);
            buttonPanel.add(volverBtn);
            
            // Llenar tabla
            for (NotaData nota : data.getNotas()) {
                model.addRow(nota.toTableRow());
            }
            
            notasMateriaPanel.add(topPanel, BorderLayout.NORTH);
            notasMateriaPanel.add(new JScrollPane(table), BorderLayout.CENTER);
            notasMateriaPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            mainContentPanel.add(notasMateriaPanel, "NOTAS_" + materia);
            ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "NOTAS_" + materia);
        }
    }

    // Add agregarNota method
    private void agregarNota(String materia) {
        JTextField tituloField = new JTextField();
        JTextField notaField = new JTextField();
        JTextField porcentajeField = new JTextField();
        
        JPanel inputPanel = new JPanel(new GridLayout(3, 2));
        inputPanel.add(new JLabel("Título:"));
        inputPanel.add(tituloField);
        inputPanel.add(new JLabel("Nota (0-5):"));
        inputPanel.add(notaField);
        inputPanel.add(new JLabel("Porcentaje (%):"));
        inputPanel.add(porcentajeField);
        
        int result = JOptionPane.showConfirmDialog(this, inputPanel, 
            "Nueva nota para " + materia, JOptionPane.OK_CANCEL_OPTION);
            
        if (result == JOptionPane.OK_OPTION) {
            try {
                double nota = Double.parseDouble(notaField.getText());
                double porcentaje = Double.parseDouble(porcentajeField.getText());
                
                if (nota >= 0 && nota <= 5 && porcentaje > 0 && porcentaje <= 100) {
                    MateriaData data = materiasData.get(materia);
                    if (data != null) {
                        data.addNota(tituloField.getText(), nota, porcentaje);
                        mostrarNotasMateria(materia);
                        guardarDatos(); // Guardar después de agregar nota
                    }
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Nota debe estar entre 0-5 y porcentaje entre 1-100",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor ingrese valores numéricos válidos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void crearNuevoTema(String materia) {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del tema:");
        if (nombre != null && !nombre.trim().isEmpty()) {
            MateriaData data = materiasData.get(materia);
            if (data != null) {
                data.addTema(nombre);
                mostrarContenidoMateria(materia);
                guardarDatos(); // Guardar después de crear tema
            }
        }
    }

    private Map<String, MateriaData> cargarDatos() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            return gson.fromJson(reader, new com.google.gson.reflect.TypeToken<HashMap<String, MateriaData>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al cargar los datos. Se iniciará con datos vacíos.",
                "Error", JOptionPane.WARNING_MESSAGE);
            return new HashMap<>();
        }
    }

    private void guardarDatos() {
        try (Writer writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            gson.toJson(materiasData, writer);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error al guardar los datos: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Nota.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        java.awt.EventQueue.invokeLater(() -> {
            new Nota().setVisible(true);
        });
    }
}
