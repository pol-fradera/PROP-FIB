package excepcions;

public class FormatInvalid extends Exception {
    public FormatInvalid() {
        super("El format del fitxer no és txt o xml.");
    }

    public String toString() { return "Has d'introduir un format vàlid: txt o xml.";}
}
