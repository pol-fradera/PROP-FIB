package domini.datatypes;
import java.util.*;
import excepcions.*;

/**
 * Estructura de dades que representa un arbre.
 * @author Pol Fradera
 */

public class Tree {

    /**
     * Node arrel del Tree.
     */
    public TreeNode root;

    /**
     * Constructora del Tree.
     * @param exp Expressio booleana.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    public Tree(List<String> exp) throws ExpBoolNoValidaException {
        List<String> llista;
        llista = infixToPost(exp);
        root = expressionTree(llista);
    }

    /**
     * Metode per saber si un String es un operador logic.
     * @param s String qualsevol.
     * @return Indica si l'String s es un operador logic., un parentesi o una clau d'obertura.
     */
    private boolean isOperator(String s) {
        return s.length() == 1 && (s.equals("&") || s.equals("|") || s.equals("!") || s.equals("(") || s.equals(")"));
    }

    /**
     * Metode que crea un arbre a partir d'una llista que representa una expressio booleana en notacio postfix.
     * @param postfix Llista que representa una expressio booleana en notacio postfix.
     * @return Node arrel de l'arbre generat.
     * @throws ExpBoolNoValidaException exp no es valida.
     */
    private TreeNode expressionTree(List<String> postfix) throws ExpBoolNoValidaException {
        Stack<TreeNode> st = new Stack<>();
        TreeNode t1 = null;
        TreeNode t2, node;
        try {
            for (String s : postfix) {
                if (!isOperator(s)) {
                    node = new TreeNode(s);
                    st.push(node);
                } else {
                    node = new TreeNode(s);

                    if (!s.equals("!")) t1 = st.pop();
                    t2 = st.pop();

                    node.leftNode = t2;
                    if (!s.equals("!")) node.rightNode = t1;

                    st.push(node);
                }
            }
            node = st.pop();
            if (!st.empty()) throw new ExpBoolNoValidaException(); //falten operadors
            return node;
        }
        catch (EmptyStackException e) {
            throw new ExpBoolNoValidaException(); //falten operands
        }
    }

    /**
     * Metode que retorna la prioritat de l'operador.
     * @param s Operador.
     * @return Nombre que indica la prioritat de l'operador.
     */
    private int priority(String s) {
        if (s.equals("|")) {
            return 1;               //Precedence of | 1
        }
        else if (s.equals("&")) {
            return 2;               //Precedence of & is 2
        }
        else if (s.equals("!")) {
            return 3;               //Precedence of ! is 3
        }
        else if (s.equals("(")) {
            return 0;
        }
        return -1;
    }

    /**
     * Metode que converteix una llista que representa una expressio booleana en notacio infix a una llista en notacio postfix.
     * @param infix Expressio booleana en notacio infix.
     * @return Expressio booleana en notacio postfix.
     */
    private List<String> infixToPost(List<String> infix) {
        Stack<String> st = new Stack<>();
        st.push("#"); //per mirar quan la pila es buida

        List<String> postfix = new ArrayList<>();

        for (String s : infix) {
            if (!isOperator(s)) {
                postfix.add(s);
            }
            else if (s.equals("(")) {
                st.push(s);
            }
            else if (s.equals(")")) {
                while (!st.peek().equals("#") && !st.peek().equals("(")) {
                    postfix.add(st.peek());
                    st.pop();
                }
                st.pop();
            }
            else {
                if (priority(s) > priority(st.peek())) st.push(s);
                else {
                    while (!st.peek().equals("#") && priority(s) <= priority(st.peek())) {
                        postfix.add(st.peek());
                        st.pop();
                    }
                    st.push(s);
                }
            }
        }

        while(!st.peek().equals("#")) {
            postfix.add(st.peek());        //afegir els nodes fins que la pila quedi buida
            st.pop();
        }
        return postfix;
    }
}
