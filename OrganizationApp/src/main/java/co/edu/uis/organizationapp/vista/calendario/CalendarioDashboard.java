package co.edu.uis.organizationapp.vista.calendario;

import co.edu.uis.organizationapp.modelo.calendario.Evento;
import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import net.miginfocom.swing.MigLayout;

public class CalendarioDashboard extends javax.swing.JFrame {
    
    private JList<Evento> eventList; 
    private DefaultListModel<Evento> eventListModel; // Modelo de datos para la lista
    private JTree subtaskList;
    private DefaultTreeModel subtaskListModel; // Modelo de datos para el árbol
    private DefaultMutableTreeNode subtaskRootNode; // Nodo raíz para el árbol

    public CalendarioDashboard() {
        // Llama a initComponents para configurar propiedades básicas del JFrame
        initComponents();

        // --- 1. Establecer el administrador de diseño a BorderLayout ---
        getContentPane().setLayout(new BorderLayout());

        // --- 2. Añadir componentes a las diferentes regiones del BorderLayout ---
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Botones de vista (Día/Semana/Mes/Año)
        JButton btnDia = new JButton("Día");
        JButton btnSemana = new JButton("Semana");
        JButton btnMes = new JButton("Mes");
        JButton btnAnio = new JButton("Año");
        
        toolBar.add(btnDia);
        toolBar.add(btnSemana);
        toolBar.add(btnMes);
        toolBar.add(btnAnio);
        
        toolBar.addSeparator(); // Separador visual
        
        // Flechas de navegación 
        JButton btnAnterior = new JButton("<"); 
        JButton btnSiguiente = new JButton(">");

        toolBar.add(btnAnterior);
        toolBar.add(btnSiguiente);

        toolBar.addSeparator();
        
        // Buscador
        JTextField buscadorField = new JTextField(15); // Campo de texto para el buscador, 15 columnas de ancho
        toolBar.add(buscadorField);
        
        getContentPane().add(toolBar, BorderLayout.NORTH); // Añade el panel al NORTE
        
        // Crea un nuevo JSplitPane con división horizontal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
       
        // --- Crear el JTabbedPane para la región IZQUIERDA del SplitPane ---

        JTabbedPane tabbedPane = new JTabbedPane(); // Crea el JTabbedPane

        // Al terminar las vistas esto debe ser reemplazado
        
        JPanel monthViewPanel = new JPanel();
        monthViewPanel.add(new JLabel("Vista Mensual del Calendario"));

        JPanel weekViewPanel = new JPanel();
        weekViewPanel.add(new JLabel("Vista Semanal del Calendario"));

        JPanel dayViewPanel = new JPanel();
        dayViewPanel.add(new JLabel("Vista Diaria del Calendario"));

        JPanel yearViewPanel = new JPanel();
        yearViewPanel.add(new JLabel("Vista Anual del Calendario"));

        // --- Añadir las pestañas al JTabbedPane ---
        tabbedPane.addTab("Mes", monthViewPanel);
        tabbedPane.addTab("Semana", weekViewPanel);
        tabbedPane.addTab("Día", dayViewPanel);
        tabbedPane.addTab("Año", yearViewPanel);
        
        // --- Establecer el JTabbedPane como el componente IZQUIERDO del JSplitPane ---
        splitPane.setLeftComponent(tabbedPane);
        
        JPanel panelDerechoSplit = new JPanel();
        
        panelDerechoSplit.setLayout(new MigLayout("", "[grow, fill]", "[grow, fill]10[]10[grow, fill]"));
        
        // 1. JList<Evento> del día/semana/mes seleccionado
        eventListModel = new DefaultListModel<Evento>(); // Crea el modelo de datos para la lista
        // TEMPORAL, solo es ejemplo
        eventList = new JList<Evento>(eventListModel); // Crea el JList con el modelo
        // Envuelve la lista en un JScrollPane para que tenga barras de desplazamiento
        JScrollPane eventListScrollPane = new JScrollPane(eventList);
        
        // 2. Botones “Añadir”, “Editar”, “Eliminar”
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Panel simple para agrupar botones
        JButton btnAnadir = new JButton("Añadir");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        buttonPanel.add(btnAnadir);
        buttonPanel.add(btnEditar);
        buttonPanel.add(btnEliminar);
        
        // 3. JTree de subtareas (Al seleccionar un evento de la lista)
        subtaskRootNode = new DefaultMutableTreeNode("Subtareas"); // Crea el nodo raíz del árbol
        subtaskListModel = new DefaultTreeModel(subtaskRootNode); // Crea el modelo del árbol
        // Añade datos de ejemplo (en la vida real, esto sería dinámico según la selección del evento)
        DefaultMutableTreeNode subtask1 = new DefaultMutableTreeNode("Investigar tema X");
        subtaskRootNode.add(subtask1);
        subtask1.add(new DefaultMutableTreeNode("Buscar en Google"));
        subtask1.add(new DefaultMutableTreeNode("Leer artículos"));
        DefaultMutableTreeNode subtask2 = new DefaultMutableTreeNode("Preparar presentación");
        subtaskRootNode.add(subtask2);
        subtaskList = new JTree(subtaskListModel); // Crea el JTree con el modelo
        // Envuelve el árbol en un JScrollPane para barras de desplazamiento
        JScrollPane subtaskListScrollPane = new JScrollPane(subtaskList);
        
        // Añade el JList (dentro de su scroll pane) en la primera fila
        panelDerechoSplit.add(eventListScrollPane, "cell 0 0, grow, wrap");

        // Añade el panel de botones en la segunda fila
        panelDerechoSplit.add(buttonPanel, "cell 0 1, align center, wrap");

        // Añade el JTree (dentro de su scroll pane) en la tercera fila
        panelDerechoSplit.add(subtaskListScrollPane, "cell 0 2, grow, push"); // push: asegura que tome el espacio restante


        // Establecer el rightPanel configurado como el componente DERECHO del SplitPane
        splitPane.setRightComponent(panelDerechoSplit);
        
        splitPane.setRightComponent(panelDerechoSplit);
        
        
        getContentPane().add(splitPane, BorderLayout.CENTER);
        
        // Panel en la región SUR (inferior)
        JPanel panelSur = new JPanel();
        // panelSur.add(new javax.swing.JButton("Guardar"));
        getContentPane().add(panelSur, BorderLayout.SOUTH); // Añade el panel al SUR

        // Panel en la región ESTE (derecha)
        JPanel panelEste = new JPanel();
        // panelEste.add(new javax.swing.JList());
        getContentPane().add(panelEste, BorderLayout.EAST); // Añade el panel al ESTE

        // Panel en la región OESTE (izquierda)
        JPanel panelOeste = new JPanel();
        // panelOeste.add(new javax.swing.JTree());
        getContentPane().add(panelOeste, BorderLayout.WEST); // Añade el panel al OESTE

        // --- 3. Ajustar el tamaño de la ventana ---
        // pack() ajusta el tamaño de la ventana para acomodar el tamaño preferido de sus componentes.
        // Debe llamarse después de añadir todos los componentes.
        pack();

        // Opcional: Establecer un tamaño específico si pack() no es suficiente
        // setSize(600, 400);
        // Opcional: Centrar la ventana en la pantalla
        setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do not modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        // Aquí solo mantenemos las propiedades básicas del JFrame que NO son parte del layout GroupLayout
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        // --- REMOVER O COMENTAR TODO EL CÓDIGO GENERADO POR EL EDITOR PARA GROUPLAYOUT ---
        // Estas líneas definen el layout usando GroupLayout y son incompatibles con BorderLayout.
        /*
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
         */
        // -----------------------------------------------------------------------------
        // pack() se llama al final del constructor principal, después de añadir todos los componentes.
        // Aquí en initComponents() solo mantenemos la configuración inicial del JFrame.
    }// </editor-fold>

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) throws UnsupportedLookAndFeelException {
        
        UIManager.setLookAndFeel(new com.formdev.flatlaf.FlatLightLaf());

        // Asegúrate de que la creación y visualización del JFrame se hagan en el EDT (Hilo de Despacho de Eventos)
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CalendarioDashboard().setVisible(true);
            }
        });
    }
}
