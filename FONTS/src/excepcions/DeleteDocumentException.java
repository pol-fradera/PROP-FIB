package excepcions;

public class DeleteDocumentException extends Exception {
    public DeleteDocumentException() {
        super("El document no s'ha pogut esborrar.");
    }


    public String toString() { return "El document no s'ha esborrat, intenta-ho de nou.";}
}