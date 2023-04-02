package presentacio;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Vista per mostrar el contingut del document seleccionat a la ViewPrincipal.
 * @author Marc Roman
 */
public class ViewMostrarCont extends ViewEditar {
    /**
     * Mostra la vista ViewEditar sense les opcions de desar, exportar i modificar el contingut del document, es a dir, mostra el contingut del document.
     * @param cp Instancia del controlador de presentacio.
     * @param t Titol del document.
     * @param a Autor del document.
     * @param cont Contingut del document.
     */
    public ViewMostrarCont(CtrlPresentacio cp, String t, String a, String cont) {
        super(cp, t, a, cont);
        textPane1.setEditable(false);
        desarButton.setVisible(false);
        exportarButton.setVisible(false);
    }

    protected void modificarTitol() {
    }


    protected void modificarAutor() {
    }
}