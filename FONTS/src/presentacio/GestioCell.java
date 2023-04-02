package presentacio;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * Classe extends de DefaultTableCellRenderer. La funció d’aquesta classe és redefinir les cel·les de la JTable que mostra els documents per tal de donar un color concret, un estil concret o inclús afegir la icona dels tres punts verticals.
 * @author Christian Rivero 
 */
public class GestioCell extends DefaultTableCellRenderer {

    /**
     * String on guardar el tipus de cell, pot ser: text, int, icon
     */
    private String tipus;
    /**
     * Font normal de les cel·les
     */
    private Font normal = new Font("Roboto Light", Font.PLAIN, 12);
    /**
     * Font en negreta de la cela
     */
    private Font bold = new Font("Roboto Light", Font.BOLD, 12);

    /**
     * Constructora bàsica de la classe
     */
    public GestioCell() {}

    /**
     * Constructora de la classe passant tipus
     * @param tipus String del tipus de la cela
     */
    public GestioCell(String tipus) {
        this.tipus = tipus;
    }

    /**
     * Métode override que retorna el component amb l'estil desitjat
     * @param table JTable a la qual pertany la cela
     * @param value Valor de la cela
     * @param selected Booleà que indica si a cela està seleccionada
     * @param focused Booleà que indica si a cela està sent focused
     * @param row Fila a la qual pertany la cela
     * @param column Columna a la qual pertany la cela
     * @return El component modificat
     */
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean selected, boolean focused, int row, int column) {
        Color colorFondo = new Color(192, 192, 192);

        if (selected) {
            this.setBackground(colorFondo);
        }
        else {
            this.setBackground(Color.white);
        }

        if (tipus.equals("text")) {
            this.setHorizontalAlignment(JLabel.LEFT);
            this.setText((String) value);
            this.setBackground((selected) ? colorFondo : Color.WHITE);
            this.setFont(normal);
            return this;
        }

        else if (tipus.equals("int")) {
            this.setHorizontalAlignment(JLabel.CENTER);
            this.setText((String) value);
            this.setForeground((selected) ? new Color(255, 255, 255) : new Color(33, 116, 34));
            this.setBackground((selected) ? colorFondo : Color.WHITE);
            this.setFont(bold);
            return this;
        }

        else if (tipus.equals("icon")) {
            JLabel label = new JLabel();
            label.setIcon(new ImageIcon(getClass().getResource("/presentacio/icons/moreOptions3.png")));
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
            return label;
        }

        return this;
    }
}



