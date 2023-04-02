package domini.controladores;

import persistencia.CtrlPersistencia;
import transversal.*;
import excepcions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static domini.datatypes.Utility.converteix_a_frases;

/**
 * Aquesta classe es l’encarregada d’interactuar amb les altres capes. Tambe es l’encarregada d’instanciar la resta de controladors de la capa de domini i el de persistencia.
 * @author Marc Roman
 */
public class CtrlDomini {
    /**
     * Controlador d'index.
     */
    private CtrlIndex ci;
    /**
     * Controlador d'expressio booleana.
     */
    private CtrlExpressioBooleana ce;
    /**
     * Controlador de persistencia.
     */
    private CtrlPersistencia cp;
    /**
     * Parametres del document obert actual. Si no hi ha cap document obert tots son igual a "".
     */
    private String titolAct, autorAct, contAct;

    /**
     * Constructora del controlador de domini, que inicialitza els controladors d'index, expressio booleana i persistencia.
     */
    public CtrlDomini() {
        ci = new CtrlIndex();
        ce = new CtrlExpressioBooleana();
        cp = new CtrlPersistencia();
    }

    /**
     * Metode per inicialitzar els indexs i expressions booleanes a la capa de domini.
     * @return Es retorna la llista de tots els documents (autor+titol) existents i les seves dates de darrera modificacio.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     * @throws ExpBoolNoValidaException Alguna expressio booleana no es valida.
     */
    public List<Pair<Pair<String, String>, String>> init() throws IOException, ExpBoolNoValidaException {
        ci.ImportarIndexs(cp.importarIndexs());
        List<Pair<String, String>> ebs = cp.carregarExpB();
        for (Pair<String, String> eb : ebs) ce.setExpressioBooleana(eb.x, eb.y);
        return getTitolsAutorsData();
    }

    /**
     * Metode per guardar els indexs i expressions booleanes a la capa de persistencia.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public void tancar() throws IOException {
        cp.exportarIndexs(ci.ExportarIndexs());
        cp.guardarExpB(ce.getAll());
    }

    /**
     * Metode per importar el document de la localitzacio loc al sistema.
     * @param loc Localitzacio del document a importar.
     * @param data Data de l'instant on s'ha demanat importar.
     * @return Es retorna l'autor i el titol del document importat.
     * @throws EDocumentException El document ja existeix al sistema.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     * @throws FormatInvalid El format no es valid.
     * @throws IDInvalid El titol o l'autor del document son invalids
     */
    public Pair<String,String> importarDocument(String loc, String data) throws EDocumentException, IOException, FormatInvalid, IDInvalid {
            String[] doc = cp.importaDocument(loc);
            if (ci.FindDoc(doc[0], doc[1])) throw new EDocumentException();
            ci.AfegirDoc(doc[0], doc[1], data, converteix_a_frases(doc[2]));
        return new Pair<>(doc[0], doc[1]);
    }

    /**
     * Metode per exportar el document (autor+titol) a la localitzacio loc.
     * @param autor Autor del document a exportar.
     * @param titol Titol del document a exportar.
     * @param loc Localitzacio on es vol guardar el document a exportar.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     * @throws FormatInvalid El format no es valid.
     */
    public void exportarDocument(String autor, String titol, String loc) throws IOException, FormatInvalid {
        cp.exportaDocument(autor, titol, loc);
    }

    /**
     * Getter d'autors.
     * @return Es retornen tots els autors existents al sistema.
     */
    public List<String> getAutors() {
        return new ArrayList<>(ci.GetAutorsPrefix(""));
    }

    /**
     * Getter de documents.
     * @return Es retornen ots els identificadors de documents (autor+titol) existents al sistema i les seves dates de darrera modificacio.
     */
    public List<Pair<Pair<String, String>,String>> getTitolsAutorsData() {
        return new ArrayList<>(ci.GetKeys());
    }

    /**
     * Metode per obtenir el contingut del document (autor+titol).
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @return Es retorna el contingut del document (autor+titol).
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public String getContingut(String autor, String titol) throws IOException {
        return cp.getContingut(autor, titol);
    }

    /**
     * Metode per obrir el document (autor+titol). titolAct, autorAct i contAct prenen el valor actual corresponent del document que s'ha obert.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @return Es retorna el contingut del document (autor+titol).
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public String obrirDocument(String autor, String titol) throws IOException {
        titolAct = titol;
        autorAct = autor;
        contAct = cp.getContingut(autor, titol);
        return contAct;
    }

    /**
     * Metode per crear un document amb autor autor, titol titol i contingut en blanc.
     * @param autor Autor del document a crear.
     * @param titol Titol del document a crear.
     * @param data Data de l'instant on s'ha demanat crear el document.
     * @throws EDocumentException El document ja existeix al sistema.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     * @throws IDInvalid El titol o l'autor del document son invalids
     */
    public void crearDocument(String autor, String titol, String data) throws EDocumentException, IOException, IDInvalid {
        if (ci.FindDoc(autor, titol)) throw new EDocumentException();
        ci.AfegirDoc(autor, titol, data, new ArrayList<String>());
        cp.desaContingut(autor, titol, "");
    }

    /**
     * Metode per esborrar el document amb autor autor i titol titol.
     * @param autor Autor del document a esborrar.
     * @param titol Titol del document a esborrar.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     * @throws DeleteDocumentException Hi ha hagut algun problema intentant esborrar el document de disc.
     */
    public void esborrarDocument(String autor, String titol) throws IOException, DeleteDocumentException {
        ci.EsborrarDoc(autor, titol);
        cp.esborrarDocument(autor, titol);
    }

    /**
     * Metode per modificar el titol del document amb clau (autor+titol).
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param newT Nou titol que se li vol posar al document.
     * @throws EDocumentException El document (autor+newT) ja existeix al sistema.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public void modificarTitol(String autor, String titol, String newT) throws EDocumentException, IOException {
        if (ci.FindDoc(autor, newT)) throw new EDocumentException();
        ci.ActualitzarTitol(autor, titol, newT);
        cp.actualitzarTitol(autor, titol, newT);
    }

    /**
     * Metode per modificar l'autor del document amb clau (autor+titol).
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param newA Nou autor que se li vol posar al document.
     * @throws EDocumentException El document (newA+titol) ja existeix al sistema.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public void modificarAutor(String autor, String titol, String newA) throws EDocumentException, IOException {
        if (ci.FindDoc(newA, titol)) throw new EDocumentException();
        ci.ActualitzarAutor(autor, titol, newA);
        cp.actualitzarAutor(autor, titol, newA);
    }

    /**
     * Metode per modificar el contingut (contAct) del document obert actualment (autorAct+titolAct) i actualitzar la seva data.
     * @param cont Nou contingut del document (autorAct+titolAct).
     * @param data Data de l'instant on s'ha desat el contingut.
     */
    public void modificarContingut(String cont, String data) {
        contAct = cont;
        ci.SetData(autorAct, titolAct, data);
        ci.ActualitzarContingut(autorAct, titolAct, converteix_a_frases(cont));
    }

    /**
     * Metode per desar el contingut (contAct) del document obert actualment (autorAct+titolAct) a la capa de persistencia.
     * @throws IOException Hi ha hagut algun problema en accedir a disc.
     */
    public void desarDocument() throws IOException {
        cp.desaContingut(autorAct, titolAct, contAct);
    }

    /**
     * Metode per simular a capa de domini que es tanca el document obert posant autorAct, titolAct i contAct amb valor "".
     */
    public void tancarDocument() {
        autorAct = "";
        titolAct = "";
        contAct = "";
    }

    /**
     * Metode que dona tots els titols de l'autor autor.
     * @param autor Autor del que es volen tots els seus titols.
     * @return Es retorna una llista amb tots els titols de l'autor autor.
     */
    public List<String> llistarTitolsdAutors(String autor) {
        return new ArrayList<>(ci.GetTitolsAutor(autor));
    }

    /**
     * Metode que dona tots els autors amb prefix prefix.
     * @param prefix Prefix dels autors a cercar.
     * @return Es retorna una llista amb tots els autors que tenen com a prefix prefix.
     */
    public List<String> llistarAutorsPrefix(String prefix) {
        return new ArrayList<>(ci.GetAutorsPrefix(prefix));
    }

    /**
     * Metode que dona les com a molt K claus dels documents mes semblants al document (autor+titol) amb l'estrategia estrategia.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param K Nombre de documents a llistar.
     * @param estrategia Estrategia per fer la cerca.
     * @return Es retorna una llista amb com a molt K claus dels documents mes semblants al document (autor+titol) amb l'estrategia estrategia.
     */
    public List<Pair<String, String>> llistarKDocumentsS(String autor, String titol, int K, boolean estrategia) {
        return ci.GetKDocsSimilarS(autor, titol, K, estrategia);
    }

    /**
     * Metode que dona les claus dels documents que compleixen l'expressio booleana exp.
     * @param exp Expressio booleana per fer la cerca.
     * @return Es retorna una llista amb les claus dels documents que compleixen l'expressio booleana exp.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    public List<Pair<String, String>> cercarExpressioBooleana(String exp) throws ExpBoolNoValidaException {
        return ce.cercarExpressioBooleana(exp, ci);
    }

    /**
     * Metode que dona les claus dels documents que cumpleixen l'expressio booleana amb nom nom.
     * @param nom Nom de l'expressio booleana per fer la cerca.
     * @return Es retorna una llista amb les claus dels documents que compleixen l'expressio booleana amb nom nom.
     */
    public List<Pair<String, String>> cercarExpressioBooleanaNom(String nom) {
        return ce.cercarExpressioBooleanaExistent(nom, ci);
    }

    /**
     * Opcional: Metode que dona les com a molt K claus dels documents mes rellevants segons les paraules paraules amb l'estrategia estrategia.
     * @param paraules Paraules rellevants a cercar.
     * @param K Nombre de documents a llistar.
     * @param estrategia Estrategia per fer la cerca.
     * @return Es retorna una llista amb com a molt K claus dels documents mes rellevants segons les paraules paraules amb l'estrategia estrategia.
     */
    public List<Pair<String, String>> cercarPerRellevancia(String paraules, int K, boolean estrategia) {
        return ci.CercaPerRellevancia(paraules, K, estrategia);
    }

    /**
     * Getter que dona totes les expressions booleanes (nom + exp).
     * @return Es retorna una llista amb totes les expressions booleanes (nom+exp).
     */
    public List<Pair<String, String>> getAllExpressionsBooleanes() {
        return ce.getAll();
    }

    /**
     * Metode per crear una expressio booleana amb nom nom i expressio exp.
     * @param nom Nom de l'expressio booleana a crear.
     * @param exp Expressio de l'expressio booleana a crear.
     * @throws EExpBoolException L’expressio booleana amb nom nom existeix.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    public void setExpressioBooleana(String nom, String exp) throws EExpBoolException, ExpBoolNoValidaException {
        if (ce.existsExpressioBooleana(nom)) throw new EExpBoolException();
        ce.setExpressioBooleana(nom, exp);
    }

    /**
     * Metode per modificar l'expressio de l'expressio booleana amb nom nom a nExp.
     * @param nom Nom de l'expressio booleana a modificar.
     * @param nExp Nova expressio per a l'expressio booleana.
     * @throws ExpBoolNoValidaException nExp no es valida.
     */
    public void modExpressioBooleana(String nom, String nExp) throws ExpBoolNoValidaException {
        ce.setExpressioBooleana(nom, nExp);
    }

    /**
     * Metode per esborrar l'expressio booleana amb nom nom.
     * @param nom Nom de l'expressio booleana a esborrar.
     */
    public void deleteExpressioBooleana(String nom) {
        ce.deleteExpressioBooleana(nom);
    }
}