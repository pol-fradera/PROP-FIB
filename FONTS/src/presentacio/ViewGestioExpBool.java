package presentacio;

import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import transversal.Pair;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Locale;

import static javax.swing.SwingUtilities.isRightMouseButton;

/**
 * Vista amb la qual es fan les gestions sobre les expressions booleanes
 *
 * @author Christian Rivero
 */
public class ViewGestioExpBool extends JFrame {
    /**
     * Panell principal de la vista
     */
    private JPanel panel1;
    /**
     * Panell on s’ubica la JTable on es llisten les expressions booleanes hi ha al sistema (nomExpressio+expressio)
     */
    private JPanel tablePanel;
    /**
     * Boto per afegir una nova expressio booleana al sistema.
     */
    private JButton novaExpressioBooleanaButton;
    /**
     * Boto per mostrar una serie de popups amb indicacions de les funcions de cada boto i accio possible a la vista
     */
    private JButton ajudaButton;
    /**
     * Boto per esborrar el conjunt d’expressions seleccionades
     */
    private JButton esborrarExpressionsSeleccionadesButton;
    /**
     * Boto per tancar aquesta vista i tornar a la vista principal
     */
    private JButton enrereButton;
    /**
     * Label on indiquem el nombre d'expressions booleanes del sistema en tot moment
     */
    private JLabel contadorExp;
    /**
     * DefaultTableModel necessaria per crear la JTable de les expressions
     */
    private DefaultTableModel tableModel;
    /**
     * Objecte this, necessari per poder passa-ho com a parametre als actionListeners
     */
    private ViewGestioExpBool exp = this;

    /**
     * Creadora unica
     *
     * @param documents taula dels documents, per tal de poder clicar en la row pertinent si es fan les cerques per expressio
     * @param cp        instancia del controlador de presentacio
     */
    public ViewGestioExpBool(JTable documents, CtrlPresentacio cp) {
        setContentPane(panel1);
        setSize(900, 450);
        setMinimumSize(new Dimension(500, 300));
        setTitle("Gestió expressions booleanes");

        /*Creacion tabla*/
        String[] colums = {"Nom", "Expressio Booleana", "Opcions"};
        Object[][] titols = {};

        tableModel = new DefaultTableModel(titols, colums) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable expressions = new JTable(tableModel);
        expressions.setAutoCreateRowSorter(true);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(expressions.getModel());
        sorter.setSortable(0, true);
        sorter.setSortable(1, false);
        sorter.setSortable(2, false);
        expressions.setRowSorter(sorter);
        //expressions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //expressions.setEnabled(false);///arreglar, necesito bloquear edicion tabla

        tablePanel.setLayout(new BorderLayout());
        JScrollPane tableScroll = new JScrollPane(expressions);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        expressions.getColumnModel().getColumn(0).setCellRenderer(new GestioCell("text"));
        expressions.getColumnModel().getColumn(1).setCellRenderer(new GestioCell("text"));
        expressions.getColumnModel().getColumn(2).setCellRenderer(new GestioCell("icon"));
        expressions.setRowHeight(25);
        expressions.getColumnModel().getColumn(0).setPreferredWidth(100);
        expressions.getColumnModel().getColumn(1).setPreferredWidth(100);
        expressions.getColumnModel().getColumn(2).setPreferredWidth(25);
        expressions.getTableHeader().setReorderingAllowed(false);

        /*Creacion popup opciones*/
        JMenuItem modificaE = new JMenuItem("Modifica");
        JMenuItem eliminaE = new JMenuItem("Elimina");
        JMenuItem buscaXE = new JMenuItem("Cerca");
        JPopupMenu popOptExp = new JPopupMenu();
        popOptExp.add(modificaE);
        popOptExp.add(eliminaE);
        popOptExp.add(buscaXE);

        JPopupMenu popBorrarExp = new JPopupMenu();
        JMenuItem eliminaExpsSelected = new JMenuItem("Esborra selecció");
        popBorrarExp.add(eliminaExpsSelected);

        expressions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (expressions.getRowCount() > 0) {
                    if (isRightMouseButton(e)) {
                        int row = expressions.rowAtPoint(e.getPoint());
                        boolean botoDretSeleccionat = false;
                        int[] selection = expressions.getSelectedRows();
                        for (int i = 0; i < selection.length && !botoDretSeleccionat; ++i) { ////MEJORABLE? SE PUEDE METER EN UN SET?
                            if (selection[i] == row) botoDretSeleccionat = true;
                        }
                        if (botoDretSeleccionat) { //si está seleccionada previamente -> borrarDocs
                            popBorrarExp.show(e.getComponent(), e.getX(), e.getY());
                        } else { //abrir popups opciones en un doc
                            expressions.clearSelection();
                            expressions.addRowSelectionInterval(row, row);
                            popOptExp.show(e.getComponent(), e.getX(), e.getY());
                        }
                    } else {
                        int columna = expressions.columnAtPoint(e.getPoint());
                        if (columna == 2) {
                            popOptExp.show(e.getComponent(), e.getX(), e.getY());
                        }
                    }
                } else if (expressions.getRowCount() == 0 && e.getClickCount() == 0) {
                    JOptionPane.showMessageDialog(null, "No hi ha cap expressió encara, les pots crear.",
                            "Error cap expressió", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /* Se puede borrar desde el boton o con boton derecho sobre seleccion */
        ActionListener esborrarExpSeleccionatsAction = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (expressions.getRowCount() > 0) {
                    /*Creamos la tabla*/
                    if (expressions.getSelectedRowCount() > 0) {
                        Object[][] expressionsBorrar = new Object[expressions.getSelectedRowCount()][2];
                        Object[] columnsBorrar = {"Títols", "Autors"};

                        int index = 0;
                        int selectedRow[] = expressions.getSelectedRows();
                        for (int i : selectedRow) {
                            Object[] docBorrar = {expressions.getValueAt(i, 0), expressions.getValueAt(i, 1)};
                            expressionsBorrar[index] = docBorrar;
                            ++index;
                            //System.out.println(selectedRow[i]);
                        }

                        DefaultTableModel tm = new DefaultTableModel(expressionsBorrar, columnsBorrar) {
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                return false;
                            }
                        };
                        JPanel panelBorrar = new ShowingDocsTable(tm, expressions, cp, false, exp);
                        panelBorrar.add(new JLabel("S'esborraran les següents expressions booleanes:"), BorderLayout.NORTH);
                        panelBorrar.add(new JLabel("Estàs d'acord?"), BorderLayout.SOUTH);
                        //panelBorrar.setFillsViewportHeight(true);

                        /*Mostramos tabla*/
                        int opt = JOptionPane.showOptionDialog(null, panelBorrar, "Esborrar expressions seleccionades",
                                JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, JOptionPane.NO_OPTION);
                        if (opt == 0) { //s'esborren + missatge
                            while (selectedRow.length != 0) {
                                cp.deleteExpressioBooleana((String) expressions.getValueAt(selectedRow[0], 0));
                                tableModel.removeRow(expressions.convertRowIndexToModel(selectedRow[0]));
                                //System.out.println(tableModel.getValueAt(expressions.convertRowIndexToModel(selectedRow[0]), 0));
                                selectedRow = expressions.getSelectedRows();
                            }
                            contadorExp.setText(Integer.toString(expressions.getRowCount()));
                            JOptionPane.showMessageDialog(null, "S'han esborrat correctament les expressions.",
                                    "Esborrar expressions seleccionades", JOptionPane.DEFAULT_OPTION);
                        } else { //misstage no s'han borrat
                            JOptionPane.showMessageDialog(null, "No s'ha esborrat cap expressió.",
                                    "Esborrar expressions seleccionades", JOptionPane.DEFAULT_OPTION);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No hi ha cap expressió seleccionada.",
                                "Error esborrar expressions seleccionades", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No hi ha cap expressió creada.",
                            "Cap expressió", JOptionPane.DEFAULT_OPTION);
                }
            }
        };

        eliminaE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int opt = JOptionPane.showConfirmDialog(null, "Segur que vols esborrar l'expressió booleana amb nom: " +
                                expressions.getValueAt(expressions.getSelectedRow(), 0) + " permanentment?", "Esborrar expressió booleana",
                        JOptionPane.YES_NO_OPTION);
                if (opt == 0) {
                    cp.deleteExpressioBooleana((String) expressions.getValueAt(expressions.getSelectedRow(), 0));
                    tableModel.removeRow(expressions.convertRowIndexToModel(expressions.getSelectedRow()));
                    contadorExp.setText(Integer.toString(expressions.getRowCount()));
                    JOptionPane.showMessageDialog(null, "S'ha esborrat l'expressió booleana correctament.",
                            "Esborrar expressió booleana", JOptionPane.DEFAULT_OPTION);
                } else {
                    JOptionPane.showMessageDialog(null, "No s'ha esborrat l'expressió booleana.",
                            "Esborrar expressió booleana", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        eliminaExpsSelected.addActionListener(esborrarExpSeleccionatsAction);
        esborrarExpressionsSeleccionadesButton.addActionListener(esborrarExpSeleccionatsAction);

        /*Opcion de modificar*/
        modificaE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String antigaExp = (String) expressions.getValueAt(expressions.getSelectedRow(), 1);
                JTextField newExp = new JTextField(antigaExp, 45);
                JPanel insertExp = new JPanel();
                insertExp.add(new JLabel("Nova expressió booleana: "));
                insertExp.add(newExp);

                String[] opts = {"Sí", "Cancel·la"};
                int opt = JOptionPane.showOptionDialog(null, insertExp, "Modificar expressió booleana",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);
                if (opt == 0 && !newExp.getText().equals("") && !newExp.getText().equals(antigaExp)) {
                    int opt2 = JOptionPane.showConfirmDialog(null, "Segur que vols modificar l'expressió booleana amb nom: " +
                                    expressions.getValueAt(expressions.getSelectedRow(), 0) + " a \"" + newExp.getText() + "\" ?",
                            "Modificar expressió booleana", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
                    if (opt2 == 0) {
                        boolean valida = cp.modExpressioBooleana((String) expressions.getValueAt(expressions.getSelectedRow(), 0), newExp.getText());
                        if (valida) {
                            //expressions.setValueAt(newExp, expressions.getSelectedRow(), 1);
                            tableModel.addRow(new Object[]{expressions.getValueAt(expressions.getSelectedRow(), 0), newExp.getText()});
                            tableModel.removeRow(expressions.convertRowIndexToModel(expressions.getSelectedRow()));
                            JOptionPane.showMessageDialog(null, "S'ha modificat correctament l'expressió",
                                    "Modificar expressió booleana", JOptionPane.DEFAULT_OPTION);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "No s'ha modificat l'expressió.",
                                "Modificar expressió booleana", JOptionPane.DEFAULT_OPTION);
                    }
                } else if (opt == 0 && newExp.getText().equals((String) expressions.getValueAt(expressions.getSelectedRow(), 1))) {
                    JOptionPane.showMessageDialog(null, "Ja té aquesta expressió.",
                            "Error modificació booleana", JOptionPane.DEFAULT_OPTION);
                } else if (opt == 0 && newExp.getText().equals("")) { //camp buit
                    JOptionPane.showMessageDialog(null, "Indica una expressió vàlida, no deixis camps buits.",
                            "Error modificació booleana", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*Buscar per exp bool*/
        buscaXE.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (documents.getRowCount() > 0) {
                    List<Pair<String, String>> docsXExp = cp.cercarExpressioBooleanaNom((String) expressions.getValueAt(expressions.getSelectedRow(), 0));

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

                        JPanel panelDocs = new ShowingDocsTable(tm, documents, cp, true, exp);
                        JOptionPane.showMessageDialog(null, panelDocs, "Resultats de cerca per expressió", JOptionPane.DEFAULT_OPTION);
                    } else {
                        JOptionPane.showMessageDialog(null, "No hi ha cap document que satisfagui l'expressió.",
                                "Cerca per expressió booleana", JOptionPane.DEFAULT_OPTION);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Encara no hi ha cap document.",
                            "Error cerca", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        /*Nova exp bool*/
        novaExpressioBooleanaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField newNom = new JTextField("", 45);
                JPanel insertNom = new JPanel();
                insertNom.add(new JLabel("Nom de l'expressió: "));
                insertNom.add(newNom);

                JTextField newExp = new JTextField("", 45);
                JPanel insertExp = new JPanel();
                insertExp.add(new JLabel("Expressió booleana: "));
                insertExp.add(newExp);

                JPanel panelCreacio = new JPanel();
                panelCreacio.setLayout(new BorderLayout());
                panelCreacio.add(insertNom, BorderLayout.NORTH);
                panelCreacio.add(insertExp, BorderLayout.SOUTH);

                String[] opts = {"Sí", "Cancel·la"};
                int opt = JOptionPane.showOptionDialog(null, panelCreacio, "Creacio expressió booleana",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, opts, opts[0]);

                if (opt == 0 && !newNom.getText().equals("") && !newNom.getText().equals("")) {
                    boolean creat = cp.creaExpressioBooleana(newNom.getText(), newExp.getText());
                    if (creat) {
                        tableModel.addRow(new Object[]{newNom.getText(), newExp.getText()});
                    }
                    contadorExp.setText(Integer.toString(expressions.getRowCount()));
                } else if (opt == 0 && (newNom.getText().equals("") || newNom.getText().equals(""))) {
                    JOptionPane.showMessageDialog(null, "Indica un nom i una expressió vàlides, no deixis camps buits.",
                            "Error creació expressió booleana", JOptionPane.DEFAULT_OPTION);
                }
            }
        });

        enrereButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cp.mostraViewPrincipal();
                dispose();
                //cp.mostraViewPrincipal();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                cp.tancarAplicacio();
                dispose();
                //cp.mostraViewPrincipal();
            }
        });

        ajudaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int count = 1; //1 primera vez, -1 se cierra ventana
                int opt = JOptionPane.showOptionDialog(null,
                        "Amb el botó \"Nova expressió booleana\" es crea una nova expressió.", "Panell d'ajuda",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Següent"}, "Següent");
                //while(opt == -2);
                String message = "";
                while (count < 6 && opt != -1) {
                    if (count == 0) message = "Amb el botó \"Nova expressió booleana\" es crea una nova expressió.";
                    else if (count == 1) message =
                            "Amb el botó \"Esborrar seleccio\" s'esborren les expressions que estiguin seleccionades" +
                                    "\n         a la taula. Es pot seleccionar arrastrant amb el ratolí o clicant amb Ctrl+Clic.";
                    else if (count == 2) message = "Amb el botó \"Enrere\" es torna a la pantalla principal.";
                    else if (count == 3)
                        message = "Amb els 3 punts verticals de cada expressió s'obre un panell d'opcions per l'expressió.";
                    else if (count == 4) message =
                            "            També hi ha les funcionalitats de poder fer clic dret sobre una expressió per mostrar les seves opcions " +
                                    "\n(equival als 3 punts verticals). I la funcionalitat de fer clic dret sobre una de les expressions seleccionades i es mostrarà " +
                                    "\n                                                un botó per borrar totes les expressions seleccionades.";
                    else if (count == 5)
                        message = "Es pot tancar el programa en qualsevol moment clicant al boto amb la X de la cantonada superior dreta.";
                    if (count == 0) opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Següent"}, "Següent");
                    else if (count < 5) opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Anterior", "Següent"},
                            "Següent");
                    else opt = JOptionPane.showOptionDialog(null, message, "Panell d'ajuda",
                                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Anterior", "Acaba"},
                                "Acaba");
                    if (opt == 0 && count != 0) --count;
                    else ++count;
                }
            }
        });

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        validate();
    }

    /**
     * Metode que incialitza la JTable expressions amb les expressions booleanes que hi ha guardades al sistema
     *
     * @param expList Llista de pairs (nomExp+expBool) de les expressions guardades
     */
    public void initExp(List<Pair<String, String>> expList) {
        for (int i = 0; i < expList.size(); ++i) {
            Pair p = expList.get(i);
            tableModel.addRow(new Object[]{p.x, p.y});
        }
        contadorExp.setText(Integer.toString(tableModel.getRowCount()));
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
        panel1.setLayout(new FormLayout("fill:d:grow", "center:d:grow"));
        panel1.setBackground(new Color(-4109001));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("left:min(m;206px):noGrow,fill:d:grow", "center:d:grow"));
        panel2.setBackground(new Color(-4109001));
        CellConstraints cc = new CellConstraints();
        panel1.add(panel2, cc.xy(1, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new FormLayout("fill:200px:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel3.setBackground(new Color(-9273651));
        panel2.add(panel3, cc.xy(1, 1, CellConstraints.FILL, CellConstraints.FILL));
        ajudaButton = new JButton();
        Font ajudaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, ajudaButton.getFont());
        if (ajudaButtonFont != null) ajudaButton.setFont(ajudaButtonFont);
        ajudaButton.setText("Ajuda");
        ajudaButton.setMnemonic('A');
        ajudaButton.setDisplayedMnemonicIndex(0);
        panel3.add(ajudaButton, cc.xy(1, 13));
        enrereButton = new JButton();
        Font enrereButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, enrereButton.getFont());
        if (enrereButtonFont != null) enrereButton.setFont(enrereButtonFont);
        enrereButton.setText("Enrere");
        panel3.add(enrereButton, cc.xy(1, 15));
        novaExpressioBooleanaButton = new JButton();
        Font novaExpressioBooleanaButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, novaExpressioBooleanaButton.getFont());
        if (novaExpressioBooleanaButtonFont != null)
            novaExpressioBooleanaButton.setFont(novaExpressioBooleanaButtonFont);
        novaExpressioBooleanaButton.setText("Nova expressió booleana");
        novaExpressioBooleanaButton.setMnemonic('N');
        novaExpressioBooleanaButton.setDisplayedMnemonicIndex(0);
        panel3.add(novaExpressioBooleanaButton, cc.xy(1, 1));
        esborrarExpressionsSeleccionadesButton = new JButton();
        Font esborrarExpressionsSeleccionadesButtonFont = this.$$$getFont$$$("Roboto Light", Font.PLAIN, 12, esborrarExpressionsSeleccionadesButton.getFont());
        if (esborrarExpressionsSeleccionadesButtonFont != null)
            esborrarExpressionsSeleccionadesButton.setFont(esborrarExpressionsSeleccionadesButtonFont);
        esborrarExpressionsSeleccionadesButton.setText("Esborrar selecció");
        esborrarExpressionsSeleccionadesButton.setMnemonic('E');
        esborrarExpressionsSeleccionadesButton.setDisplayedMnemonicIndex(0);
        panel3.add(esborrarExpressionsSeleccionadesButton, cc.xy(1, 11));
        final JLabel label1 = new JLabel();
        label1.setForeground(new Color(-16777216));
        label1.setText("Nombre d'expressions:");
        panel3.add(label1, cc.xy(1, 5, CellConstraints.CENTER, CellConstraints.BOTTOM));
        final Spacer spacer1 = new Spacer();
        panel3.add(spacer1, cc.xy(1, 9, CellConstraints.DEFAULT, CellConstraints.FILL));
        final Spacer spacer2 = new Spacer();
        panel3.add(spacer2, cc.xy(1, 3, CellConstraints.DEFAULT, CellConstraints.FILL));
        contadorExp = new JLabel();
        contadorExp.setForeground(new Color(-16777216));
        contadorExp.setText("");
        panel3.add(contadorExp, cc.xy(1, 7, CellConstraints.CENTER, CellConstraints.DEFAULT));
        tablePanel = new JPanel();
        tablePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel2.add(tablePanel, cc.xy(2, 1, CellConstraints.DEFAULT, CellConstraints.FILL));
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
