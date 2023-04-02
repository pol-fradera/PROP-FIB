package domini.datatypes;
import excepcions.ExpBoolNoValidaException;

import java.util.*;

/**
* S'implementa la classe Expressio Booleana.
* @author Pol Fradera
*/
public class ExpressioBooleana {

    /**
     * Nom de l'expressio booleana.
     */
    private String nom;

    /**
     * Expressio de l'expressio booleana.
     */
    private String exp;

    /**
     * Expressio en forma d'arbre de l'expressio booleana.
     */
    private Tree expA;

    /**
     * Constructora de l'expressio booleana amb nomes l'expressio.
     * @param exp Expressio de l'expressio booleana.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    public ExpressioBooleana(String exp) throws ExpBoolNoValidaException {
        esCorrecte(exp);

        this.exp = exp;
        List<String> llistaExp = crearLlista();
        this.expA = new Tree(llistaExp);
    }

    /**
     * Constructora de l'expressio booleana amb el nom i l'expressio.
     * @param nom Nom de l'expressio booleana.
     * @param exp Expressio de l'expressio booleana.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    public ExpressioBooleana(String nom, String exp) throws ExpBoolNoValidaException {
        esCorrecte(exp);

        this.nom = nom;
        this.exp = exp;
        List<String> llistaExp = crearLlista();
        this.expA = new Tree(llistaExp);
    }

    /**
     * Metode que llença una excepcio si la part de l'expressio on hi ha cometes o claus no es correcte.
     * @param index Indica a partir de quin caracter s'ha de comprovar l'expressio.
     * @param exp Expressio booleana per a comprovar.
     * @param c_final Caracter que indica fins a on s'ha de fer la comprovacio.
     * @return Indica l'index següent del caracter c_final.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    private int comprovacio_cometes_i_clau(int index, String exp, char c_final) throws ExpBoolNoValidaException {
        ++index;
        boolean espai_disponible = false;
        boolean paraules = false; //true si hi ha mes d'una paraula
        while (index < exp.length() && exp.charAt(index) != c_final) {
            if (es_operador(exp.charAt(index))) throw new ExpBoolNoValidaException();
            else if (exp.charAt(index) == ' ') {
                if (!espai_disponible) throw new ExpBoolNoValidaException();
                else espai_disponible = false;
                paraules = true;
            }
            else espai_disponible = true;
            ++index;
        }
        if (index >= exp.length() || !espai_disponible || !paraules) throw new ExpBoolNoValidaException();
        return index;
    }

    /**
     * Metode que llença una excepcio si l'expressio exp no es correcte.
     * @param exp Expressio booleana per a comprovar.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    private void esCorrecte(String exp) throws ExpBoolNoValidaException {
        if (exp.length() == 0) throw new ExpBoolNoValidaException(); //expressio buida
        int i = 0;
        boolean espai = false;
        boolean espai_necessari = false;
        int parentesis_oberts = 0;
        while (i < exp.length()) {
            if (exp.charAt(i) == ' ') {
                if (espai) throw new ExpBoolNoValidaException(); //dos espais seguits
                else espai = true;
                if (espai_necessari) espai_necessari = false;
            }
            else {
                if (espai_necessari) throw new ExpBoolNoValidaException(); //operadors mal col·locats
                else if (exp.charAt(i) == '(') ++parentesis_oberts;
                else if (exp.charAt(i) == ')') {
                    if (parentesis_oberts == 0) throw new ExpBoolNoValidaException(); //mal parentitzat
                    else --parentesis_oberts;
                }
                else if (exp.charAt(i) == '&' || exp.charAt(i) == '|') {
                    if (!espai) throw new ExpBoolNoValidaException(); //operadors mal col·locats
                    espai_necessari = true;
                }
                else if (exp.charAt(i) == '"') i = comprovacio_cometes_i_clau(i, exp, '"');
                else if (exp.charAt(i) == '{') i = comprovacio_cometes_i_clau(i, exp, '}');
                else if (exp.charAt(i) == '}') throw new ExpBoolNoValidaException(); //claus incorrectes
                espai = false;
            }
            ++i;
        }
        if (parentesis_oberts > 0) throw new ExpBoolNoValidaException(); //mal parentitzat
        if (exp.charAt(i-1) == '!') throw new ExpBoolNoValidaException(); //! incorrecte
        if (exp.charAt(i-1) == ' ') throw new ExpBoolNoValidaException(); //espai incorrecte
    }

    /**
     * Metode per saber si un char es un operador logic.
     * @param c Char qualsevol.
     * @return Indica si el char c es un operador logic, un parentesi o una clau d'obertura.
     */
    private boolean es_operador (char c) {
        if (c == '&' || c == '|' || c == '!' || c == '(' || c == '{' || c == ')') return true;
        return false;
    }

    /**
     * Metode que converteix en una llista la part de l'expressio que esta entre cometes.
     * @param llista Llista on s'afegeixen els strings corresponents.
     * @param index Indica a partir de quin caracter s'ha de convertir l'expressio a una llista.
     * @return Indica l'index següent del caracter cometes de tancament.
     */
    private int casCometes(List<String> llista, int index) {
        ++index;
        String s = "";
        while (exp.charAt(index) != '\"') {
            s += exp.charAt(index);
            ++index;
        }
        llista.add(s);
        return index;
    }

    /**
     * Metode que converteix en una llista la part de l'expressio que esta entre claus.
     * @param llista Llista on s'afegeixen els strings corresponents.
     * @param index Indica a partir de quin caracter s'ha de convertir l'expressio a una llista.
     * @return Indica l'index següent del caracter clau de tancament.
     */
    private int casClau(List<String> llista, int index) {
        llista.add("(");
        ++index;
        String s = "";
        while (exp.charAt(index) != '}') {
            if (exp.charAt(index) == ' ') {
                llista.add(s);
                llista.add("&");
                s = "";
            }
            else s += exp.charAt(index);
            ++index;
        }
        llista.add(s);
        llista.add(")");
        return index;
    }

    /**
     * Metode que converteix l'expressio booleana a una llista de strings.
     * @return Llista que representa l'expressio booleana.
     */
    private List<String> crearLlista() {
        List<String> llista = new ArrayList<>();
        int i = 0;
        boolean paraula = false;
        String s1 = "";
        while (i < exp.length()) {
            if (exp.charAt(i) == '{') i = casClau(llista, i);
            else if (exp.charAt(i) == '\"') i = casCometes(llista, i);
            else if (exp.charAt(i) == '&') {
                llista.add("&");
                ++i;
            }
            else if (exp.charAt(i) == '|') {
                llista.add("|");
                ++i;
            }
            else if (exp.charAt(i) == '!') {
                if (paraula) {
                    llista.add(s1);
                    paraula = false;
                }
                llista.add("!");
                s1 = "";
            }
            else if (exp.charAt(i) == '(') llista.add("(");
            else if (exp.charAt(i) == ')') {
                if (paraula) {
                    llista.add(s1);
                    paraula = false;
                }
                llista.add(")");
                s1 = "";
            }
            else if (paraula && exp.charAt(i) == ' ') { //si abans de l'espai hi ha una paraula
                llista.add(s1);
                s1 = "";
                paraula = false;
            }
            else if (exp.charAt(i) != ' ') {  //si hi ha un caracter d'una paraula
                s1 += exp.charAt(i);
                paraula = true;
            }
            ++i;
        }
        if (s1.length() > 0) llista.add(s1);
        return llista;
    }

    /**
     * Getter del nom de l'expressio booleana.
     * @return Es retorna el nom de l'expressio booleana.
     */
    public String getNom() { return nom; }

    /**
     * Getter de l'expressio de l'expressio booleana.
     * @return Es retorna l'expressio de l'expressio booleana.
     */
    public String getExp() { return exp; }

    /**
     * Getter de l'arbre de l'expressio booleana.
     * @return Es retorna l'arbre de l'expressio booleana.
     */
    public Tree getExpA() { return expA; }
}