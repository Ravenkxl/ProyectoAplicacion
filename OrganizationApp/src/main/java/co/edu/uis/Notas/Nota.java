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
import javax.swing.text.Element;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonToken;
import co.edu.uis.Notas.DataManager;
import javax.swing.text.AttributeSet;
import java.util.Base64;
import javax.swing.text.html.HTMLEditorKit;
import java.io.StringWriter;


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
    private String currentTema;
    private String currentMateria; // Add this field to track the current materia
    
    private final Gson gson;
    private String getDocumentHTML() {
         try {
             HTMLEditorKit kit = new HTMLEditorKit();
             StringWriter writer = new StringWriter();
             // Escribe todo el documento (desde offset 0 hasta length) en writer:
             kit.write(writer, contenidoPane.getDocument(), 0, contenidoPane.getDocument().getLength());
             return writer.toString();
         } catch (Exception ex) {
             ex.printStackTrace();
             return "";
         }
     }
    private static final String DATA_FILE = "notas_data.json";
    private Map<String, JLabel> notaFinalLabels = new HashMap<>();

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
                    int rgb = 0;
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if ("rgb".equals(name)) {
                            rgb = reader.nextInt();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                    return new Color(rgb, true);
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

    // Implementación de cargarDatos para inicializar materiasData desde el archivo JSON
    private Map<String, MateriaData> cargarDatos() {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return new HashMap<>();
        }
        try (Reader reader = new FileReader(file)) {
            java.lang.reflect.Type type = new TypeToken<Map<String, MateriaData>>(){}.getType();
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

        // Detectar si es un panel de tema o materia
        boolean esMateria = !nombre.equals("+ Nueva Materia") && !nombre.equals("+ Nuevo Tema") && !materiasData.values().stream().anyMatch(m -> m.getTemas().contains(nombre));
        boolean esTema = materiasData.values().stream().anyMatch(m -> m.getTemas().contains(nombre));

        if (esMateria) {
            // Botón X para eliminar materia
            JButton deleteBtn = new JButton("X");
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
            deleteBtn.setMargin(new Insets(1, 4, 1, 4));
            deleteBtn.addActionListener(e -> {
                Window parentWindow = SwingUtilities.getWindowAncestor(mainContentPanel);
                JDialog confirmDialog;
                if (parentWindow instanceof Frame) {
                    confirmDialog = new JDialog((Frame) parentWindow, "Confirmar eliminación", true);
                } else if (parentWindow instanceof Dialog) {
                    confirmDialog = new JDialog((Dialog) parentWindow, "Confirmar eliminación", true);
                } else {
                    confirmDialog = new JDialog((Frame) null, "Confirmar eliminación", true);
                }
                confirmDialog.setLayout(new BorderLayout(10, 10));
                confirmDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                confirmDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JPanel dialogPanel = new JPanel(new BorderLayout(10, 10));
                dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                dialogPanel.setBackground(Color.WHITE);

                JLabel msgLabel = new JLabel("¿Está seguro de eliminar esta materia?");
                dialogPanel.add(msgLabel, BorderLayout.CENTER);

                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
                buttonPanel.setBackground(Color.WHITE);

                JButton yesButton = new JButton("Sí");
                JButton noButton = new JButton("No");

                yesButton.addActionListener(ev -> {
                    materiasData.remove(nombre);
                    guardarDatos();
                    carpetasPanel.removeAll();
                    for (Map.Entry<String, MateriaData> entry : materiasData.entrySet()) {
                        JPanel materiaPanel = createFolderPanel(entry.getKey(), entry.getValue().color);
                        final String materiaName = entry.getKey();
                        materiaPanel.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent evt) {
                                mostrarContenidoMateria(materiaName);
                            }
                        });
                        carpetasPanel.add(materiaPanel);
                    }
                    JPanel addMateriaPanel = createFolderPanel("+ Nueva Materia", null);
                    addMateriaPanel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent evt) {
                            crearNuevaMateria();
                        }
                    });
                    carpetasPanel.add(addMateriaPanel);
                    carpetasPanel.revalidate();
                    carpetasPanel.repaint();
                    confirmDialog.dispose();
                });

                noButton.addActionListener(ev -> confirmDialog.dispose());
                buttonPanel.add(yesButton);
                buttonPanel.add(noButton);
                dialogPanel.add(buttonPanel, BorderLayout.SOUTH);
                confirmDialog.add(dialogPanel);
                confirmDialog.pack();
                confirmDialog.setLocationRelativeTo(mainContentPanel);
                confirmDialog.setAlwaysOnTop(true);
                confirmDialog.setVisible(true);
            });
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            topPanel.setOpaque(false);
            topPanel.add(deleteBtn);
            panel.add(topPanel, BorderLayout.NORTH);
        } else if (esTema) {
            // Botón X para eliminar tema
            JButton deleteBtn = new JButton("X");
            deleteBtn.setFont(new Font("Arial", Font.BOLD, 10));
            deleteBtn.setMargin(new Insets(1, 4, 1, 4));
            deleteBtn.addActionListener(e -> {
                // Buscar la materia a la que pertenece este tema
                String materiaPadre = null;
                for (Map.Entry<String, MateriaData> entry : materiasData.entrySet()) {
                    if (entry.getValue().getTemas().contains(nombre)) {
                        materiaPadre = entry.getKey();
                        break;
                    }
                }
                if (materiaPadre != null) {
                    int confirm = JOptionPane.showConfirmDialog(panel, "¿Está seguro de eliminar el tema '" + nombre + "'?", "Confirmar eliminación", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        MateriaData data = materiasData.get(materiaPadre);
                        if (data != null) {
                            data.temas.remove(nombre);
                            guardarDatos();
                            mostrarContenidoMateria(materiaPadre);
                        }
                    }
                }
            });
            JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            topPanel.setOpaque(false);
            topPanel.add(deleteBtn);
            panel.add(topPanel, BorderLayout.NORTH);
        }

        return panel;
    }

    private void crearNuevaMateria() {
        // Create modal dialog with proper parent window reference
        Window owner = SwingUtilities.getWindowAncestor(mainContentPanel);
        if (owner == null) owner = this;
        
        JDialog dialog;
        if (owner instanceof Frame) {
            dialog = new JDialog((Frame) owner, "Nueva Materia", true);
        } else if (owner instanceof Dialog) {
            dialog = new JDialog((Dialog) owner, "Nueva Materia", true);
        } else {
            dialog = new JDialog((Frame) null, "Nueva Materia", true);
        }
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBackground(Color.WHITE);
        
        JTextField nombreField = new JTextField(15);
        JButton colorButton = new JButton("Seleccionar Color");
        selectedColor = Color.WHITE;
        colorButton.setBackground(selectedColor);
        
        colorButton.addActionListener(e -> {
            // Usar el propio dialog como padre para el selector de color
            JColorChooser colorChooser = new JColorChooser(selectedColor);
            JDialog colorDialog = JColorChooser.createDialog(
                dialog, // <-- aquí el cambio: usar 'dialog' como padre
                "Seleccionar Color",
                true,
                colorChooser,
                e2 -> {
                    selectedColor = colorChooser.getColor();
                    if (selectedColor != null) {
                        colorButton.setBackground(selectedColor);
                    }
                },
                null
            );
            colorDialog.setLocationRelativeTo(dialog);
            colorDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
            colorDialog.setVisible(true);
        });
        
        inputPanel.add(new JLabel("Nombre:"));
        inputPanel.add(nombreField);
        inputPanel.add(new JLabel("Color:"));
        inputPanel.add(colorButton);
        mainPanel.add(inputPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton okButton = new JButton("Aceptar");
        JButton cancelButton = new JButton("Cancelar");
        
        okButton.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (nombre.isEmpty()) return;
            
            if (materiasData.containsKey(nombre)) {
                JOptionPane.showMessageDialog(dialog,
                    "Ya existe una materia con ese nombre",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update UI in the EventDispatch thread
            SwingUtilities.invokeLater(() -> {
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
                guardarDatos();
            });
            
            dialog.dispose();
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.pack();
        dialog.setLocationRelativeTo(owner);
        dialog.toFront();
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
        dialog.setAlwaysOnTop(false);
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
                String contenidoTexto = contenidoPane.getText();
                String atributosHTML = getDocumentHTML(); 
                   materiasData
                    .get(currentMateria)
                        .setContenidoTema(currentTema, contenidoTexto, atributosHTML); 
                
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
                String contenidoTexto = contenidoPane.getText();
                String atributosHTML = getDocumentHTML();
                materiasData
                    .get(currentMateria)
                    .setContenidoTema(currentTema, contenidoTexto, atributosHTML);
            }
        });

        colorBtn.addActionListener(e -> {
            JColorChooser colorChooser = new JColorChooser(Color.BLACK);
            final JDialog dialog = JColorChooser.createDialog(
                SwingUtilities.getWindowAncestor(this),  // Parent to main frame
                "Seleccionar Color",
                true,  // Make it modal
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
                            String contenidoTexto = contenidoPane.getText();
                            String atributosHTML = getDocumentHTML();
                            materiasData
                                .get(currentMateria)
                                .setContenidoTema(currentTema, contenidoTexto, atributosHTML);
                        }
                    }
                },
                null
            );
            dialog.setLocationRelativeTo(this);
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
                String contenidoTexto = contenidoPane.getText();
                String atributosHTML = getDocumentHTML();
                materiasData
                    .get(currentMateria)
                    .setContenidoTema(currentTema, contenidoTexto, atributosHTML);               
            }
        });
        
        edicionToolbar.add(boldBtn);
        edicionToolbar.add(italicBtn);
        edicionToolbar.add(colorBtn);
        edicionToolbar.add(new JLabel("Tamaño: "));
        edicionToolbar.add(sizeCombo);
        
        // Add file link button to toolbar
        JButton linkBtn = new JButton("Agregar Archivo");
        linkBtn.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                insertFileLink(file.getAbsolutePath(), file.getName());
            }
        });
        edicionToolbar.add(linkBtn);
        
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
    
    private void insertFileLink(String path, String displayName) {
        try {
            StyledDocument doc = contenidoPane.getStyledDocument();
            javax.swing.text.Style style = contenidoPane.addStyle("FileLink", null);
            StyleConstants.setForeground(style, Color.BLUE);
            StyleConstants.setUnderline(style, true);
            style.addAttribute("filePath", path);
            doc.insertString(contenidoPane.getCaretPosition(), displayName + "\n", style);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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
                        // Refrescar la vista de la materia después de eliminar la nota
                        SwingUtilities.invokeLater(() -> mostrarContenidoMateria(materia));
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
        notaFinalLabels.put(materia, notaFinalLabel);
        
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
        JDialog dialog = new JDialog(this, "Nuevo Tema", true);
        JTextField nombreField = new JTextField(20);
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        panel.add(new JLabel("Nombre del nuevo tema:"), BorderLayout.NORTH);
        panel.add(nombreField, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("Aceptar");
        JButton cancelButton = new JButton("Cancelar");
        
        okButton.addActionListener(e -> {
            String nombre = nombreField.getText().trim();
            if (!nombre.isEmpty()) {
                MateriaData data = materiasData.get(materia);
                if (data != null) {
                    if (data.getTemas().stream().anyMatch(t -> t.equalsIgnoreCase(nombre))) {
                        JOptionPane.showMessageDialog(dialog, 
                            "Ya existe un tema con ese nombre.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    data.addTema(nombre);
                    guardarDatos();
                    dialog.dispose();
                    // Refrescar la vista de la materia después de cerrar el diálogo
                    SwingUtilities.invokeLater(() -> mostrarContenidoMateria(materia));
                }
            }
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Implement guardarDatos method
    private void guardarDatos() {
        try (Writer writer = new FileWriter(DATA_FILE)) {
            if (currentMateria != null && currentTema != null && materiasData.get(currentMateria) != null) {
                String contenidoTexto = contenidoPane.getText();
                String atributosHTML = getDocumentHTML();
                materiasData.get(currentMateria).setContenidoTema(currentTema, contenidoTexto, atributosHTML);
            }
            gson.toJson(materiasData, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método para agregar una nueva nota a la materia (método de la clase externa Nota)
    private void agregarNota(String materia, JTable table, DefaultTableModel model) {
        // Create modal dialog
        JDialog dialog = new JDialog(this, "Agregar Nota", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(Color.WHITE);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        inputPanel.setBackground(Color.WHITE);
        
        JTextField tituloField = new JTextField(15);
        JTextField notaField = new JTextField(15);
        JTextField porcentajeField = new JTextField(15);

        inputPanel.add(new JLabel("Título:"));
        inputPanel.add(tituloField);
        inputPanel.add(new JLabel("Nota:"));
        inputPanel.add(notaField);
        inputPanel.add(new JLabel("Porcentaje:"));
        inputPanel.add(porcentajeField);
        
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton okButton = new JButton("Aceptar");
        JButton cancelButton = new JButton("Cancelar");

        okButton.addActionListener(e -> {
            String titulo = tituloField.getText().trim();
            try {
                double nota = Double.parseDouble(notaField.getText().trim());
                double porcentaje = Double.parseDouble(porcentajeField.getText().trim());

                MateriaData data = materiasData.get(materia);
                if (data != null) {
                    data.addNota(titulo, nota, porcentaje);
                    guardarDatos();
                    dialog.dispose();
                    // Refrescar la vista de la materia después de cerrar el diálogo
                    SwingUtilities.invokeLater(() -> mostrarContenidoMateria(materia));
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Ingrese valores numéricos válidos para nota y porcentaje.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.getRootPane().setDefaultButton(okButton);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

    // Método para mostrar el contenido de un tema específico de una materia
    private void mostrarContenidoTema(String materia, String tema) {
        // Set current materia for navigation
        this.currentMateria = materia;

        // Obtener el contenido del tema
        MateriaData data = materiasData.get(materia);
        String contenido = "";
        if (data != null) {
            contenido = data.getContenidoTema(tema);
        }

        // Mostrar el contenido en el JTextPane
        contenidoPane.setText(contenido != null ? contenido : "");

        // Cambiar a la vista de apuntes
        ((CardLayout) mainContentPanel.getLayout()).show(mainContentPanel, "APUNTES");
    }

    // Helper method to update nota final label
    private void updateNotaFinalLabel(JTable table, MateriaData data) {
        if (data == null) return;
        JLabel label = notaFinalLabels.get(currentMateria);
        if (label != null) {
            label.setText(String.format("Nota Final: %.2f", data.getNotaTotal()));
        }
    }
}
                         
