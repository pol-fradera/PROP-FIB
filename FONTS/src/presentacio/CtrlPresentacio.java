package presentacio;

import domini.controladores.CtrlDomini;
import excepcions.*;
import transversal.Pair;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe que comunica la capa de domini amb les vistes
 * @author Christian Rivero
 */
public class CtrlPresentacio {
    /**
     * Instancia de la ViewPrincipal
     */
    private ViewPrincipal viewPrincipal;
    /**
     * Instancia de la ViewGestioExpBool
     */
    private ViewGestioExpBool viewExps;
    /**
     * Instancia de la ViewEditar
     */
    private ViewEditar viewEditar;
    /**
     * Instancia del controlador de domini
     */
    private CtrlDomini cd;

    /**
     * Creadora basica
     */
    public CtrlPresentacio() {}

    /**
     * Metode que mostra la vista principal, tant si és la primera vegada des que s’inicia el programa, com si no, actualitzada amb tots els documents
     */
    public void mostraViewPrincipal(){
        if(viewPrincipal == null) {
            viewPrincipal = new ViewPrincipal(this);
            cd = new CtrlDomini();
            List<Pair<Pair<String, String>, String>> docs = new ArrayList<>();
            try {
                docs = cd.init();
            }
            catch (IOException e) {
                popupException(e.toString(), "Error obrir programa");
                docs = null;
            }
            catch (ExpBoolNoValidaException e) { //no hauria de pasar mai
                popupException(e.toString(), "Error obrir programa");
                docs = null;
            }
            if(docs != null) viewPrincipal.initDocs(docs);
        }
        else viewPrincipal.setVisible(true);
    }

    /**
     * Metode que oculta la vista principal
     */
    public void ocultaViewPrincipal() {
        viewPrincipal.setVisible(false);
    }

    /**
     * Metode que mostra la vista de gestio d’expressions booleanes
     * @param documents JTable de la taula de documents de la ViewPrincipal, per tal de poder seleccionar les rows a l'obrir els documents des d'aquesta vista
     */
    public void mostraVistaGestioExpBool(JTable documents){
        viewExps = new ViewGestioExpBool(documents, this);
        List<Pair<String, String>> expsList = getAllExpressionsBooleanes();

        viewExps.initExp(expsList);
    }

    /**
     * Metode que mostra la vista de mostrar el contingut d’un document (autor+titol) que existeix al sistema
     * @param titol Títol del document a mostrar
     * @param autor Autor del document a mostrar
     */
    public void mostraViewMostrarCont(String titol, String autor){ //Conseguir el cont con el getContingut, y q este llame a esta y asi la main view como q no conoce las otras views?
        String cont = getContingut(autor, titol);
        ocultaViewPrincipal();
        new ViewMostrarCont(this, titol, autor, cont);
    }

    /**
     * Metode que mostra la vista d'editar el contingut d’un document (autor+titol) que existeix al sistema
     * @param titol Titol del document obert
     * @param autor Autor del document obert
     * @param cont Contingut del document obert
     */
    public void mostraViewEditar(String titol, String autor, String cont){
        if(viewEditar != null) viewEditar.dispose();
        viewEditar = new ViewEditar(this, titol, autor, cont);
    }

    /**
     * Metode per guardar els indexs i expressions booleanes a la capa de persistencia
     */
    public void tancarAplicacio() {
        try {
            //System.out.println("Tancant aplicacio...Guardant documents i expressions...");
            cd.tancar();
            System.exit(0); //dudas
        }
        catch(IOException e) {
            popupException(e.toString(), "Error tancar programa");
        }
    }

    /**
     * Metode que canvia el titol d'un document obert, si es pot, el canvia en les vistes
     * @param newT Nou titol del document obert
     * @return Indica si es pot modificar o no
     */
    public boolean actualitzaTitol(String newT) { //se tiene q comprobarantes si se puede crear
        String titol = viewPrincipal.getTitolDocObert();
        String autor = viewPrincipal.getAutorDocObert();
        boolean valid = modificarTitol(autor, titol, newT);

        if(valid) viewPrincipal.actualitzaTitol(newT); // no hace falta else con pop up error, ya se lanza en la otra

        return valid;
    }

    /**
     * Metode que canvia l'autor d'un document obert, si es pot, el canvia en les vistes
     * @param newA Nou autor del document obert
     * @return Indica si es pot modificar o no
     */
    public boolean actualitzaAutor(String newA) { //se tiene q comprobarantes si se puede crear
        String titol = viewPrincipal.getTitolDocObert();
        String autor = viewPrincipal.getAutorDocObert();
        boolean valid = modificarAutor(autor, titol, newA);

        if(valid) viewPrincipal.actualitzaAutor(newA); // no hace falta else con pop up error, ya se lanza en la otra

        return valid;
    }

    /**
     * Metode que canvia la darrera modificacio d'un document obert
     * @param date Nova darrera modificacio del document obert
     */
    public void actualitzaDarreraModificacio(String date) {
        viewPrincipal.actualitzaDarreraModificacio(date);
    }

    /*Crides a domini*/
    /**
     * Getter d'autors
     * @return Els autors existents al sistema
     */
    public List<String> getAutors() {
        return cd.getAutors();
    }

    /**
     * Metode per obtenir el contingut del document (autor+titol)
     * @param autor Autor del document
     * @param titol Titol del document
     * @return El contingut del document (autor+titol)
     */
    public String getContingut(String autor, String titol) {
        String cont = null;
        try {
            cont = cd.getContingut(autor, titol);
        }
        catch (IOException e) {
            popupException(e.toString(), "Error obtenir contingut");
        }
        return cont;
    }

    /**
     * Metode per obrir el document (autor+titol). titolAct, autorAct i contAct prenen el valor actual corresponent del document que s'ha obert
     * @param autor Autor del document
     * @param titol Titol del document
     * @return El contingut del document (autor+titol)
     */
    public String obrirDocument(String autor, String titol) {
        String cont = null;
        try {
            cont = cd.obrirDocument(autor, titol);
            mostraViewEditar(titol, autor, cont);
        }
        catch (IOException e) {
            popupException(e.toString(), "Error obrir document");
        }
        return cont;
    }

    /**
     * Metode per simular a la capa de domini que es tanca el document obert
     */
    public void tancarDocument() {
        cd.tancarDocument();
    }

    /**
     * Metode per crear un document amb autor autor, titol titol i contingut buit
     * @param autor Autor del document a crear
     * @param titol Titol del document a crear
     * @param data Darrera modificació, data actual
     * @return Indica si s'ha creat el document introduït
     */
    public boolean crearDocument(String autor, String titol, String data){
        boolean valid = true;
        try {
            cd.crearDocument(autor, titol, data);
        }
        catch (EDocumentException e){
            popupException(e.toString(), "Error crear document");
            valid = false;
        }
        catch(IOException e) {
            popupException(e.toString(), "Error crear document");
            valid = false;
        } catch (IDInvalid e) {
            popupException(e.toString(), "Error crear document");
            valid = false;
        }
        return valid;
    }

    /**
     * Metode per desar el contingut del document obert actualment a la capa de persistencia
     */
    public void desarDocument() {
        try {
            cd.desarDocument();
        }
        catch (IOException e){
            popupException(e.toString(), "Error desar document");
        }
    }

    /**
     * Metode per importar el document de la localitzacio path al sistema
     * @param path Localitzacio del document a importar
     * @param date Darrera modificació, data actual
     * @return L'autor i el titol del document importat. Retorna null si no s'ha pogut importar
     */
    public Pair<String, String> importaDocument(String path, String date) { //path = path+nom+.format
        Pair<String, String> docImp = new Pair<>();
        try {
            docImp = cd.importarDocument(path, date);
        }
        catch (EDocumentException e){
            popupException(e.toString(), "Error importar document");
            docImp = null;
        }
        catch(IOException e) {
            popupException("El document importat no indica el títol o l'autor correctament.", "Error importar document");
            docImp = null;
        }
        catch(FormatInvalid e) {
            popupException(e.toString(), "Error importar document");
            docImp = null;
        } catch (IDInvalid e) {
            popupException(e.toString(), "Error importar document");
            docImp = null;
        }
        return docImp;
    }

    /**
     * Metode per exportar el document (autor+titol) a la localitzacio path
     * @param autor Autor del document a exportar
     * @param titol Titol del document a exportar
     * @param path Localitzacio on es vol guardar el document a exportar
     * @return Indica si el document s'ha pogut exportar correctament o no
     */
    public boolean exportaDocument(String autor, String titol, String path) {
        boolean expOk = true;
        try {
            cd.exportarDocument(autor, titol, path);
        }
        catch (IOException e) {
            expOk = false;
            popupException(e.toString(), "Error exportar document");
        }
        catch (FormatInvalid e) { //ESTA CREO Q NO DEBERIA DE DAR ESTA EXCEPCION
            expOk = false;
            popupException(e.toString(), "Error exportar document");
        }
        return expOk;
    }

    /**
     * Metode per esborrar el document amb autor autor i titol titol
     * @param autor Autor del document a esborrar
     * @param titol Titol del document a esborrar
     * @return Indica si el document s'ha pogut esborrar correctament o no
     */
    public boolean esborrarDocument(String autor, String titol) {
        boolean esborrat = true;
        try {
            cd.esborrarDocument(autor, titol);
        }
        catch(DeleteDocumentException e) {
            esborrat = false;
            popupException(e.toString(), "Error esborrar document");
        }
        catch(IOException e) {
            esborrat = false;
            popupException(e.toString(), "Error esborrar document");
        }
        return esborrat;
    }

    /**
     * Metode per modificar el titol del document amb clau (autor+titol) per newT
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newT Nou titol que se li vol posar al document
     * @return Indica si s'ha pogut modificar el titol o no
     */
    public boolean modificarTitol(String autor, String titol, String newT) {
        boolean valid = true;
        try {
            cd.modificarTitol(autor, titol, newT);
        }
        catch (EDocumentException  e){
            valid = false;
            popupException(e.toString(), "Error modificar títol");
        }
        catch(IOException e) {
            valid = false;
            popupException(e.toString(), "Error modificar títol");
        }
        return valid;
    }

    /**
     * Metode per modificar el titol del document amb clau (autor+titol)
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newA Nou autor que se li vol posar al document
     * @return Indica si s'ha pogut modificar l'autor o no
     */
    public boolean modificarAutor(String autor, String titol, String newA) {
        boolean valid = true;
        try {
            cd.modificarAutor(autor, titol, newA);
        }
        catch (EDocumentException e){
            valid = false;
            popupException(e.toString(), "Error modificar autor");
        }
        catch(IOException e) {
            valid = false;
            popupException(e.toString(), "Error modificar autor");
        }
        return valid;
    }

    /**
     * Metode per modificar el contingut del document obert actualment
     * @param cont Nou contingut del document (autorAct+titolAct)
     * @param date Nova darrera modificacio
     */
    public void modificarContingut(String cont, String date) { //vieweditar
        cd.modificarContingut(cont, date);
    }

    /**
     * Metode que dona tots els titols de l'autor autor
     * @param autor Autor del que es volen tots els seus titols
     * @return Llista amb tots els titols de l'autor autor
     */
    public List<String> llistarTitolsdAutors(String autor) {
        return cd.llistarTitolsdAutors(autor);
    }

    /**
     * Metode que dona tots els autors amb prefix prefix
     * @param prefix Prefix dels autors a cercar
     * @return Llista amb tots els autors que tenen com a prefix prefix
     */
    public List<String> llistarAutorsPrefix(String prefix) {
        return cd.llistarAutorsPrefix(prefix);
    }

    /**
     * Metode que dona les com a molt K claus dels documents més semblants al document (autor+titol) amb l'estrategia estrategia
     * @param autor Autor del document
     * @param titol Titol del document
     * @param K Nombre de documents a llistar, 1 més petit o igual que K més petit o igual que nombreDocumentsTotal -1
     * @param estrategia Estrategia per fer la cerca
     * @return Llista amb com a molt K claus dels documents més semblants al document (autor+titol) amb l'estrategia estrategia.
     */
    public List<Pair<String, String>> llistarKDocumentsS(String autor, String titol, int K, boolean estrategia) {
        return cd.llistarKDocumentsS(autor, titol, K, estrategia);
    }

    /**
     * Opcional de l'enunciat: Metode que dona les com a molt K claus dels documents més rellevants segons les paraules paraules amb l'estrategia estrategia
     * @param paraules Paraules rellevants a cercar
     * @param K Nombre de documents a llistar, 1 més petit o igual que K més petit o igual que nombreDocumentsTotal
     * @param estrategia Estrategia per fer la cerca
     * @return Llista amb com a molt K claus dels documents més rellevants segons les paraules paraules amb l'estrategia estrategia
     */
    public List<Pair<String, String>> cercarPerRellevancia(String paraules, int K, boolean estrategia) {
        return cd.cercarPerRellevancia(paraules, K, estrategia);
    }

    /**
     * Getter que dona totes les expressions booleanes (nom + exp)
     * @return Llista de pairs amb totes les expressions booleanes (nom+exp)
     */
    public List<Pair<String, String>> getAllExpressionsBooleanes() {
        return cd.getAllExpressionsBooleanes();
    }

    /**
     * Metode per crear una expressio booleana amb nom nom i expressio exp
     * @param nom Nom de l'expressio booleana a crear
     * @param exp Expressio de l'expressio booleana a crear
     * @return Indica si l'expressio booleana ha sigut creada o no
     */
    public boolean creaExpressioBooleana(String nom, String exp) {
        boolean valida = true;
        try {
            cd.setExpressioBooleana(nom, exp);
        }
        catch (EExpBoolException e) {
            valida = false;
            popupException(e.toString(), "Error crear expressió");
        }
        catch(ExpBoolNoValidaException e) {
            valida = false;
            popupException(e.toString(), "Error crear expressió");
        }
        return valida;
    }

    /**
     * Metode que dona les claus dels documents que cumpleixen l'expressio booleana exp
     * @param exp Expressio booleana per fer la cerca
     * @return Llista amb les claus dels documents que compleixen l'expressio booleana exp, la llista = null si ha succeït algun error
     */
    public List<Pair<String, String>> cercarExpressioBooleana(String exp) {
        List<Pair<String, String>> docs = new ArrayList<>();
        try {
            docs = cd.cercarExpressioBooleana(exp);
        }
        catch(ExpBoolNoValidaException e) {
            docs = null;
            popupException(e.toString(), "Error cerca per expressio");
        }
        return docs;
    }

    /**
     * Metode que dona les claus dels documents que cumpleixen l'expressio booleana amb nom nom
     * @param nom Nom de l'expressio booleana per fer la cerca
     * @return Llista amb les claus dels documents que compleixen l'expressio booleana amb nom nom
     */
    public List<Pair<String, String>> cercarExpressioBooleanaNom(String nom) {
        List<Pair<String, String>> docs = cd.cercarExpressioBooleanaNom(nom);
        return docs;
    }

    /**
     * Metode per modificar l'expressio de l'expressio booleana amb nom nom a nExp
     * @param nom Nom de l'expressio booleana a modificar
     * @param nExp Nova expressio per a l'expressio booleana
     * @return Indica si s'ha pogut fer la modificacio o no
     */
    public boolean modExpressioBooleana(String nom, String nExp) {
        boolean valida = true;
        try {
            cd.modExpressioBooleana(nom, nExp);
        }
        catch(ExpBoolNoValidaException e) {
            valida = false;
            popupException(e.toString(), "Error modificar expressió");
        }
        return valida;
    }

    /**
     * Metode per esborrar l'expressio booleana amb nom nom
     * @param nom Nom de l'expressio booleana a esborrar
     */
    public void deleteExpressioBooleana(String nom) {
        cd.deleteExpressioBooleana(nom);
    }

    /**
     * Metode main de l'aplicacio, comença mostrant la viewPincipal
     * @param args Arguments del programa
     */
    public static void main(String[] args) {
        CtrlPresentacio cp = new CtrlPresentacio();
        cp.mostraViewPrincipal();
    }

    /**
     * Metode privat per mostrar un popup amb l'error que ha causat l'excepcio
     * @param message Missatge del popup, és el motiu de l'excepcio
     * @param title Titol del popup, accio que l'ha causat
     */
    private void popupException(String message, String title) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.ERROR_MESSAGE);
    }
}
