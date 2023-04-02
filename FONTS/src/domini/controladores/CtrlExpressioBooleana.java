package domini.controladores;
import domini.datatypes.*;
import excepcions.ExpBoolNoValidaException;
import transversal.*;

import java.util.*;

import static domini.datatypes.Utility.ParseFrase;

/**
 * Aquesta classe s’encarrega de gestionar les expressions booleanes del sistema.
 * @author Pol Fradera
 */
public class CtrlExpressioBooleana {

    /**
     * Conjunt d'expressions booleanes guardades al sistema.
     */
    private Map<String, ExpressioBooleana> expressions;

    /**
     * Constructora del controlador d'expressio booleana.
     */
    public CtrlExpressioBooleana() {
        expressions = new HashMap<>();
    }

    /**
     * Metode per saber si una expressio booleana ja existeix.
     * @param nom Nom de l'expressio booleana.
     * @return Indica si l'expressio booleana amb nom nom existeix.
     */
    public boolean existsExpressioBooleana(String nom) {
        return expressions.containsKey(nom);
    }

    /**
     * Metode que dona els identificadors de les frases complementàries a les indicades per set.
     * @param set Conjunt de frases en que es vol aplicar l'operacio not.
     * @param ci Controlador d'índex necessari per realitzar l'operacio del complementari.
     * @return Es retorna un Set amb els identificadors de les frases complementàries a les frases del Set set.
     */
    private Set<Integer> not(Set<Integer> set, CtrlIndex ci) {
        int n = ci.GetNumFrases();
        Set<Integer> complementary = new HashSet<>();
        for (int i = 0; i < n; ++i) {
            if (!set.contains(i)) complementary.add(i);
        }
        return complementary;
    }

    /**
     * Metode que dona els identificadors de les frases resultants d'aplicar l'operacio unio entre el set1 i el set2.
     * @param set1 Primer conjunt de frases en que es vol aplicar l'operacio unio.
     * @param set2 Segon conjunt de frases en que es vol aplicar l'operacio unio.
     * @return Es retorna un set amb els identificadors de les frases que es troben al set1 i també de les que es troben al set2.
     */
    private Set<Integer> union(Set<Integer> set1, Set<Integer> set2) {
        set1.addAll(set2);
        return set1;
    }

    /**
     * Metode que dona els identificadors de les frases resultants d'aplicar l'operacio interseccio entre el set1 i el set2.
     * @param set1 Primer conjunt de frases en que es vol aplicar l'operacio interseccio.
     * @param set2 Segon conjunt de frases en que es vol aplicar l'operacio interseccio.
     * @return Es retorna un set només amb els identificadors de les frases que es troben en el set1 i el set2.
     */
    private Set<Integer> intersection(Set<Integer> set1, Set<Integer> set2) {
        set1.retainAll(set2);
        return set1;
    }

    /**
     * Metode per saber si un string és un operador lògic.
     * @param s String qualsevol.
     * @return Indica si l'String s és un operador lògic.
     */
    private boolean isOperator(String s) {
        return s.length() == 1 && (s.equals("&") || s.equals("|") || s.equals("!"));
    }

    /**
     * Metode que dona els identificadors de les frases de tots els documents que compleixen l'expressio booleana que té com a arrel el node node.
     * @param node Arrel d'un subarbre de l'expressio booleana.
     * @param ci Controlador d'índex necessari per realitzar la cerca.
     * @return Es retorna un set amb els identificadors de les frases que compleixen l'expressio booleana que té com a arrel el node node.
     */
    private Set<Integer> cercaExpBol(TreeNode node, CtrlIndex ci) {
        if (!isOperator(node.data)) {
            String[] words = ParseFrase(node.data); //per la seqüència
            Set<Integer> frases = ci.GetFrases(words[0]); //frases on apareix la primera paraula
            if (words.length == 1) return frases; //només és una paraula
            else { //és una seqüència
                int i = 1;
                while (i < words.length) {
                    Set<Integer> frases2 = ci.GetFrases(words[i]);
                    frases.retainAll(frases2);
                    ++i;
                }
                return ci.GetSequencia(node.data, frases); //frases on apareix la seqüencia
            }
        } else {
            if (node.data.equals("&")) return intersection(cercaExpBol(node.leftNode, ci), cercaExpBol(node.rightNode, ci));
            else if (node.data.equals("|")) return union(cercaExpBol(node.leftNode, ci), cercaExpBol(node.rightNode, ci));
            else return not(cercaExpBol(node.leftNode, ci), ci);  //operador !
        }
    }

    /**
     * Metode que dona les claus dels documents que compleixen l'expressio booleana amb nom nom_exp.
     * @param nom_exp Nom de l'expressio booleana existent per fer la cerca.
     * @param ci Controlador d'índex necessari per realitzar la cerca.
     * @return Es retorna una llista amb les claus dels documents que compleixen l'expressio booleana amb nom nom_exp.
     */
    public List<Pair<String, String>> cercarExpressioBooleanaExistent(String nom_exp, CtrlIndex ci) {
        ExpressioBooleana expB = expressions.get(nom_exp);
        Tree expTree = expB.getExpA();
        Set<Integer> frases = cercaExpBol(expTree.root, ci);
        return ci.GetDocuments(frases);
    }

    /**
     * Metode que dona les claus dels documents que compleixen l'expressio booleana exp.
     * @param exp Expressio booleana per fer la cerca.
     * @param ci Controlador d'índex necessari per realitzar la cerca.
     * @return Es retorna una llista amb les claus dels documents que compleixen l'expressio booleana exp.
     * @throws ExpBoolNoValidaException exp no és vàlida.
     */
    public List<Pair<String, String>> cercarExpressioBooleana(String exp, CtrlIndex ci) throws ExpBoolNoValidaException {
        ExpressioBooleana expB = new ExpressioBooleana(exp);
        Tree expTree = expB.getExpA();
        Set<Integer> frases = cercaExpBol(expTree.root, ci);
        return ci.GetDocuments(frases);
    }

    /**
     * Getter d'expressions booleanes.
     * @return Es retornen totes les expressions booleanes existents al sistema (per cada una, el seu nom i l'expressio).
     */
    public List<Pair<String, String>> getAll() {
        List<Pair<String, String>> exps = new ArrayList<>();
        for (String clau : expressions.keySet()) {
            Pair<String, String> p = new Pair<>();
            p.x = expressions.get(clau).getNom();
            p.y = expressions.get(clau).getExp();
            exps.add(p);
        }
        return exps;
    }

    /**
     * Metode per crear una expressio booleana amb nom nom i expressio exp.
     * @param nom Nom de l'expressio booleana a crear.
     * @param exp Expressio de l'expressio booleana a crear.
     * @throws ExpBoolNoValidaException exp no és vàlida.
     */
    public void setExpressioBooleana(String nom, String exp) throws ExpBoolNoValidaException {
        ExpressioBooleana expB = new ExpressioBooleana(nom, exp);
        expressions.put(nom, expB);
    }

    /**
     * Metode per esborrar l'expressio booleana amb nom nom.
     * @param nom Nom de l'expressio booleana a esborrar.
     */
    public void deleteExpressioBooleana(String nom) {
        expressions.remove(nom);
    }
}