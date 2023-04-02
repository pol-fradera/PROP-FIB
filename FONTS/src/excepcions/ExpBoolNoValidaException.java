package excepcions;

public class ExpBoolNoValidaException extends Exception {
    public ExpBoolNoValidaException() {
        super("L'expressió booleana no és vàlida.");
    }

    public String toString() { return "L'expressió booleana introduïda no és vàlida.";}
}
