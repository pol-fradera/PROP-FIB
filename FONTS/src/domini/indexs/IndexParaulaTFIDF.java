package domini.indexs;

import java.util.PriorityQueue;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import transversal.Pair;
import domini.datatypes.Utility;

/**
 * Index de semblança entre documents
 * @author Eric Ryhr
 */
public class IndexParaulaTFIDF implements Serializable{

    /**
     * Set amb les paraules que no es guardaran a l'index
     */
    static Set<String> stopWords;
    
    /**
     * Index TFIDFs per document i paraula. x es TF i y es TFIDF
     */
    private TreeMap<Pair<String, String>, TreeMap<String, Pair<Double, Double>>> indexTFIDF;
    
    /**
     * Index nombre de documents on apareix cada paraula
     */
    private TreeMap<String, Integer> indexNumDocumentsParaula;

    /**
     * Constructora de la classe IndexParaulaTFIDF. Carrega les stopWords
     */
    public IndexParaulaTFIDF() {
        indexTFIDF = new TreeMap<Pair<String, String>, TreeMap<String, Pair<Double, Double>>>();
        indexNumDocumentsParaula = new TreeMap<String, Integer>();
        if(stopWords == null) readStopWords();
    }

    /** 
     * Metode per insertar documents a l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     */
    public void AfegirDoc(String autor, String titol, List<String> contingut) {
        //Juntem totes les paraules del document en una llista
        List<String> paraules = getAllWords(contingut);
        int numWordsDoc = paraules.size();
        Pair<String, String> autorTitol = new Pair<String, String>(autor, titol);

        //Creem una nova fila pel nou document i en calculem les Term Frequencies
        TreeMap<String, Pair<Double, Double>> infoDoc = new TreeMap<>();
        calcularTFs(infoDoc, paraules, numWordsDoc, false);
        indexTFIDF.put(autorTitol, infoDoc);

        //Actualitzem les frequencies globals i els TFIDFs
        calcularNumDocumentsParaules(paraules);
        calcularTFIDFs();
    }

    
    /** 
     * Metode per esborrar documents de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     */
    public void EsborrarDoc(String autor, String titol) {
        Pair<String, String> autorTitol = new Pair<String,String>(autor, titol);

        TreeMap<String, Pair<Double, Double>> infoDoc = indexTFIDF.get(autorTitol);
        //Per cada paraula del document actualitzem indexGlobalTF
        for(Map.Entry<String, Pair<Double, Double>> infoWord : infoDoc.entrySet()) {
            String paraula = infoWord.getKey();
            int newGlobalTF = indexNumDocumentsParaula.get(paraula) - 1;
            indexNumDocumentsParaula.put(paraula, newGlobalTF);
        }

        indexTFIDF.remove(autorTitol);
        
        calcularTFIDFs();
    }

    
    /** 
     * Metode per actualitzar el titol d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newTitol Nou titol del document
     */
    public void ActualitzarTitol(String autor, String titol, String newTitol) {
        Pair<String, String> oldAutorTitol = new Pair<String, String>(autor, titol);
        Pair<String, String> newAutorTitol = new Pair<String, String>(autor, newTitol);

        TreeMap<String, Pair<Double, Double>> infoDoc = indexTFIDF.remove(oldAutorTitol);
        indexTFIDF.put(newAutorTitol, infoDoc);
    }

    
    /** 
     * Metode per actualitzar l'autor d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param newAutor Nou autor del document
     */
    public void ActualitzarAutor(String autor, String titol, String newAutor) {
        Pair<String, String> oldAutorTitol = new Pair<String, String>(autor, titol);
        Pair<String, String> newAutorTitol = new Pair<String, String>(newAutor, titol);

        TreeMap<String, Pair<Double, Double>> infoDoc = indexTFIDF.remove(oldAutorTitol);
        indexTFIDF.put(newAutorTitol, infoDoc);
    }

    
    /** 
     * Metode per actualitzar el contingut d'un document de l'index
     * @param autor Autor del document
     * @param titol Titol del document
     * @param contingut Contingut del document
     */
    public void ActualitzarContingut(String autor, String titol, List<String> contingut) {
        EsborrarDoc(autor, titol);
        AfegirDoc(autor, titol, contingut);
    }

    
    /** 
     * Metode per comprovar si un document es a l'index
     * @param autorTitol Clau del document
     * @return True si el document es a l'index, False si no
     */
    public boolean DocExists(Pair<String, String> autorTitol){
        return indexTFIDF.get(autorTitol) != null;
    }

    
    /** 
     * Metode per obtenir els documents mes semblants al passat per parametre
     * @param autorTitol Document a comparar
     * @param K Nombre de respostes a retornar
     * @param estrategia True si la comparacio es fa amb TF, False si amb TF-IDF
     * @return Llista dels documents mes semblants ordenats amb el mes semblant primer
     */
    public List<Pair<String, String>> GetKDocsSimilarS(Pair<String, String> autorTitol, int K, boolean estrategia) {
        //Obtenim la llista de TF-IDF's de S
        TreeMap<String, Pair<Double, Double>> qTFIDF = indexTFIDF.get(autorTitol);
        
        //Creem un comparador que ordeni elements de gran a petit
        Comparator<Pair<Double, Pair<String, String>>> customComparator = new Comparator<Pair<Double, Pair<String, String>>>() {
            @Override
            public int compare(Pair<Double, Pair<String, String>> s1, Pair<Double, Pair<String, String>> s2) {
                return Double.compare(s2.x, s1.x);
            }
        };
        //Aqui colocarem els documents en ordre de similaritat
        PriorityQueue<Pair<Double, Pair<String, String>>> docsSemblants = new PriorityQueue<Pair<Double, Pair<String, String>>>(customComparator);

        //Comparem tots els documents amb query
        for (Pair<String, String> doc : indexTFIDF.keySet()) {
            if(doc.equals(autorTitol)) continue;

            TreeMap<String, Pair<Double, Double>> docTFIDF = indexTFIDF.get(doc);
            double metric = cosinusMetric(qTFIDF, docTFIDF, estrategia);

            System.out.println(doc.x + " " + doc.y + " " + metric);

            docsSemblants.add(new Pair<Double, Pair<String, String>>(metric, doc));
        }
        
        //Retornem els K primers resultats
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        for(int i = 0; i < K; i++) {
            if(docsSemblants.size() == 0) return result;
            result.add(docsSemblants.poll().y);
        }
        
        return result;
    }

    
    /** 
     * Metode per obtenir els documents mes semblants a l'entrada passada per parametre
     * @param entrada Entrada a comparar
     * @param K Nombre de respostes a retornar
     * @param estrategia True si la comparacio es fa amb TF, False si amb TF-IDF
     * @return Llista dels documents mes semblants ordenats amb el mes semblant primer
     */
    public List<Pair<String, String>> CercaPerRellevancia(String entrada, int K, boolean estrategia) {
        //Obtenim la llista de TF-IDF's de l'entrada
        TreeMap<String, Pair<Double, Double>> qTFIDF = new TreeMap<>();
        //Juntem totes les paraules de l'entrada en una llista
        List<String> paraules = getAllWords(Arrays.asList(entrada));
        int numWordsDoc = paraules.size();

        //TFs de la query
        calcularTFs(qTFIDF, paraules, numWordsDoc, true);
        //TF-IDFs de la query
        for(Map.Entry<String,Pair<Double, Double>> infoWord : qTFIDF.entrySet()) {
            String paraula = infoWord.getKey();
            infoWord.getValue().y = infoWord.getValue().x * idf(paraula);
        }

        //Creem un comparador que ordeni elements de gran a petit
        Comparator<Pair<Double, Pair<String, String>>> customComparator = new Comparator<Pair<Double, Pair<String, String>>>() {
            @Override
            public int compare(Pair<Double, Pair<String, String>> s1, Pair<Double, Pair<String, String>> s2) {
                return Double.compare(s2.x, s1.x);
            }
        };
        //Aqui colocarem els documents en ordre de similaritat
        PriorityQueue<Pair<Double, Pair<String, String>>> docsSemblants = new PriorityQueue<Pair<Double, Pair<String, String>>>(customComparator);

        //Comparem tots els documents amb query
        for (Pair<String, String> doc : indexTFIDF.keySet()) {
            TreeMap<String, Pair<Double, Double>> docTFIDF = indexTFIDF.get(doc);
            double metric = cosinusMetric(qTFIDF, docTFIDF, estrategia);

            docsSemblants.add(new Pair<Double, Pair<String, String>>(metric, doc));
        }
        
        //Retornem els K primers resultats
        List<Pair<String, String>> result = new ArrayList<Pair<String, String>>();
        for(int i = 0; i < K; i++) {
            if(docsSemblants.size() == 0) return result;
            result.add(docsSemblants.poll().y);
        }
        
        return result;
    }

    
    /** 
     * Metode per obtenir la semblança entre 2 documents
     * @param query Document 1
     * @param document Document 2
     * @param estrategia True si la comparacio es fa amb TF, False si amb TF-IDF
     * @return Semblança entre els 2 documents entre [0, 1]
     */
    static private double cosinusMetric(TreeMap<String, Pair<Double, Double>> query, TreeMap<String, Pair<Double, Double>> document, boolean estrategia){
        //Si algun dels documents es buit la metrica es nula
        if(query.size() == 0 || document.size() == 0) return 0.0;

        double dot = 0.0;
        double queryNorm = 0.0;
        double documentNorm = 0.0;

        Iterator<String> itQuery = query.keySet().iterator();
        Iterator<String> docQuery = document.keySet().iterator();
        String wordQ = (String) itQuery.next();
        String wordD = (String) docQuery.next();

        while(true) {
            double q, d;
            if(estrategia) {
                //Agafem TFs
                q = query.get(wordQ).x;
                d = document.get(wordD).x;
            } else {
                //Agafem TFIDFs
                q = query.get(wordQ).y;
                d = document.get(wordD).y;
            }
            //Si son iguals calculem el producte escalar i avancem els 2 iteradors
            if(wordQ.equals(wordD)) {
                dot += q*d;
                queryNorm += q*q;
                documentNorm += d*d;
                if(itQuery.hasNext()) wordQ = (String) itQuery.next();
                else break;
                if(docQuery.hasNext()) wordD = (String) docQuery.next();
                else break;
            //Si un dels dos es mes petit l'avancem fins que agafi a l'altre
            } else if (wordQ.compareTo(wordD) < 0) {
                queryNorm += q*q;
                if(itQuery.hasNext()) wordQ = (String) itQuery.next();
                else break;
            } else {
                documentNorm += d*d;
                if(docQuery.hasNext()) wordD = (String) docQuery.next();
                else break;
            }
        }
        
        /*while(itQuery.hasNext() && docQuery.hasNext()) {
            double q, d;
            if(estrategia) {
                //Agafem TFs
                q = query.get(wordQ).x;
                d = document.get(wordD).x;
            } else {
                //Agafem TFIDFs
                q = query.get(wordQ).y;
                d = document.get(wordD).y;
            }
            //Si son iguals calculem el producte escalar i avancem els 2 iteradors
            if(wordQ.equals(wordD)) {
                dot += q*d;
                queryNorm += q*q;
                documentNorm += d*d;
                wordQ = (String) itQuery.next();
                wordD = (String) docQuery.next();
            //Si un dels dos es mes petit l'avancem fins que agafi a l'altre
            } else if (wordQ.compareTo(wordD) < 0) {
                queryNorm += q*q;
                wordQ = (String) itQuery.next();
            } else {
                documentNorm += d*d;
                wordD = (String) docQuery.next();
            }
        } */

        queryNorm = Math.sqrt(queryNorm);
        documentNorm = Math.sqrt(documentNorm);

        double res;
        if(queryNorm == 0.0 || documentNorm == 0.0) res = 0.0;
        else res = dot/(queryNorm*documentNorm);

        return res;
    }

    
    /** 
     * Metode per obtenir totes les paraules d'un contingut
     * @param frases Contingut en forma de llista de frases
     * @return Llista de paraules
     */
    static private List<String> getAllWords(List<String> frases){
        List<String> paraules = new ArrayList<String>();
        for (String frase : frases) {
            paraules.addAll(Arrays.asList(Utility.ParseFrase(frase)));
        }
        return paraules;
    }

    
    /** 
     * Metode per calcular l'IDF d'una paraula
     * @param word Paraula a calcular
     * @return IDF calculat
     */
    private double idf(String word) {
        return Math.log((1+indexTFIDF.size())/(1+indexNumDocumentsParaula.get(word))) + 1;
    }

    
    /** 
     * Metode per calcular les TFs de les paraules d'un document
     * @param infoDoc Set de TFs i TFIDFs del document
     * @param paraules Llista de paraules del document
     * @param numWordsDoc Nombre de paraules del document
     * @param ignoreNewWords True les noves paraules no es posen a l'indexNumDocumentsParaula, False si
     */
    private void calcularTFs(TreeMap<String, Pair<Double, Double>> infoDoc, List<String> paraules, int numWordsDoc, boolean ignoreNewWords){
        //Sumem les paraules que apareixen al document
        for(String paraula : paraules) {
            if(stopWords.contains(paraula)) continue;
            //Si la paraula no existia afegim una nova columna al index global de paraules
            if(!indexNumDocumentsParaula.containsKey(paraula)) {
                if(ignoreNewWords) continue; //No afegirem la paraula al index
                else indexNumDocumentsParaula.put(paraula, 0);
            }
            //Sumem 1 a TF
            if(infoDoc.get(paraula) == null) infoDoc.put(paraula, new Pair<Double, Double>(1.0, 0.0));
            else {
                //Fem get i despres put perque no es pot fer get(paraula).x++
                Pair<Double, Double> aux = infoDoc.get(paraula);
                aux.x++;
                infoDoc.put(paraula, aux);
            }
        }
        //Dividim pel nombre de paraules del document per obtenir TF
        for (Pair<Double, Double> infoWord : infoDoc.values()) {
            infoWord.x = infoWord.x/numWordsDoc;
        }
    }

    
    /** 
     * Metode per actualitzar l'indexNumDocumentsParaula
     * @param paraules Noves paraules a inserir a l'index o a actualitzar el seu valor
     */
    private void calcularNumDocumentsParaules(List<String> paraules) {
        //Per cada paraula recorrem la seva columna del index i comptem els cops que el seu TF > 0
        for (String paraula : paraules) {
            int count = 0; 
            for (TreeMap<String, Pair<Double, Double>> infoDoc : indexTFIDF.values()) {
                if(infoDoc.get(paraula) != null) 
                    if(infoDoc.get(paraula).x > 0.0) count++;
            }
            indexNumDocumentsParaula.put(paraula, count);
        }
    }

    /**
     * Metode per calcular els TFIDFs de totes les paraules de tots els documents de l'index
     */
    private void calcularTFIDFs() {
        for (TreeMap<String, Pair<Double, Double>> infoDoc : indexTFIDF.values()) {
            for(Map.Entry<String,Pair<Double, Double>> infoWord : infoDoc.entrySet()) {
                String paraula = infoWord.getKey();
                infoWord.getValue().y = infoWord.getValue().x * idf(paraula);
            }
        }
    }

    /**
     * Metode per llegir les stop words i inicialitzar el set stopWords
     */
    static private void readStopWords() {
        stopWords = new HashSet<>();
        
        //Add stop words
        String[] caStopWords = ca.split(" ");
        String[] spStopWords = sp.split(" ");
        String[] engStopWords = eng.split(" ");

        for (String caStopWord : caStopWords) stopWords.add(caStopWord);
        for (String spStopWord : spStopWords) stopWords.add(spStopWord);
        for (String engStopWord : engStopWords) stopWords.add(engStopWord);
    }

    /**
     * Llista d'stop words en catala
     */
    static String ca = "últim última últimes últims a abans això al algun alguna algunes alguns allà allí allò als altra altre altres amb aprop aquí aquell aquella aquelles aquells aquest aquesta aquestes aquests cada catorze cent cert certa certes certs cinc com cosa d' darrer darrera darreres darrers davant de del dels després deu dinou disset divuit dos dotze durant el ell ella elles ells els en encara et extra fins hi i jo l' la les li llur lo los més m' ma massa mateix mateixa mateixes mateixos mes meu meva mig molt molta moltes molts mon mons n' na ni no nosaltres nostra nostre nou ns o on onze pel per però perquè perque poc poca pocs poques primer primera primeres primers prop què qual quals qualsevol qualssevol quan quant quanta quantes quants quatre que qui quin quina quines quins quinze res s' sa segon segona segones segons sense ses set setze seu seus seva seves sino sis sobre son sons sota t' ta tal tals tan tant tanta tantes tants tes teu teus teva teves ton tons tot tota totes tots tres tretze tu un una unes uns vint vos vosaltres vosté vostés vostra vostre vuit";
    
    /**
     * Llista d'stop words en castella
     */
    static String sp = "a actualmente adelante además afirmó agregó ahora ahí al algo alguna algunas alguno algunos algún alrededor ambos ante anterior antes apenas aproximadamente aquí aseguró así aunque ayer añadió aún bajo bien buen buena buenas bueno buenos cada casi cerca cierto cinco comentó como con conocer considera consideró contra cosas creo cual cuales cualquier cuando cuanto cuatro cuenta cómo da dado dan dar de debe deben debido decir dejó del demás dentro desde después dice dicen dicho dieron diferente diferentes dijeron dijo dio donde dos durante e ejemplo el ella ellas ello ellos embargo en encuentra entonces entre era eran es esa esas ese eso esos esta estaba estaban estamos estar estará estas este esto estos estoy estuvo está están ex existe existen explicó expresó fin fue fuera fueron gran grandes ha haber habrá había habían hace hacen hacer hacerlo hacia haciendo han hasta hay haya he hecho hemos hicieron hizo hoy hubo igual incluso indicó informó junto la lado las le les llegó lleva llevar lo los luego lugar manera manifestó mayor me mediante mejor mencionó menos mi mientras misma mismas mismo mismos momento mucha muchas mucho muchos muy más nada nadie ni ninguna ningunas ninguno ningunos ningún no nos nosotras nosotros nuestra nuestras nuestro nuestros nueva nuevas nuevo nuevos nunca o ocho otra otras otro otros para parece parte partir pasada pasado pero pesar poca pocas poco pocos podemos podrá podrán podría podrían poner por porque posible primer primera primero primeros principalmente propia propias propio propios próximo próximos pudo pueda puede pueden pues que quedó queremos quien quienes quiere quién qué realizado realizar realizó respecto se sea sean segunda segundo según seis ser será serán sería señaló si sido siempre siendo siete sigue siguiente sin sino sobre sola solamente solas solo solos son su sus sí sólo tal también tampoco tan tanto tendrá tendrán tenemos tener tenga tengo tenido tenía tercera tiene tienen toda todas todavía todo todos total tras trata través tres tuvo un una unas uno unos usted va vamos van varias varios veces ver vez y ya yo él ésta éstas éste éstos última últimas último últimos";
    
    /**
     * Llista d'stop words en angles
     */
    static String eng = "a a's able about above according accordingly across actually after afterwards again against ain't all allow allows almost alone along already also although always am among amongst an and another any anybody anyhow anyone anything anyway anyways anywhere apart appear appreciate appropriate are aren't around as aside ask asking associated at available away awfully b be became because become becomes becoming been before beforehand behind being believe below beside besides best better between beyond both brief but by c c'mon c's came can can't cannot cant cause causes certain certainly changes clearly co com come comes concerning consequently consider considering contain containing contains corresponding could couldn't course currently d definitely described despite did didn't different do does doesn't doing don't done down downwards during e each edu eg eight either else elsewhere enough entirely especially et etc even ever every everybody everyone everything everywhere ex exactly example except f far few fifth first five followed following follows for former formerly forth four from further furthermore g get gets getting given gives go goes going gone got gotten greetings h had hadn't happens hardly has hasn't have haven't having he he's hello help hence her here here's hereafter hereby herein hereupon hers herself hi him himself his hither hopefully how howbeit however i i'd i'll i'm i've ie if ignored immediate in inasmuch inc indeed indicate indicated indicates inner insofar instead into inward is isn't it it'd it'll it's its itself j just k keep keeps kept know knows known l last lately later latter latterly least less lest let let's like liked likely little look looking looks ltd m mainly many may maybe me mean meanwhile merely might more moreover most mostly much must my myself n name namely nd near nearly necessary need needs neither never nevertheless new next nine no nobody non none noone nor normally not nothing novel now nowhere o obviously of off often oh ok okay old on once one ones only onto or other others otherwise ought our ours ourselves out outside over overall own p particular particularly per perhaps placed please plus possible presumably probably provides q que quite qv r rather rd re really reasonably regarding regardless regards relatively respectively right s said same saw say saying says second secondly see seeing seem seemed seeming seems seen self selves sensible sent serious seriously seven several shall she should shouldn't since six so some somebody somehow someone something sometime sometimes somewhat somewhere soon sorry specified specify specifying still sub such sup sure t t's take taken tell tends th than thank thanks thanx that that's thats the their theirs them themselves then thence there there's thereafter thereby therefore therein theres thereupon these they they'd they'll they're they've think third this thorough thoroughly those though three through throughout thru thus to together too took toward towards tried tries truly try trying twice two u un under unfortunately unless unlikely until unto up upon us use used useful uses using usually uucp v value various very via viz vs w want wants was wasn't way we we'd we'll we're we've welcome well went were weren't what what's whatever when whence whenever where where's whereafter whereas whereby wherein whereupon wherever whether which while whither who who's whoever whole whom whose why will willing wish with within without won't wonder would would wouldn't x y yes yet you you'd you'll you're you've your yours yourself yourselves z zero";
}
