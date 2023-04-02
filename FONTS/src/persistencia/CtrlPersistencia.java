package persistencia;
import excepcions.DeleteDocumentException;
import excepcions.FormatInvalid;
import excepcions.IDInvalid;
import transversal.Pair;

import java.io.IOException;
import java.util.List;

/**
 * Aquesta classe és l’encarregada d’interactuar amb les classes de persistencia.
 * @author Pol Fradera
 */
public class CtrlPersistencia {

    /**
     * Constructora del controlador de persistencia.
     */
    public CtrlPersistencia() {

    }

    /**
     * Metode per importar un document de disc i desar-lo a la carpeta del sistema.
     * @param path Ruta del document a importar a disc.
     * @return Array que conté l'autor, el titol i el contingut del document en aquest ordre.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     * @throws FormatInvalid L'extensio del fitxer importat no era correcta.
     * @throws IDInvalid El titol o l'autor del document son invalids
     */
    public String[] importaDocument(String path) throws IOException, FormatInvalid, IDInvalid {
        return GestorDocuments.ImportaDocument(path);
    }

    /**
     * Metode per exportar un document del sistema a disc a la localitzacio del path.
     * @param autor Autor del document a desar.
     * @param titol Titol del document a desar.
     * @param path Localitzacio on desar el document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     * @throws FormatInvalid L'extensio del fitxer importat no era correcta.
     */
    public void exportaDocument(String autor, String titol, String path) throws IOException, FormatInvalid {
        GestorDocuments.ExportaDocument(autor, titol, path);
    }

    /**
     * Metode per obtenir el contingut d'un document desat a la carpeta del sistema.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @return Contingut del document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public String getContingut(String autor, String titol) throws IOException {
        return GestorDocuments.GetContingut(autor, titol);
    }

    /**
     * Metode per desar el contingut d'un document a la carpeta del sistema.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param contingut Contingut del document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public void desaContingut(String autor, String titol, String contingut) throws IOException {
        GestorDocuments.DesaContingut(autor, titol, contingut);
    }

    /**
     * Metode per esborrar un document de la carpeta del sistema.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     * @throws DeleteDocumentException Hi ha hagut algun problema en esborrar el document.
     */
    public void esborrarDocument(String autor, String titol) throws IOException, DeleteDocumentException {
        GestorDocuments.EsborrarDoc(autor, titol);
    }

    /**
     * Metode per actualitzar l'autor d'un document de la carpeta del sistema.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param newAutor Nou autor del document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public void actualitzarAutor(String autor, String titol, String newAutor) throws IOException {
        GestorDocuments.ActualitzarAutor(autor, titol, newAutor);
    }

    /**
     * Metode per actualitzar el titol d'un document de la carpeta del sistema.
     * @param autor Autor del document.
     * @param titol Titol del document.
     * @param newTitol Nou titol del document.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public void actualitzarTitol(String autor, String titol, String newTitol) throws IOException {
        GestorDocuments.ActualitzarTitol(autor, titol, newTitol);
    }

    /**
     * Metode per importar els indexs de disc.
     * @return Import en forma d'array de bytes.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public byte[] importarIndexs() throws IOException {
        return GestorIndexs.ImportarIndexs();
    }

    /**
     * Metode per exportar els indexs a disc.
     * @param info indexs en forma d'array de bytes.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public void exportarIndexs(byte[] info) throws IOException {
        GestorIndexs.ExportarIndexs(info);
    }

    /**
     * Metode per carregar les expressions booleanes de disc.
     * @return Es retornen totes les expressions booleanes existents al disc (per cada una, el seu nom i l'expressio).
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public List<Pair<String, String>> carregarExpB() throws IOException {
        return GestorExpBooleanes.CarregarExpB();
    }

    /**
     * Metode per guardar les expressions booleanes a disc.
     * @param exps Totes les expressions booleanes que es volen guardar a disc.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public void guardarExpB(List<Pair<String, String>> exps) throws IOException {
        GestorExpBooleanes.GuardarExpB(exps);
    }
}