package presentacio;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import transversal.Pair;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.Collator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Es la vista principal del programa, la que apareix quan s’inicia l’aplicacio.
 * Mostra la llista dels documents (titols+autors) i et deixa obrir-los, filtrar-los, i altres opcions sobre ells. Des d’aquesta s’obren les altres vistes mitjançant crides al CtrlPresentacio
 *
 * @author Christian Rivero
 */
public class ViewPrincipal extends JFrame {
    /**
     * Panell principal de la vista
     */
    private JPanel panel1;
    /**
     * Boto per crear un document i afegir-ho al sistema
     */
    private JButton creaButton;
    /**
     * Boto per importar un document de tipus txt o xml i afegir-ho al sistema
     */
    private JButton importaButton;
    /**
     * Boto per mostrar una serie de popups amb indicacions de les funcions de cada boto i accio possible a la vista
     */
    private JButton ajudaButton;
    /**
     * Panell on s’ubica la JTable on es llisten els documents que hi ha al sistema (autor+titol)
     */
    private JPanel tablePanel;
    /**
     * Boto que obre la vista de ViewGestExpBool
     */
    private JButton gestioExpBoolButton;
    /**
     * Boto que mostra un popup amb els diferents tipus de cerca sobre documents que hi ha
     */
    private JButton busquedaButton;
    /**
     * Boto per esborrar un conjunt de documents seleccionats
     */
    private JButton esborrarDocsButton;
    /**
     * Label on indiquem el nombre de documents del sistema en tot moment
     */
    private JLabel contadorDocs;
    /**
     * Int que indica quina ha sigut l’anterior columna del header del JTable dels documents pulsada.
     * Pot ser 0 o 1 (columnes que permeten l’ordenacio) i si es prem dues vegades la mateixa columna seguida, es posa a -1
     */
    private int columnRepetida;
    /**
     * DefaultTableModel necessaria per crear la JTable dels documents.
     */
    private DefaultTableModel tableModel;
    /**
     * JTable que mostra els documents a la vista (titols + autors).
     */
    private JTable documents;
    /**
     * Objecte this, necessari per poder passa-ho com a parametre als actionListeners.
     */
    private ViewPrincipal viewPrin = this;


    /**
     * Creadora unica
     *
     * @param cp Instancia del controlador de presentacio
     */
    public ViewPrincipal(CtrlPresentacio cp) {
        columnRepetida = -1;
        setContentPane(panel1);
        setTitle("Documenteitor");
        setSize(1000, 500);
        setMinimumSize(new Dimension(500, 300));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        String[] colums = {"Títols", "Autors", "Darrera modificació", "Opcions"};
        Object[][] documentsObj = new Object[0][3];
        /*for(int i = 0; i < documentsList.size(); ++i) {
            Object[] document = {documentsList.get(i).y, documentsList.get(i).x};
            documentsObj[i] = document;
        }*/

        tableModel = new DefaultTableModel(documentsObj, colums) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        documents = new JTable(tableModel);

        //documents.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(documents.getModel());
        documents.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        /*sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        columnRepetida = 1;*/
        sorter.setSortKeys(sortKeys);
        sorter.setSortable(3, false);

        JTableHeader header = documents.getTableHeader();
        header.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point point = e.getPoint();
                int column = documents.columnAtPoint(point);
                if (column != 3) {
                    sortKeys.clear();
                    if (columnRepetida != 0 && column == 0) { //titulo, autor
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        columnRepetida = column;
                    } else if (columnRepetida != 1 && column == 1) { //autor, titulo
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        columnRepetida = column;
                    } else if (columnRepetida != 2 && column == 2) { //data, autor, titulo
                        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        columnRepetida = column;
                    } else if (columnRepetida == 0 && column == 0) { //titulo, autor
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                        columnRepetida = -1;
                    } else if (columnRepetida == 1 && column == 1) { //autor, titulo
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
                        columnRepetida = -1;
                    } else if (columnRepetida == 2 && column == 2) { //data, autor, titulo
                        sortKeys.add(new RowSorter.SortKey(2, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
                        columnRepetida = -1;
                    }
                    sorter.setSortable(3, false);
                    sorter.setSortKeys(sortKeys);
                }
            }
        });

        tablePanel.setLayout(new BorderLayout());
        JScrollPane tableScroll = new JScrollPane(documents);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        documents.getColumnModel().getColumn(0).setCellRenderer(new GestioCell("text"));
        documents.getColumnModel().getColumn(1).setCellRenderer(new GestioCell("text"));
        documents.getColumnModel().getColumn(2).setCellRenderer(new GestioCell("int"));
        documents.getColumnModel().getColumn(3).setCellRenderer(new GestioCell("icon"));
        documents.setRowHeight(25);
        documents.getColumnModel().getColumn(0).setPreferredWidth(100);
        documents.getColumnModel().getColumn(1).setPreferredWidth(100);
        documents.getColumnModel().getColumn(2).setPreferredWidth(200);
        documents.getColumnModel().getColumn(3).setPreferredWidth(10);
        documents.getTableHeader().setReorderingAllowed(false);

        /* popup busqueda */
        JMenuItem llistarTdeA = new JMenuItem("Llistar títols autor");
        JMenuItem llistarAperP = new JMenuItem("Llistar autors prefix");
        JMenuItem cercaR = new JMenuItem("Cerca per rellevància");
        JMenuItem cercaExp = new JMenuItem("Cerca per expressió booleana");
        JPopupMenu popBusqueda = new JPopupMenu();
        popBusqueda.add(llistarTdeA);
        popBusqueda.add(llistarAperP);
        popBusqueda.add(cercaR);
        popBusqueda.add(cercaExp);

        /* popup opciones docs */
        JMenuItem exportarD = new JMenuItem("Exportar document");
        JMenuItem llistarSemblants = new JMenuItem("Llistar documents semblants");
        JMenuItem modT = new JMenuItem("Modificar títol");
        JMenuItem modA = new JMenuItem("Modificar autor");
        JMenuItem mostrarD = new JMenuItem("Mostrar document");
        JMenuItem borrarD = new JMenuItem("Esborrar document");
        JPopupMenu popOptDoc = new JPopupMenu();
        popOptDoc.add(exportarD);
        popOptDoc.add(llistarSemblants);
        popOptDoc.add(modT);
        popOptDoc.add(modA);
        popOptDoc.add(mostrarD);
        popOptDoc.add(borrarD);

        /* popup boton derecho sobre seleccion -> esborrar */
        JMenuItem borrarDocsSeleccionats = new JMenuItem("Esborrar documents seleccionats");
        JPopupMenu popBorrarDocs = new JPopupMenu();
        popBorrarDocs.add(borrarDocsSeleccionats);

        /*Desplegable cerca*/
        busquedaButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                popBusqueda.show(e.getComponent(), busquedaButton.getX() + busquedaButton.getWidth() - 7, e.getY() - 30);
            }
        });

        /*eventos sobre la tabla*/
        documents.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (documents.getRowCount() > 0) {
                    if (isRightMouseButton(e)) {
                        int row = documents.rowAtPoint(e.getPoint());
                        boolean botoDretSobreSeleccionat = false;
                        int[] selection = documents.getSelectedRows();
                        for (int i = 0; i < selection.length && !botoDretSobreSeleccionat; ++i) {
                            if (selection[i] == row) botoDretSobreSeleccionat = true;
                        }
                        if (botoDretSobreSeleccionat) { //si está seleccionada previamente -> borrarDocs
                            popBorrarDocs.show(e.getComponent(), e.getX(), e.getY());
                        } else { //abrir popups opciones en un doc
                            documents.clearSelection();
                            documents.addRowSelectionInterval(row, row);
                            popOptDoc.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } else {
                        int columna = documents.columnAtPoint(e.getPoint());
                        if (e.getClickCount() == 2 && columna != 3) {
                            String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
                            String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
                            cp.obrirDocument(autor, titol);
                            cp.ocultaViewPrincipal();
                        } else if (columna == 3) {
                            popOptDoc.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                } else if (documents.getRowCount() == 0 && e.getClickCount() == 2) {
                    JOptionPane.showMessageDialog(null, "No hi ha cap document encara, crea o importa un document.", "Error cap document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /* borrardocs seleccionados */
        ActionListener esborrarDocsSeleccionatsAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                /*Creamos la tabla*/
                if (documents.getSelectedRowCount() > 0) {
                    Object[][] documentsBorrar = new Object[documents.getSelectedRowCount()][2];
                    Object[] columnsBorrar = {"Títols", "Autors"};

                    int index = 0;
                    int selectedRow[] = documents.getSelectedRows();
                    for (int i : selectedRow) {
                        Object[] docBorrar = {documents.getValueAt(i, 0), documents.getValueAt(i, 1)};
                        documentsBorrar[index] = docBorrar;
                        ++index;
                        //System.out.println(selectedRow[i]);
                    }

                    DefaultTableModel tm = new DefaultTableModel(documentsBorrar, columnsBorrar) {
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            return false;
                        }
                    };
                    JPanel panelBorrar = new ShowingDocsTable(tm, documents, cp, false, viewPrin);
                    panelBorrar.add(new JLabel("S'esborraran els següents documents: "), BorderLayout.NORTH);
                    panelBorrar.add(new JLabel("Estàs d'acord?"), BorderLayout.SOUTH);

                    /*Mostramos tabla*/
                    int opt = JOptionPane.showOptionDialog(null, panelBorrar, "Esborrar documents seleccionats",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.NO_OPTION);
                    if (opt == 0) { //s'esborren + missatge
                        int contDocsElimOk = 0;
                        while (selectedRow.length != 0) {
                            String titol = (String) documents.getValueAt(selectedRow[0], 0);
                            String autor = (String) documents.getValueAt(selectedRow[0], 1);
                            if (cp.esborrarDocument(autor, titol)) {
                                tableModel.removeRow(documents.convertRowIndexToModel(selectedRow[0]));
                                ++contDocsElimOk;
                            }
                            selectedRow = documents.getSelectedRows();
                        }
                        contadorDocs.setText(Integer.toString(documents.getRowCount()));
                        String nombreDocumentsDepen = " documents."; //si es 1 documento borrado quiero que diga document y no documents
                        if (contDocsElimOk == 1) nombreDocumentsDepen = " document.";
                        JOptionPane.showMessageDialog(null, "S'han esborrat correctament " + contDocsElimOk + nombreDocumentsDepen,
                                "Esborrar documents seleccionats", JOptionPane.DEFAULT_OPTION);
                    } else { //misstage no s'han borrat
                        JOptionPane.showMessageDialog(null, "No s'ha esborrat cap document.",
                                "Esborrar documents seleccionats", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap document seleccionat.",
                            "Esborrar documents seleccionats", JOptionPane.DEFAULT_OPTION);
                }
            }
        };

        /* Se puede borrar desde el boton o con boton derecho sobre selección */
        borrarDocsSeleccionats.addActionListener(esborrarDocsSeleccionatsAction);
        esborrarDocsButton.addActionListener(esborrarDocsSeleccionatsAction);

        /*modificar titulo*/
        modT.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
                String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
                JPanel insertTitol = new JPanel();
                JTextField newT = new JTextField(titol, 20);
                insertTitol.add(new JLabel("Escriu el nou títol: "));
                insertTitol.add(newT);
                String[] opts = {"Sí", "Cancel·la"};
                int opt = JOptionPane.showOptionDialog(null, insertTitol, "Modificar títol",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                if (opt == 0 && !newT.getText().equals("") && !newT.getText().equals(titol) && newT.getText().length() <= 50 && !newT.getText().contains("_")) {
                    int opt2 = JOptionPane.showConfirmDialog(null, "El document tindrà el títol: " + newT.getText() +
                            " i l'autor: " + autor + ", estàs d'acord?", "Modificar títol", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
                    if (opt2 == 0) {
                        boolean modificat = cp.modificarTitol(autor, titol, newT.getText()); //autor, titol, newT
                        if (modificat) {
                            String data = (String) documents.getValueAt(documents.getSelectedRow(), 2);
                            tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
                            tableModel.addRow(new Object[]{newT.getText(), autor, data});
                        }
                    }
                } else if(newT.getText().length() > 50 || newT.getText().contains("_")) {
                    JOptionPane.showMessageDialog(null, "El títol no pot contenir \"_\" ni ser més llarg de 50 caràcters.");
                } else if (opt == 0 && newT.getText().equals(titol)) {
                    JOptionPane.showMessageDialog(null, "Ja és el títol del document.");
                } else if (opt == 0) { //es titol buit
                    JOptionPane.showMessageDialog(null, "Introdueix un títol vàlid, no es permeten deixar camps buits.");
                }
            }
        });

        /*modificar autor*/
        modA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
                String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);

                JPanel insertAutor = new JPanel();
                JTextField newA = new JTextField(autor, 20);
                insertAutor.add(new JLabel("Escriu el nou autor: "));
                insertAutor.add(newA);
                String[] opts = {"Sí", "Cancel·la"};
                int opt = JOptionPane.showOptionDialog(null, insertAutor, "Modificar autor",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                if (opt == 0 && !newA.getText().equals("") && !newA.getText().equals(autor) && newA.getText().length() <= 50 && !newA.getText().contains("_")) {
                    int opt2 = JOptionPane.showConfirmDialog(null, "El document tindrà el títol: " + titol +
                            " i l'autor: " + newA.getText() + ", estàs d'acord?", "Modificar autor", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
                    if (opt2 == 0) {
                        boolean modificat = cp.modificarAutor(autor, titol, newA.getText()); //autor, titol, newA
                        if (modificat) {
                            String data = (String) documents.getValueAt(documents.getSelectedRow(), 2);
                            tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
                            tableModel.addRow(new Object[]{titol, newA.getText(), data});
                        }
                    }
                } else if(newA.getText().length() > 50 || newA.getText().contains("_")) {
                    JOptionPane.showMessageDialog(null, "L'autor no pot contenir \"_\" ni ser més llarg de 50 caràcters.");
                } else if (opt == 0 && newA.getText().equals(autor)) {
                    JOptionPane.showMessageDialog(null, "Ja és l'autor del document.",
                            "Modificar títol", JOptionPane.DEFAULT_OPTION);
                } else if (opt == 0) { //és autor buit
                    JOptionPane.showMessageDialog(null, "Introdueix un autor vàlid, no es permeten deixar camps buits.",
                            "Modificar títol", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*esborrar únic doc*/
        borrarD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
                String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
                int opt = JOptionPane.showConfirmDialog(null, "Segur que vols esborrar el document amb títol: " +
                        titol + " i autor: " + autor + " permanentment?", "Esborrar document", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
                if (opt == 0) {
                    if (cp.esborrarDocument(autor, titol)) {
                        tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
                        contadorDocs.setText(Integer.toString(documents.getRowCount()));
                        JOptionPane.showMessageDialog(null, "S'ha esborrat el document correctament",
                                "Esborrar document", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No s'ha esborrat el document",
                            "Esborrar document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*crear doc*/
        creaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel panelCreacio = new JPanel();
                JTextField newT = new JTextField("", 20);
                JPanel insertTitol = new JPanel();
                insertTitol.add(new JLabel("Escriu el titol: "));
                insertTitol.add(newT);

                JTextField newA = new JTextField("", 20);
                JPanel insertAutor = new JPanel();
                insertAutor.add(new JLabel("Escriu l'autor: "));
                insertAutor.add(newA);

                panelCreacio.setLayout(new BorderLayout());
                panelCreacio.add(insertTitol, BorderLayout.NORTH);
                panelCreacio.add(insertAutor, BorderLayout.SOUTH);

                String[] opts = {"Sí", "Cancel·la"};
                int opt = JOptionPane.showOptionDialog(null, panelCreacio, "Creació document",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                if (opt == 0 && !newT.getText().equals("") && !newA.getText().equals("") && newT.getText().length() <= 50 && !newT.getText().contains("_") && newA.getText().length() <= 50 && !newA.getText().contains("_")) {
                    String data = LocalDate.now() + " " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                    boolean creat = cp.crearDocument(newA.getText(), newT.getText(), data);
                    if (creat) {
                        tableModel.addRow(new Object[]{newT.getText(), newA.getText(), data});
                        contadorDocs.setText(Integer.toString(documents.getRowCount()));
                    }
                } else if(newT.getText().length() > 50 || newT.getText().contains("_") || newA.getText().length() > 50 || newA.getText().contains("_")) {
                    JOptionPane.showMessageDialog(null, "Ni títol ni l'autor poden contenir \"_\" ni ser més llargs de 50 caràcters.",
                            "Error crear", JOptionPane.ERROR_MESSAGE);
                } else if (opt == 0 && (newT.getText().equals("") || newA.getText().equals(""))) {
                    JOptionPane.showMessageDialog(null, "Indica un títol i autor vàlids, no deixis camps buits.",
                            "Error crear", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        /*importar docs, maximo 10, tipo txt o xml*/
        importaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setFileFilter(new FileNameExtensionFilter("Compatibles amb l'aplicació, màxim 10", "txt", "xml"));
                chooser.setDialogTitle("Selecciona fitxers, màxim 10");
                chooser.setMultiSelectionEnabled(true);
                int result = chooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File[] arxius = chooser.getSelectedFiles();
                    if (arxius.length > 10) {
                        JOptionPane.showMessageDialog(null,
                                "Has seleccionat més arxius del màxim, s'importaran només els 10 primers en ordre alfabètic.");
                    }
                    int min = 10;
                    if (arxius.length < 10) min = arxius.length;
                    //List<String> paths = new ArrayList<>();
                    int contDocsImp = 0; //comptador de documents importats correctaments
                    for (int i = 0; i < min; ++i) {
                        String date = LocalDate.now() + " " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                        Pair docImp = cp.importaDocument(arxius[i].getAbsolutePath(), date);
                        if (docImp != null) {
                            ++contDocsImp;
                            tableModel.addRow(new Object[]{docImp.y, docImp.x, date});
                        }
                    }
                    contadorDocs.setText(Integer.toString(documents.getRowCount()));
                    contadorDocs.setText(Integer.toString(documents.getRowCount()));
                    String nombreDocumentsDepen = " documents."; //si es 1 documento borrado quiero que diga document y no documents
                    if (contDocsImp == 1) nombreDocumentsDepen = " document.";
                    JOptionPane.showMessageDialog(null, "S'han importat correctament " + contDocsImp +
                            nombreDocumentsDepen, "Importar document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /* único doc*/
        exportarD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Selecciona una carpeta on guardar el document");
                chooser.setMultiSelectionEnabled(false);
                int result = chooser.showOpenDialog(null);
                if (result == chooser.APPROVE_OPTION) {
                    JPanel panelExportacio = new JPanel();
                    JTextField newNom = new JTextField("", 20);
                    JPanel insertNom = new JPanel();
                    insertNom.add(new JLabel("Nom de l'arxiu: "));
                    insertNom.add(newNom);

                    JComboBox tipus = new JComboBox();
                    tipus.addItem("txt");
                    tipus.addItem("xml");
                    tipus.setSize(90, 20);
                    JPanel insertTipus = new JPanel();
                    insertTipus.setLayout(new BorderLayout());
                    insertTipus.add(new JLabel("Tria el format: "), BorderLayout.WEST);
                    insertTipus.add(tipus);

                    panelExportacio.setLayout(new BorderLayout());
                    panelExportacio.add(insertNom, BorderLayout.NORTH);
                    panelExportacio.add(insertTipus, BorderLayout.SOUTH);

                    String[] opts = {"Sí", "Cancel·la"};
                    int opt = JOptionPane.showOptionDialog(null, panelExportacio, "Exportació document",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
                    while (opt == 0 && newNom.getText().matches(".*[\\\\/:*?\"<>|#%&${}!':´`@+=].*")) { //hacer el bucle mientras ponga chars invalidos, funcionan todos bien excepto el \, arreglar
                        JOptionPane.showMessageDialog(null,
                                "No es permeten noms d'arxiu amb \\ / : * ? \" < > | # % & $ { } ! ' : ` ´ @ + =.");
                        opt = JOptionPane.showOptionDialog(null, panelExportacio, "Exportació document",
                                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
                        System.out.println(newNom.getText());
                    }

                    if (opt == 0 && !newNom.getText().equals("")) {
                        String titol = (String) tableModel.getValueAt(documents.getSelectedRow(), 0);
                        String autor = (String) tableModel.getValueAt(documents.getSelectedRow(), 1);
                        String path = chooser.getSelectedFile().getAbsolutePath();
                        String loc = path + "\\" + newNom.getText() + "." + (String) tipus.getSelectedItem();
                        if (cp.exportaDocument(autor, titol, loc)) {
                            JOptionPane.showMessageDialog(null, "S'ha exportat el document correctament.",
                                    "Exportació", JOptionPane.DEFAULT_OPTION);
                        }
                    } else if (opt == 0 && (newNom.getText().equals("") || ((String) tipus.getSelectedItem()).equals(""))) {
                        JOptionPane.showMessageDialog(null, "Indica un nom i un format vàlids, no deixis camps buits.",
                                "Error exportació", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        /*abre vista de gestión de exp booleanas*/
        gestioExpBoolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cp.ocultaViewPrincipal();
                cp.mostraVistaGestioExpBool(documents);
            }
        });

        /*abrir contenido del doc, solo lectura*/
        mostrarD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cp.mostraViewMostrarCont((String) documents.getValueAt(documents.getSelectedRow(), 0), (String) documents.getValueAt(documents.getSelectedRow(), 1));
            }
        });

        /*llistar K documents semblants*/
        llistarSemblants.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 1) {
                    JPanel KPanel = new JPanel();
                    SpinnerModel valueKModel = new SpinnerNumberModel(1, 1, documents.getRowCount() - 1, 1);
                    JSpinner valueK = new JSpinner(valueKModel);
                    JFormattedTextField tf = ((JSpinner.DefaultEditor) valueK.getEditor()).getTextField(); //para evitar modificar por texto
                    tf.setEditable(false);
                    JLabel KLabel = new JLabel("Indica el nombre de documents que vols llistar:");
                    KPanel.setLayout(new BorderLayout());
                    KPanel.add(KLabel, BorderLayout.NORTH);
                    KPanel.add(valueK, BorderLayout.SOUTH);

                    String[] estrategies = {"TF-IDF", "TF"};
                    JComboBox jCBestrategia = new JComboBox(estrategies);
                    jCBestrategia.setEditable(false);
                    JPanel estratPanel = new JPanel();
                    JLabel estratLabel = new JLabel("Selecciona estratègia:");
                    estratPanel.setLayout(new BorderLayout());
                    estratPanel.add(estratLabel, BorderLayout.NORTH);
                    estratPanel.add(jCBestrategia, BorderLayout.SOUTH);

                    JPanel message = new JPanel();
                    message.setLayout(new BorderLayout());
                    message.add(KPanel, BorderLayout.NORTH);
                    message.add(estratPanel, BorderLayout.SOUTH);

                    int opt1 = JOptionPane.showOptionDialog(null, message, "Llistar documents semblants",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.NO_OPTION);
                    if (opt1 == 0) {
                        String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
                        String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
                        boolean estrategia = false;
                        if (jCBestrategia.getSelectedItem().equals("TF")) estrategia = true;
                        List<Pair<String, String>> docsSemblants = cp.llistarKDocumentsS(autor, titol, (int) valueK.getValue(), estrategia);

                        Object[][] docsSemblantsObj = new Object[docsSemblants.size()][2];
                        for (int i = 0; i < docsSemblants.size(); ++i) {
                            Object[] docSemblantsObj = {docsSemblants.get(i).y, docsSemblants.get(i).x};
                            //System.out.println(docsSemblants.get(i).y + docsSemblants.get(i).x);
                            docsSemblantsObj[i] = docSemblantsObj;
                        }
                        String[] columns = {"Títols", "Autors"};
                        DefaultTableModel tm = new DefaultTableModel(docsSemblantsObj, columns) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        JPanel panelDocs = new ShowingDocsTable(tm, documents, cp, true, viewPrin);
                        String intro = "Aquests són els " + valueK.getValue() + " documents més semblants";
                        if ((int) valueK.getValue() == 1) intro = "Aquests és el document més semblant";
                        JLabel label = new JLabel(intro + " al document amb títol: " + titol + " i autor: " +
                                autor + " amb l'estratègia " + jCBestrategia.getSelectedItem() + ".");
                        panelDocs.add(label, BorderLayout.SOUTH);
                        JOptionPane.showMessageDialog(null, panelDocs, "Documents més semblants", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha suficients documents",
                            "Documents més semblants", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*CERQUES*/

        /*Llistar titols d'autor*/
        llistarTdeA.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 0) {
                    List<String> autorsList = cp.getAutors();

                    //PARA ORDENAR POR ORDEN ALFABÉTICO Y NO POR ASCII
                    Locale catalan = new Locale("ca-ES");
                    Collator catalanCollator = Collator.getInstance(catalan);
                    Collections.sort(autorsList, catalanCollator);

                    String[] autorsArray = autorsList.toArray(new String[0]);

                    JComboBox jca = new JComboBox(autorsArray);

                    jca.setEditable(false);

                    Object[] options = new Object[]{"Tria un autor: ", jca};

                    int opt = JOptionPane.showOptionDialog(null, options, "Llistar títols d'autor",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
                    if (opt == 0) {
                        String autorSelec = (String) jca.getSelectedItem();
                        List<String> titols = cp.llistarTitolsdAutors(autorSelec);
                        Object[][] titolsdAutor = new Object[titols.size()][2];
                        for (int i = 0; i < titols.size(); ++i) {
                            Object[] titol = {titols.get(i), autorSelec};
                            titolsdAutor[i] = titol;
                        }
                        String[] cols = {"Títols", "Autor"};
                        DefaultTableModel tm = new DefaultTableModel(titolsdAutor, cols) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        JPanel panelTits = new ShowingDocsTable(tm, documents, cp, true, viewPrin);
                        JOptionPane.showMessageDialog(null, panelTits, "Títols d'autor", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap autor encara, crea o importa un document.",
                            "Error cap document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*llistar autors per prefix*/
        llistarAperP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 0) {
                    JPanel insertPref = new JPanel();
                    JTextField pref = new JTextField("", 20);
                    insertPref.add(new JLabel("Prefix de l'autor a llistar: "));
                    insertPref.add(pref);

                    String[] opts = {"Sí", "Cancel·la"};
                    int opt = JOptionPane.showOptionDialog(null, insertPref, "Llistar autors per prefix",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                    if (opt == 0) {
                        List<String> autors = cp.llistarAutorsPrefix(pref.getText());
                        if (autors.size() > 0) {
                            //PARA ORDENAR POR ORDEN ALFABÉTICO Y NO POR ASCII
                            Locale catalan = new Locale("ca-ES");
                            Collator catalanCollator = Collator.getInstance(catalan);
                            Collections.sort(autors, catalanCollator);

                            Object[][] autorsObj = new Object[autors.size()][1];
                            for (int i = 0; i < autors.size(); ++i) {
                                Object[] autor = {autors.get(i)};
                                autorsObj[i] = autor;
                            }
                            String[] columns = {"Autors"};
                            DefaultTableModel tm = new DefaultTableModel(autorsObj, columns) {
                                @Override
                                public boolean isCellEditable(int row, int column) {
                                    return false;
                                }
                            };
                            JPanel panelAuts = new ShowingDocsTable(tm, documents, cp, false, viewPrin);
                            JOptionPane.showMessageDialog(null, panelAuts, "Autors donat prefix",
                                    JOptionPane.DEFAULT_OPTION);
                        } else {
                            JOptionPane.showMessageDialog(null, "No hi ha cap autor amb el prefix " + pref.getText(),
                                    "Autors donat prefix", JOptionPane.DEFAULT_OPTION);
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap autor encara, crea o importa un document.",
                            "Error cap document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*cerca per rellevància*/
        cercaR.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 0) {
                    JPanel paraulesPanel = new JPanel();
                    JTextField paraulesField = new JTextField("");
                    JLabel paraulesLabel = new JLabel("Escriu les paraules que vols cercar per rellevància, separades per un espai:");
                    paraulesPanel.setLayout(new BorderLayout());
                    paraulesPanel.add(paraulesLabel, BorderLayout.NORTH);
                    paraulesPanel.add(paraulesField, BorderLayout.SOUTH);

                    JPanel KPanel = new JPanel();
                    SpinnerModel valueKModel = new SpinnerNumberModel(1, 1, documents.getRowCount(), 1);
                    JSpinner valueK = new JSpinner(valueKModel);
                    JFormattedTextField tf = ((JSpinner.DefaultEditor) valueK.getEditor()).getTextField(); //para evitar modificar por texto
                    tf.setEditable(false);
                    JLabel KLabel = new JLabel("Indica el nombre de documents que vols llistar:");
                    KPanel.setLayout(new BorderLayout());
                    KPanel.add(KLabel, BorderLayout.NORTH);
                    KPanel.add(valueK, BorderLayout.SOUTH);

                    String[] estrategies = {"TF-IDF", "TF"};
                    JComboBox jCBestrategia = new JComboBox(estrategies);
                    jCBestrategia.setEditable(false);
                    JPanel estratPanel = new JPanel();
                    JLabel estratLabel = new JLabel("Selecciona estratègia:");
                    estratPanel.setLayout(new BorderLayout());
                    estratPanel.add(estratLabel, BorderLayout.NORTH);
                    estratPanel.add(jCBestrategia, BorderLayout.SOUTH);

                    JPanel message = new JPanel();
                    message.setLayout(new BorderLayout());
                    message.add(paraulesPanel, BorderLayout.NORTH);
                    message.add(KPanel, BorderLayout.CENTER);
                    message.add(estratPanel, BorderLayout.SOUTH);

                    int opt1 = JOptionPane.showOptionDialog(null, message, "Cerca per rellevància",
                            JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.NO_OPTION);
                    if (opt1 == 0 && !paraulesField.getText().equals("")) {
                        boolean estrategia = false;
                        if (jCBestrategia.getSelectedItem().equals("TF")) estrategia = true;

                        List<Pair<String, String>> docsCondicio = cp.cercarPerRellevancia(paraulesField.getText(), (int) valueK.getValue(), estrategia);
                        Object[][] docsCondicioObj = new Object[docsCondicio.size()][2];
                        for (int i = 0; i < docsCondicio.size(); ++i) {
                            Object[] docCondicioObj = {docsCondicio.get(i).y, docsCondicio.get(i).x};
                            docsCondicioObj[i] = docCondicioObj;
                        }
                        String[] columns = {"Títols", "Autors"};
                        DefaultTableModel tm = new DefaultTableModel(docsCondicioObj, columns) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        JPanel panelDocs = new ShowingDocsTable(tm, documents, cp, true, viewPrin);
                        String intro = "Aquests són els " + valueK.getValue() + " documents més rellevants";
                        if ((int) valueK.getValue() == 1) intro = "Aquest és el document més rellevant";
                        JLabel label = new JLabel(intro + " segons les paraules escollides amb l'estratègia " + jCBestrategia.getSelectedItem() + ".");
                        panelDocs.add(label, BorderLayout.SOUTH);
                        JOptionPane.showMessageDialog(null, panelDocs, "Documents segons la cerca per rellevància",
                                JOptionPane.DEFAULT_OPTION);
                    } else if (opt1 == 0 && paraulesField.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "El conjunt de paraules no pot ser buit.",
                                "Error paraules introduïdes", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap document encara, els pots crear o importar.",
                            "Error cap document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*cerca x expressió booleana*/
        cercaExp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 0) {
                    JPanel panelBusquedaExp = new JPanel();
                    JTextField newExp = new JTextField("", 45);
                    JPanel insertExp = new JPanel();
                    insertExp.add(new JLabel("Escriu l'expressió per cercar: "));
                    insertExp.add(newExp);
                    panelBusquedaExp.setLayout(new BorderLayout());
                    panelBusquedaExp.add(insertExp, BorderLayout.CENTER);

                    String[] tox = {"Cerca", "Cancel·lar"};
                    int opt1 = JOptionPane.showOptionDialog(null, panelBusquedaExp,
                            "Cercar per expressió booleana", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION,
                            null, tox, tox[0]);
                    if (opt1 == 0 && !newExp.getText().equals("")) {
                        List<Pair<String, String>> docsXExp = cp.cercarExpressioBooleana(newExp.getText());

                        if (docsXExp != null) { //exp booleana és vàlida
                            String[] tox2 = {"Guardar expressió", "Cancel·lar"};
                            int opt2;
                            if (docsXExp.size() > 0) {
                                Object[][] docsSemblantsObj = new Object[docsXExp.size()][2];
                                for (int i = 0; i < docsXExp.size(); ++i) {
                                    Object[] docSemblantsObj = {docsXExp.get(i).y, docsXExp.get(i).x};
                                    docsSemblantsObj[i] = docSemblantsObj;
                                }
                                String[] columns = {"Títols", "Autors"};
                                DefaultTableModel tm = new DefaultTableModel(docsSemblantsObj, columns) {
                                    @Override
                                    public boolean isCellEditable(int row, int column) {
                                        return false;
                                    }
                                };

                                JPanel panelDocs = new ShowingDocsTable(tm, documents, cp, true, viewPrin);

                                opt2 = JOptionPane.showOptionDialog(null, panelDocs,
                                        "Resultats de cerca per expressió", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION,
                                        null, tox2, tox2[1]);
                            } else {
                                opt2 = JOptionPane.showOptionDialog(null, "No hi ha cap document que satisfagui l'expressió.",
                                        "Resultats de cerca per expressió", JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION,
                                        null, tox2, tox2[1]);
                            }

                            if (opt2 == 0) { //guardar l'expressió
                                JPanel panelGuardarExp = new JPanel();
                                JTextField newNom = new JTextField("", 20);
                                JPanel insertNom = new JPanel();
                                insertNom.add(new JLabel("Nom expressió: "));
                                insertNom.add(newNom);
                                panelGuardarExp.setLayout(new BorderLayout());
                                panelGuardarExp.add(insertNom, BorderLayout.NORTH);
                                JPanel expresioIntroduida = new JPanel();
                                newExp.setEditable(false);
                                expresioIntroduida.add(new JLabel("Expressió: "));
                                expresioIntroduida.add(newExp);
                                panelGuardarExp.add(expresioIntroduida, BorderLayout.SOUTH);

                                String[] opts = {"Sí", "Cancel·la"};
                                int opt3 = JOptionPane.showOptionDialog(null, panelGuardarExp, "Guardar nova expressió booleana",
                                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                                if (!newNom.getText().equals("") && opt3 == 0) {
                                    boolean creat = cp.creaExpressioBooleana(newNom.getText(), newExp.getText());
                                    if (creat) {
                                        JOptionPane.showMessageDialog(null, "S'ha guardat l'expressió booleana amb el nom: " +
                                                newNom.getText() + ".", "Guardar nova expressió booleana", JOptionPane.DEFAULT_OPTION);
                                    }
                                } else if (opt3 == 0) {
                                    JOptionPane.showMessageDialog(null, "Indica un nom vàlid, no deixis camps buits.",
                                            "Error guardar expressió", JOptionPane.ERROR_MESSAGE);
                                }
                            }
                        }
                    } else if (opt1 == 0 && newExp.getText().equals("")) {
                        JOptionPane.showMessageDialog(null, "No es permeten expressions booleanes buides.",
                                "Error expressió", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap document encara, els pots crear o importar.",
                            "Error cap document", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*indicaciones de los botones*/
        ajudaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = 1; //1 primera vez, -1 se cierra ventana
                int opt = JOptionPane.showOptionDialog(null, "Amb el botó \"Nou document\" " +
                                "es crea un nou document amb contingut buit.", "Panell d'ajuda", JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Següent"}, "Següent");
                String message = ""; //COM NO HI HAN ELS BLOCS DE TEXT A AQUESTA VERSIÓ DE JAVA, HO HE DE FER D'AQUESTA FORMA
                while (count < 10 && opt != -1) {
                    if (count == 0)
                        message = "Amb el botó \"Nou document\" es crea un nou document amb contingut buit.";
                    else if (count == 1)
                        message = "                      Amb el botó \"Importa\" es crea un nou document amb el contingut del document seleccionat. " +
                                "\nNomés es pot importar documents de tipus .txt o .xml, amb màxim 10 documents a la vegada. Si es seleccionen més " +
                                "\n                                                 només s'importaran els 10 primers en ordre alfabètic.";
                    else if (count == 2)
                        message = "Amb el botó \"Esborrar documents seleccionats\" s'esborren els documents que estiguin seleccionats " +
                                "\n                  a la taula. Es poden seleccionar arrastrant amb el ratolí o clicant amb Ctrl+Clic.";
                    else if (count == 3)
                        message = "Amb el botó \"Gestió expressions booleanes\" s'obre una vista per gestionar les expressions guardades.";
                    else if (count == 4)
                        message = "El botó de \"Cerca\" mostra un desplegable amb 4 opcions de filtratge dels documents.";
                    else if (count == 5)
                        message = "Amb els 3 punts verticals de cada document s'obre un panell d'opcions pel document.";
                    else if (count == 6)
                        message = "           També hi ha les funcionalitats de poder fer clic dret sobre un document per mostrar les seves opcions " +
                                "\n(equival als 3 punts verticals). I la funcionalitat de fer clic dret sobre un dels documents seleccionats i es mostrarà " +
                                "\n                                                         un botó per esborrar tots els documents seleccionats.";
                    else if (count == 7)
                        message = "Es poden ordenar els documents clicant en els noms de les columnes de la taula (\"Títols\"," +
                                "\n                                                       \"Autors\" i \"Darrera modificació\").";
                    else if (count == 8)
                        message = "Els documents mostrats a les cerques, es poden obrir per editar fent doble clic a sobre.";
                    else if (count == 9)
                        message = "Es pot tancar el programa en qualsevol moment clicant al botó amb la X de la cantonada superior dreta.";
                    if (count == 0)
                        opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Següent"}, "Següent");
                    else if (count < 9)
                        opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Anterior", "Següent"}, "Següent");
                    else
                        opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda", JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Anterior", "Acaba"}, "Acaba");
                    if (opt == 0 && count != 0) --count;
                    else ++count;
                }
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                /*sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                columnRepetida = 1;
                sorter.setSortKeys(sortKeys);
                sorter.setSortable(3, false);*/
                cp.tancarAplicacio();
                dispose();
                //cp.mostraViewPrincipal();
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        validate();
    }

    /*private void borrarDocs(Object[][] documentsBorrar) {
        List<Pair<String, String>> docsBorrarList = new ArrayList<>();
        Pair<String,String> p = new Pair();
        for(int i = 0; i < documentsBorrar.length; ++i) {
            p.x = (String) documentsBorrar[i][1];
            p.y = (String) documentsBorrar[i][0];
            //System.out.println(p.x + p.y);
            docsBorrarList.add(p);
        }
        cp.esborrarDocuments(docsBorrarList);
    }*/

    /**
     * Metode per cambiar el titol d'un document obert
     *
     * @param newT Nou titol del document obert, el document obert esta seleccionat en la JTable documents
     */
    public void actualitzaTitol(String newT) {
        String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
        String data = (String) documents.getValueAt(documents.getSelectedRow(), 2);
        tableModel.addRow(new Object[]{newT, autor, data});
        tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
        int row = -1;
        for (int i = 0; i < documents.getRowCount() && row == -1; ++i) {
            if (((String) documents.getValueAt(i, 0)).equals(newT) && ((String) documents.getValueAt(i, 1)).equals(autor)) {
                row = i;
            }
        }
        documents.clearSelection();
        documents.addRowSelectionInterval(row, row);
    }

    /**
     * Metode per cambiar l'autor d'un document obert
     *
     * @param newA Nou autor del document obert, el document obert esta seleccionat en la JTable documents
     */
    public void actualitzaAutor(String newA) {
        String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
        String data = (String) documents.getValueAt(documents.getSelectedRow(), 2);
        tableModel.addRow(new Object[]{titol, newA, data});
        tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
        int row = -1;
        for (int i = 0; i < documents.getRowCount() && row == -1; ++i) {
            if (((String) documents.getValueAt(i, 0)).equals(titol) && ((String) documents.getValueAt(i, 1)).equals(newA)) {
                row = i;
            }
        }
        documents.clearSelection();
        documents.addRowSelectionInterval(row, row);
    }

    /**
     * Metode per cambiar la darrera modificacio d'un document obert
     *
     * @param date Nova darrera modificacio del document obert, el document obert esta seleccionat en la JTable documents
     */
    public void actualitzaDarreraModificacio(String date) {
        String titol = (String) documents.getValueAt(documents.getSelectedRow(), 0);
        String autor = (String) documents.getValueAt(documents.getSelectedRow(), 1);
        tableModel.addRow(new Object[]{titol, autor, date});
        tableModel.removeRow(documents.convertRowIndexToModel(documents.getSelectedRow()));
        int row = -1;
        for (int i = 0; i < documents.getRowCount() && row == -1; ++i) {
            if (((String) documents.getValueAt(i, 0)).equals(titol) && ((String) documents.getValueAt(i, 1)).equals(autor)) {
                row = i;
            }
        }
        documents.clearSelection();
        documents.addRowSelectionInterval(row, row);
    }

    /**
     * Metode que retorna el titol del document obert
     *
     * @return Titol del document obert, el document obert esta seleccionat a la JTable documents
     */
    public String getTitolDocObert() {
        return (String) documents.getValueAt(documents.getSelectedRow(), 0);
    }

    /**
     * Metode que retorna l'autor del document obert
     *
     * @return Autor del document obert, el document obert esta seleccionat a la JTable documents
     */
    public String getAutorDocObert() {
        return (String) documents.getValueAt(documents.getSelectedRow(), 1);
    }

    /**
     * Metode que incialitza la JTable documents amb els documents que hi ha guardats al sistema
     *
     * @param docsList Llista de pairs (autor+titol) dels documents guardats
     */
    public void initDocs(List<Pair<Pair<String, String>, String>> docsList) {
        for (Pair<Pair<String, String>, String> p : docsList) {
            tableModel.addRow(new Object[]{p.x.y, p.x.x, p.y});
        }
        contadorDocs.setText(Integer.toString(documents.getRowCount()));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new FormLayout("left:214px:noGrow,fill:178px:grow", "center:d:grow"));
        panel1.setBackground(new Color(-4109001));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel2.setBackground(new Color(-9273651));
        CellConstraints cc = new CellConstraints();
        panel1.add(panel2, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        creaButton = new JButton();
        creaButton.setBorderPainted(true);
        creaButton.setContentAreaFilled(true);
        creaButton.setDefaultCapable(true);
        creaButton.setDoubleBuffered(false);
        Font creaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, creaButton.getFont());
        if (creaButtonFont != null) creaButton.setFont(creaButtonFont);
        creaButton.setText("Nou document");
        creaButton.setMnemonic('N');
        creaButton.setDisplayedMnemonicIndex(0);
        panel2.add(creaButton, cc.xy(1, 1));
        importaButton = new JButton();
        Font importaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, importaButton.getFont());
        if (importaButtonFont != null) importaButton.setFont(importaButtonFont);
        importaButton.setText("Importa");
        importaButton.setMnemonic('I');
        importaButton.setDisplayedMnemonicIndex(0);
        panel2.add(importaButton, cc.xy(1, 3));
        ajudaButton = new JButton();
        Font ajudaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, ajudaButton.getFont());
        if (ajudaButtonFont != null) ajudaButton.setFont(ajudaButtonFont);
        ajudaButton.setText("Ajuda");
        ajudaButton.setMnemonic('A');
        ajudaButton.setDisplayedMnemonicIndex(0);
        panel2.add(ajudaButton, cc.xy(1, 19));
        gestioExpBoolButton = new JButton();
        Font gestioExpBoolButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, gestioExpBoolButton.getFont());
        if (gestioExpBoolButtonFont != null) gestioExpBoolButton.setFont(gestioExpBoolButtonFont);
        gestioExpBoolButton.setHorizontalAlignment(0);
        gestioExpBoolButton.setHorizontalTextPosition(0);
        gestioExpBoolButton.setText("Gestió expressions booleanes");
        gestioExpBoolButton.setMnemonic('G');
        gestioExpBoolButton.setDisplayedMnemonicIndex(0);
        panel2.add(gestioExpBoolButton, cc.xy(1, 15));
        busquedaButton = new JButton();
        Font busquedaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, busquedaButton.getFont());
        if (busquedaButtonFont != null) busquedaButton.setFont(busquedaButtonFont);
        busquedaButton.setHideActionText(false);
        busquedaButton.setText("Cerca >");
        busquedaButton.setMnemonic('C');
        busquedaButton.setDisplayedMnemonicIndex(0);
        panel2.add(busquedaButton, cc.xy(1, 17));
        esborrarDocsButton = new JButton();
        Font esborrarDocsButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, esborrarDocsButton.getFont());
        if (esborrarDocsButtonFont != null) esborrarDocsButton.setFont(esborrarDocsButtonFont);
        esborrarDocsButton.setText("Esborrar documents seleccionats");
        esborrarDocsButton.setMnemonic('E');
        esborrarDocsButton.setDisplayedMnemonicIndex(0);
        panel2.add(esborrarDocsButton, cc.xy(1, 13));
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$(null, -1, -1, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setForeground(new Color(-16777216));
        label1.setText("Nombre de documents:");
        panel2.add(label1, cc.xy(1, 7, CellConstraints.CENTER, CellConstraints.BOTTOM));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, cc.xy(1, 11, CellConstraints.DEFAULT, CellConstraints.FILL));
        contadorDocs = new JLabel();
        Font contadorDocsFont = this.$$$getFont$$$(null, -1, -1, contadorDocs.getFont());
        if (contadorDocsFont != null) contadorDocs.setFont(contadorDocsFont);
        contadorDocs.setForeground(new Color(-16777216));
        contadorDocs.setText("");
        panel2.add(contadorDocs, cc.xy(1, 9, CellConstraints.CENTER, CellConstraints.DEFAULT));
        final Spacer spacer2 = new Spacer();
        panel2.add(spacer2, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(tablePanel, cc.xy(2, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        Font font = new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
        boolean isMac = System.getProperty("os.name", "").toLowerCase(Locale.ENGLISH).startsWith("mac");
        Font fontWithFallback = isMac ? new Font(font.getFamily(), font.getStyle(), font.getSize()) : new StyleContext().getFont(font.getFamily(), font.getStyle(), font.getSize());
        return fontWithFallback instanceof FontUIResource ? fontWithFallback : new FontUIResource(fontWithFallback);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}



