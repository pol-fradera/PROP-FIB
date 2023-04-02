package domini.indexs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import transversal.Pair;
import domini.datatypes.Utility;


/**
 * Index per les cerques per expressio booleana
 * @author Eric Ryhr
 */
public class IndexExpBooleana implements Serializable{
    
    /**
     * Per cada paraula indica a quines frases apareix i la paraula que hi te a continuacio
     */
    private HashMap<String, List<Pair<Boolean, String>>> indexParaulaFrase;

    /**
     * Per cada frase (index) indica a quin document pertany 
     */
    private List<Pair<String, String>> indexFraseDocument;

    /**
     * Constructora de la classe IndexExpBooleana
     */
    public IndexExpBooleana() {
        indexParaulaFrase = new HashMap<>();
        indexFraseDocument = new ArrayList<Pair<String, String>>();
    }

    /** 
     * Metode per insertar documents a l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     */
    public void AfegirDoc(String autor, String titol, List<String> contingut) {
        Pair<String, String> autorTitol = new Pair<String, String>(autor, titol);

        for (String frase : contingut) {
            indexFraseDocument.add(autorTitol);
            int n = indexFraseDocument.size();
            addPosition();
            String[] paraules = Utility.ParseFrase(frase);

            for(int i = 0; i < paraules.length; i++) {
                String paraula = paraules[i];
                //Si la paraula no era al index afegim una altra entrada
                if(!indexParaulaFrase.containsKey(paraula)) {
                    List<Pair<Boolean, String>> infoParaula = new ArrayList<Pair<Boolean, String>>();
                    fillList(infoParaula);      //Omplim la llista amb false

                    Pair<Boolean, String> infoParaulaFrase = infoParaula.get(n-1);
                    infoParaulaFrase.x = true;          //L'ultim element sera true perque es la frase que acabem de insertar
                    if(i != paraules.length-1) infoParaulaFrase.y = paraules[i+1]; //Ens guardem la seguent paraula
                    infoParaula.set(n-1, infoParaulaFrase);
                    indexParaulaFrase.put(paraula, infoParaula);//Inserim la fila a l'index
                }
                //Si la paraula ja existeix al index simplement posem a true la casella corresponent i guardem la paraula seguent 
                else {
                    List<Pair<Boolean, String>> infoParaula = indexParaulaFrase.get(paraula);
                    Pair<Boolean, String> infoParaulaFrase = infoParaula.get(n-1);
                    infoParaulaFrase.x = true;          //L'ultim element sera true perque es la frase que acabem de insertar
                    if(i != paraules.length-1) infoParaulaFrase.y = paraules[i+1]; //Ens guardem la seguent paraula
                    infoParaula.set(n-1, infoParaulaFrase);
                    indexParaulaFrase.put(paraula, infoParaula);//Inserim la fila a l'index
                }
            }
        }
    }

    
    /** 
     * Metode per esborrar documents de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     */
    public void EsborrarDoc(String autor, String titol) {
        int startingIndexEliminar = -1;
        int numFrasesEliminar = 0;

        //Obtenim totes les frases que pertanyen al document que esborrarem
        //OPTIMITZACIO: Es fa un break quan ha trobat tots els autorTitol iguals seguits
        for (int i = 0; i < indexFraseDocument.size(); i++) {
            Pair<String, String> autorTitol = indexFraseDocument.get(i);
            if(autor.equals(autorTitol.x) && titol.equals(autorTitol.y)) {
                if(startingIndexEliminar == -1) startingIndexEliminar = i;
                numFrasesEliminar++;
            } else if (startingIndexEliminar != -1) break;
        }

        //Eliminem les frases (borrem sempre del mateix index perque cada cop que esborrem la llista es fa mes curta i la proxima posicio a eliminar cau al mateix lloc)
        for (int i = startingIndexEliminar; i < startingIndexEliminar+numFrasesEliminar; i++) {
            //int s = indexFraseDocument.size();
            indexFraseDocument.remove(startingIndexEliminar);

            //Del indexParaulaFrase eliminem les columnes de les frases eliminades
            for (List<Pair<Boolean, String>> infoParaula : indexParaulaFrase.values()) {
                infoParaula.remove(startingIndexEliminar);
            }
        }

        //Si alguna paraula no apareix a cap frase l'eliminem de l'index
        cleanIndex();
    }

    
    /** 
     * Metode per actualitzar el titol d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newTitol Nou titol del document
     */
    public void ActualitzarTitol(String autor, String titol, String newTitol) {
        Pair<String, String> oldAutorTitol = new Pair<String, String>(autor, titol);
        Pair<String, String> newAutorTitol = new Pair<String, String>(autor, newTitol);
        
        int startingIndexCanviar = -1;
        int numDocsCanviar = 0;
        for (int i = 0; i < indexFraseDocument.size(); i++) {
            if(indexFraseDocument.get(i).equals(oldAutorTitol)) {
                if(startingIndexCanviar == -1) startingIndexCanviar = i;
                numDocsCanviar++;
            } else if (startingIndexCanviar != -1) break;
        }

        for (int i = startingIndexCanviar; i < startingIndexCanviar+numDocsCanviar; i++) {
            indexFraseDocument.set(i, newAutorTitol);
        }
    }

    
    /** 
     * Metode per actualitzar l'autor d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newAutor Nou autor del document
     */
    public void ActualitzarAutor(String autor, String titol, String newAutor) {
        Pair<String, String> oldAutorTitol = new Pair<String, String>(autor, titol);
        Pair<String, String> newAutorTitol = new Pair<String, String>(newAutor, titol);
        
        int startingIndexCanviar = -1;
        int numDocsCanviar = 0;
        for (int i = 0; i < indexFraseDocument.size(); i++) {
            if(indexFraseDocument.get(i).equals(oldAutorTitol)) {
                if(startingIndexCanviar == -1) startingIndexCanviar = i;
                numDocsCanviar++;
            } else if (startingIndexCanviar != -1) break;
        }

        for (int i = startingIndexCanviar; i < startingIndexCanviar+numDocsCanviar; i++) {
            indexFraseDocument.set(i, newAutorTitol);
        }
    }

    
    /** 
     * Metode per actualitzar el contingut d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     */
    public void ActualitzarContingut(String autor, String titol, List<String> contingut) {
        EsborrarDoc(autor, titol);
        AfegirDoc(autor, titol, contingut);
    }

    
    /** 
     * Metode per obtenir els indexs de les frases on apareix una paraula
     * @param paraula Paraula a buscar
     * @return Index de les frases on apareix paraula
     */
    public Set<Integer> GetFrases(String paraula) {
        Set<Integer> frases = new HashSet<Integer>();
        List<Pair<Boolean, String>> infoParaula = indexParaulaFrase.get(paraula);

        //Si la paraula no es a cap frase returnem un set buit
        if(infoParaula == null) return frases;

        for(int i = 0; i < infoParaula.size(); i++) {
            if(infoParaula.get(i).x) frases.add(i);
        }

        return frases;
    }

    
    /**
     * Metode per obtenir el nombre de frases emmagatzemades
     * @return Nombre de frases emmagatzemades
     */
    public int GetNumFrases(){
        return indexFraseDocument.size();
    }

    
    /** 
     * Metode per obtenir els indexs de les frases on apareix una seqüencia de paraules
     * @param sequencia Seqüencia a buscar
     * @param candidats Index de les frases que contenen totes les paraules de la seqüencia (potser no en ordre)
     * @return Index de les frases on apareix seqüencia
     */
    public Set<Integer> GetSequencia(String sequencia, Set<Integer> candidats){
        Set<Integer> res = new HashSet<Integer>();
        String[] paraules = Utility.ParseFrase(sequencia);

        for (Integer c : candidats) {
            boolean valid = true;
            for (int i = 0; i < paraules.length; i++) {
                String paraula = paraules[i];
                if(i != paraules.length-1) {
                    String nextParaulaSeq = paraules[i+1];
                    List<Pair<Boolean, String>> infoParaula = indexParaulaFrase.get(paraula);
                    if(infoParaula == null) {
                        valid = false;
                        break;
                    }
                    Pair<Boolean, String> infoParaulaFrase = infoParaula.get(c);
                    String nextParaulaReal = infoParaulaFrase.y;
                    if(!nextParaulaSeq.equals(nextParaulaReal))  {
                        valid = false;
                        break;
                    }
                }
            }
            if(valid) res.add(c); //Si arribem aqui totes les paraules de la sequencia estan en ordre a la frase c
        }
        return res;
    }

    
    /** 
     * Metode per, donat un conjunt d'indexs, retornar els documents on apareixen
     * @param indexs Conjunt d'indexs a buscar
     * @return Claus dels documents obtinguts
     */
    public List<Pair<String, String>> GetDocuments(Set<Integer> indexs) {
        Set<Pair<String, String>> docs = new HashSet<Pair<String, String>>();

        for (int index : indexs) {
            docs.add(indexFraseDocument.get(index));
        }

        return new ArrayList<Pair<String, String>>(docs);
    }

    
    /** 
     * Metode per omplir les n primeres posicions d'una llista amb parelles de (False, null), on n es el nombre de frases emmagatzemades
     * @param infoWord Llista a tractar
     */
    private void fillList(List<Pair<Boolean, String>> infoWord) {
        for(int i = 0; i < indexFraseDocument.size(); i++) infoWord.add(new Pair<Boolean, String>(false, null));
    }
    
    /** 
     * Metode per afegir una posicio al final de cada fila de l'indexParaulaFrase per la frase que s'inserira a continuacio
     */
    private void addPosition() {
        for (List<Pair<Boolean, String>> infoParaula : indexParaulaFrase.values()) {
            infoParaula.add(new Pair<Boolean, String>(false, null));
        }
    }

    /** 
     * Metode per netejar l'index treient les paraules que no apareixen a cap frase
     */
    private void cleanIndex() {
        Set<String> toRemove = new HashSet<String>();

        //Busquem les paraules que no tenen entrades
        for (String paraula : indexParaulaFrase.keySet()) {
            List<Pair<Boolean, String>> infoParaula = indexParaulaFrase.get(paraula);
            boolean found = false;
            for (Pair<Boolean, String> b : infoParaula) {
                if(b.x) {
                    found = true;
                    break;
                }
            }
            if(!found) toRemove.add(paraula);
        }
        //Borrem les paraules que no tenen entrades
        for (String s : toRemove) indexParaulaFrase.remove(s);
    }
}
