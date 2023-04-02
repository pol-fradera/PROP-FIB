package domini.controladores;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Set;

import transversal.Pair;
import domini.datatypes.Trie;
import domini.indexs.*;

/**
 * Controlador de domini pels indexs
 * @author Eric Ryhr
 */
public class CtrlIndex {

    /**
     * Index per desar les claus dels documents
     */
    private Trie indexDocuments;

    /**
     * Index per assistir les queries per expressio booleana
     */
    private IndexExpBooleana indexExpBooleana;

    /**
     * Index per realitzar comparacions entre documents
     */
    private IndexParaulaTFIDF indexParaulaTFIDF;
    
    /**
     * Constructora de la classe CtrlIndex. Inicialitza els indexs
     */
    public CtrlIndex() {
        indexDocuments = new Trie();
        indexExpBooleana = new IndexExpBooleana();
        indexParaulaTFIDF = new IndexParaulaTFIDF();
    }

    
    /** 
     * Metode per insertar documents als indexs
     * @param autor Autor del document
     * @param titol Titol del document
     * @param data Data de modificacio del document
     * @param contingut Contingut del document
     */
    public void AfegirDoc(String autor, String titol, String data, List<String> contingut) {
        indexDocuments.AfegirDoc(autor, titol, data);
        indexExpBooleana.AfegirDoc(autor, titol, contingut);
        indexParaulaTFIDF.AfegirDoc(autor, titol, contingut);
    }

    
    /** 
     * Metode per esborrar documents dels indexs
     * @param autor Autor del document
     * @param titol Titol del document
     */
    public void EsborrarDoc(String autor, String titol) {
        indexDocuments.EsborrarDoc(autor, titol);
        indexExpBooleana.EsborrarDoc(autor, titol);
        indexParaulaTFIDF.EsborrarDoc(autor, titol);
    }

    
    /** 
     * Metode per comprovar si existeixen documents als indexs
     * @param autor Autor del document a buscar
     * @param titol Titol del document a buscar
     * @return True si el document es troba als indexs, False si no
     */
    public boolean FindDoc(String autor, String titol) {
        return indexDocuments.FindDoc(autor, titol);
    }

    /** 
     * Metode per escriure la data d'un document
     * @param autor Autor del document
     * @param titol Titol del document
     * @param data Data del document
     */
    public void SetData(String autor, String titol, String data) {
        indexDocuments.SetData(autor, titol, data);
    }

    
    /** 
     * Metode per actualitzar el titol d'un document dels indexs
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newTitol Nou titol del document
     */
    public void ActualitzarTitol(String autor, String titol, String newTitol) {
        indexDocuments.ActualitzarTitol(autor, titol, newTitol);
        indexExpBooleana.ActualitzarTitol(autor, titol, newTitol);
        indexParaulaTFIDF.ActualitzarTitol(autor, titol, newTitol);
    }

    
    /** 
     * Metode per actualitzar l'autor d'un document dels indexs
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newAutor Nou autor del document
     */
    public void ActualitzarAutor(String autor, String titol, String newAutor) {
        indexDocuments.ActualitzarAutor(autor, titol, newAutor);
        indexExpBooleana.ActualitzarAutor(autor, titol, newAutor);
        indexParaulaTFIDF.ActualitzarAutor(autor, titol, newAutor);
    }

    
    /** 
     * Metode per actualitzar el contingut d'un document dels indexs
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     */
    //PRE: EXISTEIX
    public void ActualitzarContingut(String autor, String titol, List<String> contingut) {
        indexExpBooleana.ActualitzarContingut(autor, titol, contingut);
        indexParaulaTFIDF.ActualitzarContingut(autor, titol, contingut);
    }

    
    /** 
     * Metode per obtenir totes les claus dels documents dels indexs
     * @return Set amb totes les tries ((autor, titol), data) del Trie
     */
    public Set<Pair<Pair<String, String>, String>> GetKeys() {
        return indexDocuments.GetKeys();
    }

    
    /** 
     * Metode per obtenir tots els titols d'un autor
     * @param autor Autor del que s'obtenen els titols
     * @return Set amb els titols de l'autor. Buit si no existeix
     */
    public Set<String> GetTitolsAutor(String autor) {
        return indexDocuments.GetTitolsAutor(autor);
    }

    
    /** 
     * Metode per obtenir els autors que comencen per prefix
     * @param prefix Prefix pel que han de començar els noms dels autors
     * @return Set amb els autors obtinguts
     */
    public Set<String> GetAutorsPrefix(String prefix) {
        return indexDocuments.SearchWordsPrefix(prefix);
    }

    
    /** 
     * Metode per obtenir els documents mes semblants al passat per parametre
     * @param autor Autor del document a comparar
     * @param titol Titol del document a comparar
     * @param K Nombre de respostes a retornar
     * @param estrategia True si la comparacio es fa amb TF, False si amb TF-IDF
     * @return Llista dels documents mes semblants ordenats amb el mes semblant primer
     */
    public List<Pair<String, String>> GetKDocsSimilarS (String autor, String titol, int K, boolean estrategia) {
        return indexParaulaTFIDF.GetKDocsSimilarS(new Pair<String, String>(autor, titol), K, estrategia);
    }

    
    /** 
     * Metode per obtenir els documents mes semblants a l'entrada passada per parametre
     * @param entrada Entrada a comparar
     * @param K Nombre de respostes a retornar
     * @param estrategia True si la comparacio es fa amb TF, False si amb TF-IDF
     * @return Llista dels documents mes semblants ordenats amb el mes semblant primer
     */
    public List<Pair<String, String>> CercaPerRellevancia(String entrada, int K, boolean estrategia) {
        return indexParaulaTFIDF.CercaPerRellevancia(entrada, K, estrategia);
    }

    
    /** 
     * Metode per obtenir els indexs de les frases on apareix una paraula
     * @param paraula Paraula a buscar
     * @return Index de les frases on apareix paraula
     */
    //Retorna els indexs de les frases que contenen paraula
    public Set<Integer> GetFrases(String paraula) {
        return indexExpBooleana.GetFrases(paraula);
    }

    
    /** 
     * Metode per obtenir el nombre de frases emmagatzemades
     * @return Nombre de frases emmagatzemades
     */
    public int GetNumFrases() {
        return indexExpBooleana.GetNumFrases();
    }

    
    /** 
     * Metode per obtenir els indexs de les frases on apareix una seqüencia de paraules
     * @param sequencia Seqüencia a buscar
     * @param candidats Index de les frases que contenen totes les paraules de la seqüencia (potser no en ordre)
     * @return Index de les frases on apareix seqüencia
     */
    public Set<Integer> GetSequencia(String sequencia, Set<Integer> candidats) {
        return indexExpBooleana.GetSequencia(sequencia, candidats);
    }

    
    /** 
     * Metode per, donat un conjunt d'indexs, retornar els documents on apareixen
     * @param indexs Conjunt d'indexs a buscar
     * @return Claus dels documents obtinguts
     */
    public List<Pair<String, String>> GetDocuments(Set<Integer> indexs) {
        return indexExpBooleana.GetDocuments(indexs);
    }

    
    /** 
     * Metode per importar els indexs de disc i sobreescriure els actuals
     * @param info Indexs en forma d'array de bytes
     * @throws IOException Hi ha hagut algun problema amb els streams
     */
    public void ImportarIndexs(byte[] info) throws IOException {
        try {
            //Si no existia el fitxer (programa obert per primer cop) no es fa res
            if(info == null) return;

            ByteArrayInputStream bais = new ByteArrayInputStream(info);
            ObjectInputStream ois = new ObjectInputStream(bais);
            Object[] indexs = (Object[]) ois.readObject();
            indexDocuments = (Trie) indexs[0];
            indexExpBooleana = (IndexExpBooleana) indexs[1];
            indexParaulaTFIDF = (IndexParaulaTFIDF) indexs[2];
            ois.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    
    /** 
     * Metode per exportar els indexs a capa persistencia
     * @return Els 3 indexs serialitzats en forma de byte array
     * @throws IOException Hi ha hagut algun problema amb els streams
     */
    public byte[] ExportarIndexs() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        Object[] indexs = new Object[] {indexDocuments, indexExpBooleana, indexParaulaTFIDF};
        oos.writeObject(indexs);
        oos.close();
        return baos.toByteArray();
    }

}