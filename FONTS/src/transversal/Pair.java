package transversal;

import java.io.Serializable;
import java.util.Objects;

/**
 * Classe que unifica 2 objectes
 * @author Christian Rivero
 */
public class Pair<T1, T2> implements Comparable, Serializable{
    /**
     * Primer objecte de la clase pair
     */
    public T1 x;
    /**
     * Segon objecte de la clase pair
     */
    public T2 y;

    /**
     * Creadora que inicialitza els parametres
     * @param x Objecte 1
     * @param y Objecte 2
     */
    public Pair(T1 x, T2 y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creadora bàsica
     */
    public Pair() {}

    /**
     * Metode override de la funcio equals per defecte. Retorna true si dos objectes pairs son iguals, i fals si no ho son
     * @param obj Objecte explicit a comparar
     * @return Indica si l'objecte implicit es igual que l'objecte explicit
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Pair<?, ?> pair = (Pair<?, ?>) obj;
        return x.equals(pair.x) && y.equals(pair.y);
    }

    /**
     * Metode override de la funcio compareTo per defecte
     * @param o Objecte explicit a comparar
     * @return Es 1 si el paràmetre implicit es mes petit que el parametre explicit, 0 si son iguals i -1 si es mes gran. Es mes petit si te un ‘x’ mes petit, i si son iguals, si te un ‘y’ mes petit
     */
    @Override
    public int compareTo(Object o) {
        if(hashCode() < o.hashCode()) return -1;
        if(hashCode() == o.hashCode()) return 0;
        else return 1;
    }

    /**
     * Metode override de la funcio hashCode per defecte.
     * @return El hashCode dels objectes ‘x’ i ‘y’.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}