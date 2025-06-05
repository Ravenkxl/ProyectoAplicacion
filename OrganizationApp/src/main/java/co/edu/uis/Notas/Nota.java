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
import javax.swing.text.AttributeSet;
import java.util.Base64;

public class Nota extends javax.swing.JFrame {
    // Add these fields at the class level
    private JTable miniNotasTable;
    private JTable currentNotasTable;
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
    private String currentMateria; // Add this field to track the current materia
    
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
        private Map<String, String> temasAtributos; // Add this field
        
        public MateriaData(Color color) {
            this.color = color != null ? color : Color.WHITE;
            this.temas = new HashMap<>();
            this.notas = new ArrayList<>();
            this.acumulado = 0.0;
            this.temasAtributos = new HashMap<>();
        }
        
        public void addTema(String nombre) {
            if (!temas.containsKey(nombre)) {
                temas.put(nombre, "");
            }
        }

        public void setContenidoTema(String tema, String contenido, String atributos) {
            temas.put(tema, contenido);
            temasAtributos.put(tema, atributos);
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
            
            // No ajustar el acumulado si no hay 100%
            if (totalPorcentaje > 0) {
                acumulado = Math.round(acumulado * 100.0) / 100.0; // Redondear a 2 decimales
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
        contenidoPane.setEditable(true);
        contenidoPane.putClientProperty("caretWidth", 1); // Better caret
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
        
        // Panel superior
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
            JColorChooser colorChooser = new JColorChooser(Color.BLACK);
            final JDialog dialog = JColorChooser.createDialog(null,
                "Seleccionar Color",
                false, // non-modal
                colorChooser,
                event -> {
                    Color selected = colorChooser.getColor();
                    if (selected != null) {
                        StyledDocument doc = contenidoPane.getStyledDocument();
                        int start = contenidoPane.getSelectionStart();
                        int end = contenidoPane.getSelectionEnd();
                        if (start != end) {
                            javax.swing.text.Style style = contenidoPane.addStyle("Color", null);
                            StyleConstants.setForeground(style, selected);
                            doc.setCharacterAttributes(start, end - start, style, false);
                        }
                    }
                },
                null);
            dialog.setLocationRelativeTo(apuntesPanel);
            dialog.setVisible(true);
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
            if (currentMateria != null) {
                ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIA_" + currentMateria);
            } else {
                ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIAS");
            }
        });
        volverPanel.add(volverBtn);
        
        // Organizar toolbar y botón volver
        topPanel.add(edicionToolbar, BorderLayout.CENTER);
        topPanel.add(volverPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Create a panel to hold the scrollPane
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(new JScrollPane(contenidoPane), BorderLayout.CENTER);
        panel.add(contentPanel, BorderLayout.CENTER);
        
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
        materiaPanel.setName("MATERIA_" + materia); // Add this line
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
                    public void mouseClicked(MouseEvent e) {
                        Nota.this.mostrarContenidoTema(materia, tema);
                    }
                });
                carpetasTemasPanel.add(temaPanel);
            }
            
            // Agregar botón de nuevo tema
            JPanel addTemaPanel = createFolderPanel("+ Nuevo Tema", null);
            addTemaPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    Nota.this.crearNuevoTema(materia); // Fixed: Use explicit reference to outer class
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
        
        // Get materia data at the beginning
        MateriaData data = materiasData.get(materia);
        if (data == null) return panel;

        // Panel superior
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Notas de " + materia);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tabla y botones
        DefaultTableModel model = new DefaultTableModel(
            new Object[]{"Evaluación", "Nota", "Porcentaje", "Ponderado"}, 0
        );
        JTable table = new JTable(model);
        currentNotasTable = table;  // Store reference to main table
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton addBtn = new JButton("Agregar Nota");
        JButton deleteBtn = new JButton("Eliminar Nota");
        
        addBtn.addActionListener(e -> agregarNota(materia, table, model));
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    MateriaData data = materiasData.get(materia);
                    if (data != null) {
                        data.notas.remove(selectedRow);
                        data.calcularAcumulado();
                        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                        tableModel.removeRow(selectedRow);
                        
                        // Update the other table if it exists
                        JTable otherTable = (table == miniNotasTable) ? currentNotasTable : miniNotasTable;
                        if (otherTable != null) {
                            ((DefaultTableModel)otherTable.getModel()).removeRow(selectedRow);
                        }
                        guardarDatos();
                    }
                }
            }
        });
        
        buttonPanel.add(addBtn);
        buttonPanel.add(deleteBtn);
        
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Agregar panel para nota final en la esquina inferior derecha
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusPanel.setBackground(Color.WHITE);
        JLabel notaFinalLabel = new JLabel(String.format("Nota Final: %.2f", data.getNotaTotal()));
        notaFinalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusPanel.add(notaFinalLabel);
        
        // Panel para botones y nota final
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(buttonPanel, BorderLayout.WEST);
        bottomPanel.add(statusPanel, BorderLayout.EAST);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Cargar notas existentes
        if (data != null) {
            for (NotaData nota : data.getNotas()) {
                model.addRow(nota.toTableRow());
            }
        }
        
        return panel;
    }

    // Método para crear un nuevo tema en una materia
    private void crearNuevoTema(String materia) {
        String nombreTema = JOptionPane.showInputDialog(this, "Nombre del nuevo tema:", "Nuevo Tema", JOptionPane.PLAIN_MESSAGE);
        if (nombreTema != null && !nombreTema.trim().isEmpty()) {
            MateriaData data = materiasData.get(materia);
            if (data != null) {
                // Validar duplicados
                boolean existe = data.getTemas().stream().anyMatch(existing -> existing.trim().equalsIgnoreCase(nombreTema));
                if (existe) {
                    JOptionPane.showMessageDialog(this, "Ya existe un tema con ese nombre.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                data.addTema(nombreTema);
                guardarDatos();
                mostrarContenidoMateria(materia); // Refrescar la vista de la materia
            }
        }
    }

    // Implement guardarDatos method
    private void guardarDatos() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            gson.toJson(materiasData, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para agregar una nueva nota a la materia (método de la clase externa Nota)
    private void agregarNota(String materia, JTable table, DefaultTableModel model) {
        JTextField tituloField = new JTextField();
        JTextField notaField = new JTextField();
        JTextField porcentajeField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Título:"));
        panel.add(tituloField);
        panel.add(new JLabel("Nota:"));
        panel.add(notaField);
        panel.add(new JLabel("Porcentaje:"));
        panel.add(porcentajeField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Agregar Nota", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String titulo = tituloField.getText().trim();
            double nota, porcentaje;
            try {
                nota = Double.parseDouble(notaField.getText().trim());
                porcentaje = Double.parseDouble(porcentajeField.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Ingrese valores numéricos válidos para nota y porcentaje.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            MateriaData data = materiasData.get(materia);
            if (data != null) {
                data.addNota(titulo, nota, porcentaje);
                NotaData nuevaNota = data.getNotas().get(data.getNotas().size() - 1);
                model.addRow(nuevaNota.toTableRow());
                guardarDatos();
                // Actualizar la etiqueta de nota final si existe
                Container parent = table.getParent();
                while (parent != null && !(parent instanceof JPanel)) {
                    parent = parent.getParent();
                }
                if (parent instanceof JPanel) {
                    JPanel panelParent = (JPanel) parent;
                    for (Component comp : panelParent.getComponents()) {
                        if (comp instanceof JPanel) {
                            for (Component subComp : ((JPanel) comp).getComponents()) {
                                if (subComp instanceof JLabel) {
                                    JLabel label = (JLabel) subComp;
                                    if (label.getText().startsWith("Nota Final:")) {
                                        label.setText(String.format("Nota Final: %.2f", data.getNotaTotal()));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Método para mostrar el contenido de un tema específico de una materia
    private void mostrarContenidoTema(String materia, String tema) {
        MateriaData data = materiasData.get(materia);
        if (data != null) {
            currentMateria = materia;
            
            // Reinitialize the text pane
            contenidoPane = new JTextPane();
            contenidoPane.setEditable(true);
            contenidoPane.putClientProperty("caretWidth", 2);
            contenidoPane.setFont(new Font("Dialog", Font.PLAIN, 12));
            
            // Set content
            contenidoPane.setText(data.getContenidoTema(tema));
            
            // Add document listener for auto-save
            contenidoPane.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void changedUpdate(javax.swing.event.DocumentEvent e) { guardarCambios(); }
                public void insertUpdate(javax.swing.event.DocumentEvent e) { guardarCambios(); }
                public void removeUpdate(javax.swing.event.DocumentEvent e) { guardarCambios(); }
                
                private void guardarCambios() {
                    SwingUtilities.invokeLater(() -> {
                        data.setContenidoTema(tema, contenidoPane.getText(), "");
                        guardarDatos();
                    });
                }
            });

            // Update the apuntes panel with new text pane
            apuntesPanel.removeAll();
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(edicionToolbar, BorderLayout.CENTER);
            JPanel volverPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton volverBtn = new JButton("Volver");
            volverBtn.addActionListener(e -> {
                guardarDatos();
                ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "MATERIA_" + currentMateria);
            });
            volverPanel.add(volverBtn);
            topPanel.add(volverPanel, BorderLayout.EAST);
            
            apuntesPanel.add(topPanel, BorderLayout.NORTH);
            apuntesPanel.add(new JScrollPane(contenidoPane), BorderLayout.CENTER);
            apuntesPanel.revalidate();
            apuntesPanel.repaint();

            ((CardLayout)mainContentPanel.getLayout()).show(mainContentPanel, "APUNTES");
            contenidoPane.requestFocusInWindow();
        }
    }

    // Serializes AttributeSet to a Base64 string (simple implementation)
    private String serializeAttributes(AttributeSet attrs) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(attrs);
            oos.close();
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Deserializes and applies AttributeSet to the StyledDocument
    private void applyAttributes(byte[] data, StyledDocument doc) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bis);
            AttributeSet attrs = (AttributeSet) ois.readObject();
            ois.close();
            if (attrs != null) {
                doc.setCharacterAttributes(0, doc.getLength(), attrs, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Implement cargarDatos method
    private Map<String, MateriaData> cargarDatos() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(file)) {
            java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, MateriaData>>(){}.getType();
            Map<String, MateriaData> data = gson.fromJson(reader, type);
            if (data == null) {
                return new HashMap<>();
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

