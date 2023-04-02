package excepcions;

public class IDInvalid extends Exception {
    public IDInvalid() {
        super("L'id indicat conté \"_\" o és més llarg de 50 caràcters.");
    }

    public String toString() { return "Ni el títol ni l'autor pot contenir \"_\" ni ser més llarg de 50 caràcters.";}
}
