package domini.datatypes;

/**
 * Estructura de dades que representa un node d'un arbre.
 * @author Pol Fradera
 */
public class TreeNode {

    /**
     * Contingut del node.
     */
    public String data;

    /**
     * Fill esquerre del node.
     */
    public TreeNode leftNode;

    /**
     * Fill dret del node.
     */
    public TreeNode rightNode;

    /**
     * Constructora del TreeNode.
     * @param s Contingut del node.
     */
    public TreeNode(String s) {
        this.data = s;
        this.leftNode = null;
        this.rightNode = null;
    }
}
