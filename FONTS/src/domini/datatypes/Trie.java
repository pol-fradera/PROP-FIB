package domini.datatypes;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import transversal.Pair;

/**
 * Estructura de dades per emmagatzemar documents
 * Implementacio basica del trie extreta de https://www.baeldung.com/trie-java
 * @author Eric Ryhr
 */
public class Trie implements Serializable{
    
    /**
     * Node arrel del Trie
     */
    private TrieNode root;

    /**
     * TrieNode: Estructura de dades pels nodes del Trie
     */
    private class TrieNode implements Serializable{

        /**
         * Punter als fills d'aquest node per cada caracter
         */
        HashMap<Character, TrieNode> children;

        /**
         * Indica si un node es final d'alguna paraula
         */
        boolean isEndWord = false;

        /**
         * Mapa amb els titols de l'autor que te el final de paraula en aquest node i les dates de modificacio
         */
        TreeMap<String, String> titolsDates;
        
        /**
         * Constructora del TrieNode
         */
        TrieNode() {
            children = new HashMap<>();
            titolsDates = new TreeMap<>(); //Fem servir treemap per a que estigui ordenat
        }
    }

    /**
     * Constructora del Trie
     */
    public Trie() {
        root = new TrieNode();
    }
    
    /** 
     * Metode per insertar documents al Trie
     * @param autor Autor del document
     * @param titol Titol del document
     * @param data Data de modificacio del document
     */
    public void AfegirDoc(String autor, String titol, String data) {
        TrieNode current = root;
    
        for (char l: autor.toCharArray()) {
            current = current.children.computeIfAbsent(l, c -> new TrieNode());
        }
        current.isEndWord = true;
        current.titolsDates.put(titol, data);
    }

    /** 
     * Metode per buscar documents al Trie
     * @param autor Autor del document a buscar
     * @param titol Titol del document a buscar
     * @return True si el document es troba al Trie, False si no
     */
    public boolean FindDoc(String autor, String titol) {
        TrieNode current = root;
        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return false;
            }
            current = node;
        }
        return current.titolsDates.containsKey(titol);
    }

    /** 
     * Metode per escriure la data d'un document
     * @param autor Autor del document
     * @param titol Titol del document
     * @param data Data del document
     */
    public void SetData(String autor, String titol, String data) {
        TrieNode current = root;
        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return;
            }
            current = node;
        }
        current.titolsDates.put(titol, data);
    }
    
    /** 
     * Metode per obtenir la data d'un document
     * @param autor Autor del document
     * @param titol Titol del document
     * @return Data del document. Null si no hi es al Trie
     */
    public String GetData(String autor, String titol) {
        TrieNode current = root;
        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return null;
            }
            current = node;
        }
        return current.titolsDates.get(titol);
    }

    /** 
     * Metode per esborrar documents del Trie
     * @param autor Autor del document
     * @param titol Titol del document
     */
    public void EsborrarDoc(String autor, String titol) {
        delete(root, autor, titol, 0);
    }

    /** 
     * Metode per actualitzar el titol d'un document del Trie
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newTitol Nou titol del document
     */
    public void ActualitzarTitol(String autor, String titol, String newTitol) {
        TrieNode current = root;
        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.children.get(ch);
            current = node;
        }
        String data = current.titolsDates.remove(titol);
        current.titolsDates.put(newTitol, data);
    }
    
    /** 
     * Metode per actualitzar l'autor d'un document del Trie
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newAutor Nou autor del document
     */
    public void ActualitzarAutor(String autor, String titol, String newAutor) {
        String data = GetData(autor, titol);
        EsborrarDoc(autor, titol);
        AfegirDoc(newAutor, titol, data);
    }
    
    /** 
     * Metode per obtenir tots els documents del Trie
     * @return Set amb totes les tries (autor, titol, data) del Trie
     */
    public Set<Pair<Pair<String, String>, String>> GetKeys() {
        Set<Pair<Pair<String, String>, String>> keys = new TreeSet<>();
        StringBuilder s = new StringBuilder();
        getKeys(root, 0, s, keys);
        return keys;
    }
    
    /** 
     * Metode per obtenir tots els titols d'un autor
     * @param autor Autor del que s'obtenen els titols
     * @return Set amb els titols de l'autor. Buit si no existeix
     */
    public Set<String> GetTitolsAutor(String autor) {
        TrieNode current = root;
        for (int i = 0; i < autor.length(); i++) {
            char ch = autor.charAt(i);
            TrieNode node = current.children.get(ch);
            if (node == null) {
                return new TreeSet<String>();
            }
            current = node;
        }
        return current.titolsDates.keySet();
    }
    
    /** 
     * Metode per obtenir els autors que comencen per prefix
     * @param prefix Prefix pel que han de començar els noms dels autors
     * @return Set amb els autors obtinguts ordenat
     */
    public Set<String> SearchWordsPrefix(String prefix) {   
        Set<String> words = new TreeSet<String>();
        
        TrieNode current = root;
        //Fem un recorregut fins al node on acaba el prefix
        for (int i = 0; i < prefix.length(); i++) {
            char ch = prefix.charAt(i);
            TrieNode node = current.children.get(ch);
            //Si no hi ha cap paraula que comenci per prefix retornem una llista buida
            if (node == null) {
                return words;
            }
            current = node;
        }

        StringBuilder builder = new StringBuilder(prefix);
        getWords(current, prefix.length(), builder, words);

        return words;
    }
        
    /** 
     * Metode per esborrar nodes del Trie de forma recursiva
     * @param current Node actual
     * @param autor Autor del document que estem esborrant del Trie
     * @param titol Titol del document que estem esborrant del Trie
     * @param index Profunditat actual del node current
     * @return True si s'hauria d'esborrar el node current, False si no
     */
    private boolean delete(TrieNode current, String autor, String titol, int index) {
        //Quan arribem a l'ultim node
        if (index == autor.length()) {
            current.titolsDates.remove(titol);
            if (!current.isEndWord) {
                return false;
            }
            if(current.titolsDates.isEmpty()) current.isEndWord = false;
            return current.children.isEmpty() && current.titolsDates.isEmpty();
        }

        char ch = autor.charAt(index);
        TrieNode node = current.children.get(ch);
        if (node == null) {
            return false;
        }
        boolean shouldDeleteCurrentNode = delete(node, autor, titol, index + 1) && !node.isEndWord;
    
        if (shouldDeleteCurrentNode) {
            current.children.remove(ch);
            return current.children.isEmpty();
        }
        return false;
    }
    
    /** 
     * Metode per obtenir els autors que comencen per prefix fent una cerca en profunditat
     * @param node Node actual
     * @param level Profunditat actual del node current
     * @param prefix Prefix pel que han de començar els noms dels autors
     * @param words Set amb els autors ja obtinguts
     */
    private void getWords(TrieNode node, int level, StringBuilder prefix, Set<String> words) {

        if(node.isEndWord){
            words.add(prefix.toString());
        }

        HashMap<Character, TrieNode> children = node.children;
        Iterator<Character> iterator = children.keySet().iterator();
        while (iterator.hasNext()) {
            char character = iterator.next();
            prefix = prefix.insert(level, character); 
            getWords(children.get(character), level+1, prefix, words);
            prefix.deleteCharAt(level);
        }
    }
    
    /** 
     * Metode per obtenir els documents des del node parametre fent una cerca en profunditat
     * @param node Node actual
     * @param level Profunditat actual del node current
     * @param autor Identificador d'autor del node actual
     * @param keys Set amb els documents ja obtinguts
     */
    private void getKeys(TrieNode node, int level, StringBuilder autor, Set<Pair<Pair<String, String>, String>> keys) {

        if(node.isEndWord){
            for (Entry<String, String> titolData : node.titolsDates.entrySet()) {
                Pair<String, String> key = new Pair<>(autor.toString(), titolData.getKey());
                Pair<Pair<String, String>, String> doc = new Pair<>(key, titolData.getValue());
                keys.add(doc);
            }
        }

        HashMap<Character, TrieNode> children = node.children;
        Iterator<Character> iterator = children.keySet().iterator();
        while (iterator.hasNext()) {
            char character = iterator.next();
            autor = autor.insert(level, character); 
            getKeys(children.get(character), level+1, autor, keys);
            autor.deleteCharAt(level);
        }
    }
}