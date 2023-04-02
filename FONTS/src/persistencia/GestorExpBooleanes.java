package persistencia;

import transversal.Pair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe amb funcions estatiques per gestionar l’emmagatzematge i la carrega d’expressions booleanes al disc.
 * @author Pol Fradera
 */
public class GestorExpBooleanes {

    /**
     * Metode per carregar les expressions booleanes de disc.
     * @return Es retornen totes les expressions booleanes existents al disc (per cada una, el seu nom i l'expressio).
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
    public static List<Pair<String, String>> CarregarExpB() throws IOException {
        try {
            List<Pair<String, String>> exps = new ArrayList<>();
            String dirPath = "./appdata/expressions/";
            File carpeta = new File(dirPath);
            if (carpeta.exists()) {
                File[] llistaFitxers = carpeta.listFiles();
                if (llistaFitxers != null) {
                    for (File fitxer : llistaFitxers) {
                        String nom = fitxer.getName();
                        FileInputStream fileInputStream = new FileInputStream(dirPath.concat(nom));
                        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                        String exp = (String) objectInputStream.readObject();
                        objectInputStream.close();
                        Pair<String, String> p = new Pair<>();
                        p.x = nom;
                        p.y = exp;
                        exps.add(p);
                        fitxer.delete();
                    }
                }
            }
            return exps;
        }
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Metode per guardar les expressions booleanes a disc.
     * @param exps Totes les expressions booleanes que es volen guardar a disc.
     * @throws IOException Hi ha hagut algun problema en accedir al disc.
     */
   public static void GuardarExpB(List<Pair<String, String>> exps) throws IOException {
        String dirPath = "./appdata/expressions/";
        Files.createDirectories(Paths.get(dirPath));
       for (Pair<String, String> exp : exps) {
           FileOutputStream fileOutputStream = new FileOutputStream(dirPath.concat(exp.x));
           ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
           objectOutputStream.writeObject(exp.y);
           objectOutputStream.flush();
           objectOutputStream.close();
       }
    }
}