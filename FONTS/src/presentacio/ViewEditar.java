package presentacio;
import com.intellij.uiDesigner.core.Spacer;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Vista per editar, exportar i desar les modificacions del document seleccionat a la ViewPrincipal.
 *
 * @author Marc Roman
 */
public class ViewEditar extends JFrame {
    /**
     * Panell principal de la vista.
     */
    private JPanel panel1;
    /**
     * Boto per desar les modificacions del contingut del document que esta obert.
     */
    protected JButton desarButton;
    /**
     * Boto per exportar el document que esta obert.
     */
    protected JButton exportarButton;
    /**
     * Boto per tornar enrere a ViewPrincipal.
     */
    private JButton sortirButton;
    /**
     * Etiqueta amb el titol del document.
     */
    protected JLabel titol;
    /**
     * Etiqueta amb l’autor del document.
     */
    protected JLabel autor;
    /**
     * Panell per mostrar i modificar el contingut del document.
     */
    protected JTextPane textPane1;
    /**
     * Contingut del document de l’ultima vegada que s’ha desat.
     */
    private String cont;
    /**
     * Controlador de la capa de presentacio.
     */
    private CtrlPresentacio cp;

    /**
     * Pregunta a l’usuari si vol desar el document abans de sortir de la vista o tancar el programa. En cas que l’usuari accepti, es guarda el contingut del document a contAct a CtrlDomini, altrament no es guarda.
     *
     * @param contNou Contingut actual del document sense desar.
     * @param sortir  Boolea que indica si es vol tancar el programa o sortir a la pantalla principal.
     * @return Es retorna un enter, que si es 0, vol dir que s'ha guardat, altrament vol dir que no s'ha guardat.
     */
    private int desarAbansDeTancar(String contNou, boolean sortir) {
        String frase = "No has desat el document. El vols desar abans de ";
        if (sortir) frase += "tornar a la pantalla d'inici?";
        else frase += "tancar el programa?";
        int opt, opt2 = 1;
        do {
            opt = JOptionPane.showConfirmDialog(null, frase, "Desar document", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
            if (opt == 0) {
                String data = LocalDate.now() + " " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                cp.modificarContingut(contNou, data);
                cp.actualitzaDarreraModificacio(data);
            } else if (opt == 1) {
                opt2 = JOptionPane.showConfirmDialog(null, "Estàs segur que no vols desar el document?", "Desar document", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
            }
        } while (opt == 1 && opt2 == 1);
        return opt;
    }

    /**
     * Pregunta a l'usuari quin nou titol vol posar al document i si es valid, el modifica.
     */
    protected void modificarTitol() {
        JPanel insertTitol = new JPanel();
        JTextField newT = new JTextField(titol.getText(), 20);
        insertTitol.add(new JLabel("Escriu el nou títol: "));
        insertTitol.add(newT);
        String[] opts = {"Sí", "Cancel·la"};
        int opt = JOptionPane.showOptionDialog(null, insertTitol, "Modificar títol",
                JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, opts, opts[0]);

        if (opt == 0 && !titol.getText().equals(newT.getText()) && !newT.getText().equals("") && newT.getText().length() <= 50 && !newT.getText().contains("_")) { // diria q no pot passar a no ser q tanquis
            String au = autor.getText();
            int opt2 = JOptionPane.showConfirmDialog(null, "El document tindrà el títol: " + newT.getText() +
                    " i l'autor: " + au + ", estàs d'acord?", "Modificar títol", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
            if (opt2 == 0) {
                boolean valid = cp.actualitzaTitol(newT.getText());
                if (valid) {
                    titol.setText(newT.getText());
                    JOptionPane.showMessageDialog(null, "S'ha modificat el títol a " + newT.getText() + ".");
                }
            }
        } else if(newT.getText().length() > 50 || newT.getText().contains("_")) {
            JOptionPane.showMessageDialog(null, "El títol no pot contenir \"_\" ni ser més llarg de 50 caràcters.");
        }
        else if (opt == 0 && newT.getText().equals(""))
            JOptionPane.showMessageDialog(null, "No es permeten camps en buit.");
        else if (opt == 0) JOptionPane.showMessageDialog(null, "Ja és el títol del document.");
    }

    /**
     * Pregunta a l'usuari quin nou autor vol posar al document i si és valid, el modifica.
     */
    protected void modificarAutor() {
        JPanel insertAutor = new JPanel();
        JTextField newA = new JTextField(autor.getText(), 20);
        insertAutor.add(new JLabel("Escriu el nou autor: "));
        insertAutor.add(newA);
        String[] opts = {"Sí", "Cancel·la"};
        int opt = JOptionPane.showOptionDialog(null, insertAutor, "Modificar autor",
                JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, opts, opts[0]);

        if (opt == 0 && !autor.getText().equals(newA.getText()) && !newA.getText().equals("") && newA.getText().length() <= 50 && !newA.getText().contains("_")) {
            String ti = titol.getText();
            int opt2 = JOptionPane.showConfirmDialog(null, "El document tindrà el títol: " + ti +
                    " i l'autor: " + newA.getText() + ", estàs d'acord?", "Modificar autor", JOptionPane.YES_NO_OPTION, JOptionPane.DEFAULT_OPTION);
            if (opt2 == 0) {
                boolean valid = cp.actualitzaAutor(newA.getText());
                if (valid) {
                    autor.setText(newA.getText());
                    JOptionPane.showMessageDialog(null, "S'ha modificat l'autor a " + newA.getText() + ".");
                }
            }
        } else if(newA.getText().length() > 50 || newA.getText().contains("_")) {
            JOptionPane.showMessageDialog(null, "L'autor no pot contenir \"_\" ni ser més llarg de 50 caràcters.");
        } else if (opt == 0 && newA.getText().equals(""))
            JOptionPane.showMessageDialog(null, "No es permeten camps en buit.");
        else if (opt == 0) JOptionPane.showMessageDialog(null, "Ja és l'autor del document.");
    }

    /**
     * Mostra un panell editable amb el contingut del document (a+t), amb el titol t i l’autor a a dalt del panell i un boto per desar el contingut del document, un altre per exportar el document i un tercer per tornar enrere, es a dir, anar a la ViewPrincipal.
     *
     * @param ctrlp Instancia del controlador de presentacio.
     * @param t     Titol del document.
     * @param a     Autor del document.
     * @param c     Contingut del document.
     */
    public ViewEditar(CtrlPresentacio ctrlp, String t, String a, String c) {
        setContentPane(panel1);
        setMinimumSize(new Dimension(400, 200));
        setTitle("Documenteitor");
        setSize(1000, 500);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        cp = ctrlp;
        titol.setText(t);
        autor.setText(a);
        textPane1.setText(c);
        cont = c;

        sortirButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contNou = textPane1.getText();
                if (!cont.equals(contNou)) {
                    desarAbansDeTancar(contNou, true);
                }
                cp.desarDocument();
                cp.tancarDocument();
                cp.mostraViewPrincipal();
                dispose();
            }
        });

        desarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String contNou = textPane1.getText();
                if (!cont.equals(contNou)) {
                    String data = LocalDate.now() + " " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                    cp.modificarContingut(contNou, data);
                    cont = contNou;
                    JOptionPane.showMessageDialog(null, "El document s'ha desat correctament.");
                    cp.actualitzaDarreraModificacio(data);
                }
            }
        });

        exportarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Selecciona una carpeta on guardar el document");
                chooser.setMultiSelectionEnabled(false);
                //chooser.setCurrentDirectory(new File(System.getProperty("user.dir") + "/data/dades"));
                int result = chooser.showOpenDialog(null);
                //int result = chooser.showSaveDialog(this);
                if (result == chooser.APPROVE_OPTION) {
                    //System.out.println(chooser.getSelectedFile().getAbsolutePath());
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
                    //JPanel insertTitol = new JPanel();
                    //insertTitol.add(new JLabel("Escriu el titol:"));
                    //insertTitol.add(newT);

                    String[] opts = {"Sí", "Cancel·la"};
                    int opt = JOptionPane.showOptionDialog(null, panelExportacio, "Exportació document",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.DEFAULT_OPTION, null, opts, opts[0]);

                    if (opt == 0 && newNom.getText() != null && !newNom.getText().equals("") && !((String) tipus.getSelectedItem()).equals("")) {
                        String path = chooser.getSelectedFile().getAbsolutePath();
                        String au = autor.getText(), ti = titol.getText();
                        String loc = path + "\\" + newNom.getText() + "." + (String) tipus.getSelectedItem(); //COMPROBAR Q SE HACE BIEN EL PATH
                        String data = LocalDate.now() + " " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS);
                        cont = textPane1.getText();
                        cp.modificarContingut(cont, data);
                        cp.desarDocument();
                        if (cp.exportaDocument(au, ti, loc)) {
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

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String contNou = textPane1.getText();
                int opt = 0;
                if (!cont.equals(contNou)) opt = desarAbansDeTancar(contNou, false);
                if (opt == 0 || opt == 1) {
                    cp.desarDocument();
                    cp.tancarAplicacio();
                }
            }
        });

        titol.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                modificarTitol();
            }
        });

        autor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                modificarAutor();
            }
        });
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
        panel1.setLayout(new FormLayout("left:min(m;206px):noGrow,left:29dlu:noGrow,fill:min(d;100px):grow,fill:61px:noGrow,fill:d:grow", "center:max(d;4px):noGrow,center:max(d;4px):noGrow,top:0dlu:noGrow,center:d:grow"));
        panel1.setBackground(new Color(-6641931));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new FormLayout("fill:d:grow", "center:max(d;4px):noGrow,top:4dlu:noGrow,center:max(d;4px):noGrow,top:4dlu:noGrow,center:d:grow,top:4dlu:noGrow,center:max(d;4px):noGrow"));
        panel2.setBackground(new Color(-9273651));
        CellConstraints cc = new CellConstraints();
        panel1.add(panel2, cc.xy(1, 4, CellConstraints.FILL, CellConstraints.FILL));
        desarButton = new JButton();
        desarButton.setText("Desa");
        panel2.add(desarButton, cc.xy(1, 1));
        exportarButton = new JButton();
        exportarButton.setText("Exporta");
        panel2.add(exportarButton, cc.xy(1, 3));
        sortirButton = new JButton();
        sortirButton.setText("      Enrere     ");
        panel2.add(sortirButton, cc.xy(1, 7));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, cc.xy(1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
        titol = new JLabel();
        titol.setBackground(new Color(-9273651));
        titol.setEnabled(true);
        Font titolFont = this.$$$getFont$$$("Verdana", -1, 14, titol.getFont());
        if (titolFont != null) titol.setFont(titolFont);
        titol.setText("");
        panel1.add(titol, cc.xy(3, 2));
        autor = new JLabel();
        autor.setBackground(new Color(-9273651));
        Font autorFont = this.$$$getFont$$$("Verdana", -1, 14, autor.getFont());
        if (autorFont != null) autor.setFont(autorFont);
        autor.setText("");
        panel1.add(autor, cc.xy(5, 2));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, cc.xyw(2, 4, 4, CellConstraints.FILL, CellConstraints.FILL));
        textPane1 = new JTextPane();
        textPane1.setText("");
        scrollPane1.setViewportView(textPane1);
        final JLabel label1 = new JLabel();
        Font label1Font = this.$$$getFont$$$("Verdana", -1, 14, label1.getFont());
        if (label1Font != null) label1.setFont(label1Font);
        label1.setText("Títol:");
        panel1.add(label1, cc.xy(2, 2, CellConstraints.LEFT, CellConstraints.BOTTOM));
        final JLabel label2 = new JLabel();
        Font label2Font = this.$$$getFont$$$("Verdana", -1, 14, label2.getFont());
        if (label2Font != null) label2.setFont(label2Font);
        label2.setText("Autor:");
        panel1.add(label2, cc.xy(4, 2, CellConstraints.DEFAULT, CellConstraints.BOTTOM));
        final JLabel label3 = new JLabel();
        Font label3Font = this.$$$getFont$$$(null, -1, 20, label3.getFont());
        if (label3Font != null) label3.setFont(label3Font);
        label3.setText("    ");
        panel1.add(label3, cc.xy(1, 1));
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

