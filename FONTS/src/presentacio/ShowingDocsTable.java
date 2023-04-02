package presentacio;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe extends de JPanel. Com en diversos llocs de l’aplicació es mostren autors, títols, documents, i sempre ho fem amb un JPanel amb un JTable (amb un comportament concret) i un JScrollPane, hem fet una classe per evitar programar tantes vegades el mateix i encapsular aquest tipus particular de JPanel.
 * @author Christian Rivero
 */
public class ShowingDocsTable extends JPanel {
    /**
     * Int que indica quina ha sigut l’anterior columna del header del JTable pulsada.
     * Pot ser 0 o 1 (columnes que permeten l’ordenació) i si es prem dues vegades la mateixa columna seguida, es posa a -1
     */
    private int anteriorColumn;

    /**
     * Creadora de la classe.
     * @param tm DefaultTableModel de la taula documents, necessaria per fer la JTable
     * @param documents JTable on es mostren els documents en la view principal, la passem perquè s'ha de seleccionar la fila pertinent en cas que s'obri un dels documents que mostra aquesta taula
     * @param cp Instància del CtrlPresentacio
     * @param mostrarDoc Indica si s'ha de mostrar la vista d'edició de documents si es fa doble clic en algunes de les seves files. Com a qeusta classe s'utilitza en diferents llocs, hi ha vegades que no ens interessa que es mostrin els documents
     * @param vistaCaller Instància de la vista que crea la classe (pot ser o la view principal o la view de gestor de les expressions booleanes), necessària per ocultar-la en cas que s'obri un dels documents
     */
    public ShowingDocsTable(DefaultTableModel tm, JTable documents, CtrlPresentacio cp, boolean mostrarDoc, JFrame vistaCaller) {
        anteriorColumn = -1;
        JTable table = new JTable(tm);
        table.getTableHeader().setReorderingAllowed(false);
        table.setAutoCreateRowSorter(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //las tablas que creamos con esta clase nunca tienen más de 2 columnas
        if (table.getColumnCount() >= 1) {
            table.getColumnModel().getColumn(0).setCellRenderer(new GestioCell("text"));
            table.getColumnModel().getColumn(0).setPreferredWidth(100);
        }
        if (table.getColumnCount() >= 2) {
            table.getColumnModel().getColumn(1).setCellRenderer(new GestioCell("text"));
            table.getColumnModel().getColumn(1).setPreferredWidth(100);
        }
        table.setRowHeight(25);

        if (table.getColumnCount() > 1) {
            TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
            table.setRowSorter(sorter);
            List<RowSorter.SortKey> sortKeys = new ArrayList<>();

            JTableHeader header = table.getTableHeader();
            header.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Point point = e.getPoint();
                    int column = table.columnAtPoint(point);
                    sortKeys.clear();
                    if (anteriorColumn != 1 && column == 1) {
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        anteriorColumn = column;
                    } else if (anteriorColumn != -0 && column == 0) {
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
                        anteriorColumn = column;
                    } else if (anteriorColumn == 1 && column == 1) {
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING)); //ASCENDING
                        anteriorColumn = -1;
                    } else if (anteriorColumn == 0 && column == 0) {
                        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
                        sortKeys.add(new RowSorter.SortKey(1, SortOrder.DESCENDING)); //ASCENDING
                        anteriorColumn = -1;
                    }
                    sorter.setSortKeys(sortKeys);
                }
            });
        }

        if(mostrarDoc) {
            table.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 1) { //solo se selecciona en la tabla
                        String titol = (String)table.getValueAt(table.getSelectedRow(), 0);
                        String autor = (String)table.getValueAt(table.getSelectedRow(), 1);

                        //se selecciona en la tabla documentos por si le quiere modificar el titulo o autor, saber cual es
                        int row = -1;
                        for (int i = 0; i < documents.getRowCount() && row == -1; ++i) {
                            String titolDocs = (String) documents.getValueAt(i, 0);
                            String autorDocs = (String) documents.getValueAt(i, 1);
                            if(titol.equals(titolDocs) && autor.equals(autorDocs)) row = i;
                        }
                        documents.clearSelection();
                        documents.addRowSelectionInterval(row, row);
                    }
                    if (e.getClickCount() == 2) { //se selecciona en la tabla y se abre
                        String titol = (String)table.getValueAt(table.getSelectedRow(), 0);
                        String autor = (String)table.getValueAt(table.getSelectedRow(), 1);

                        //se selecciona en la tabla documentos por si le quiere modificar el titulo o autor, saber cual es
                        int row = -1;
                        for (int i = 0; i < documents.getRowCount() && row == -1; ++i) {
                            String titolDocs = (String) documents.getValueAt(i, 0);
                            String autorDocs = (String) documents.getValueAt(i, 1);
                            if(titol.equals(titolDocs) && autor.equals(autorDocs)) row = i;
                        }
                        documents.clearSelection();
                        documents.addRowSelectionInterval(row, row);

                        //abre el documento
                        cp.obrirDocument(autor, titol);
                        vistaCaller.setVisible(false);
                    }
                }
            });
        }
        setLayout(new BorderLayout());
        JScrollPane tableAutsScroll = new JScrollPane(table);
        add(tableAutsScroll);
    }
}
