package excepcions;

public class EDocumentException extends Exception {
    public EDocumentException() {
        super("El document existeix.");
    }

    public String toString() { return "El document ja existeix.";}
}