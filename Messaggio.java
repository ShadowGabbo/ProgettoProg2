package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.Base64Encoding;
import utils.EntryEncoding;
import utils.Fragment;
import utils.UICard;

/**
 * Classe concreta immutabile che rappresenta un messaggio costituito da una o più parti {@link Messaggio.Parte}.
 * 
 * <p> Lo stato e' rappresentato da un <em>elenco di parti</em> {@link Messaggio.Parte}.
 * 
 * <p> E' possibile attraverso dei metodi di produzione statici avere accesso alla intestazioni essenziali per un messaggio quali:
 * <ul>
 *  <li> il mittente del messaggio: {@link #mittente()}
 *  <li> i destinatari del messaggio: {@link #destinatari()}
 *  <li> la data associata al messaggio: {@link #data()}
 *  <li> l'oggetto del messaggio: {@link #oggetto()}
 * </ul>
 * 
 * <p> Inoltre e' possibile costruire un messaggio da:
 * <ul>
 *  <li> se singlepart dalle 4 intestazioni obbligatorie un corpo e un flag
 *  <li> se multipart dalle 4 intestazioni obbligatorie e i due corpi (text/plain e text/html)
 *  <li> un altro messaggio (costruttore copia)
 *  <li> da una lista di fragments (metodo statico)
 * </ul>
 * 
 * <p> I messaggi sono naturalmente ordinati per data decrescente {@link #compareTo(Messaggio)}
 * 
 * <p> E' possibile accedere al contenuto delle parti ({@link Messaggio.Parte}) tramite iterazione {@link #iterator()}. 
 */
public class Messaggio implements Comparable<Messaggio>, Iterable<Messaggio.Parte>{
    /**
     * Classe interna immutabile che rappresenta una parte di un messaggio sia esso singlepart che multipart.
     * 
     * <p> Lo stato e' rappresentato da un <em>elenco di intestazioni</em> ({@link Intestazione}) e un corpo.
     *
     * <p> Gli oggetti di questa classe sono istanziati esclusivamente dai costruttori di {@link Messaggio}.
     * 
     * <p> Inoltre e' garantito in fase di costruzione l'ordine delle intestazioni (come da specifica)
     */
    public class Parte{
        /**
         * Le intestazioni della parte del messaggio
         */
        private final List<Intestazione> intestazioni;
        
        /**
         * Il corpo della parte del messaggio
         */
        private final String corpo;

        // RI: intestazioni non e' null e non contiene null, corpo non null e non vuoto, intestazioni ordinate, intestazioni deve contentere almeno un elemento
        // AF: una parte e' un elenco di intestazioni e un corpo, la prima parte del messaggio conterra' sicuramente
        //     almeno le 4 intestazioni obbligatorie (mittente, destinatari, data, oggetto);

        /**
         * Costruisce una parte dato un elenco di intestazioni ed un corpo 
         * 
         * @param intestazioni l'elenco di intestazioni
         * @param corpo il corpo della parte
         * @throws NullPointerException se intestazioni e' null o contiene null, o corpo null
         * @throws IllegalArgumentException se corpo e' vuoto
         */
        private Parte(final List<Intestazione> intestazioni, final String corpo) throws NullPointerException, IllegalArgumentException {
            Objects.requireNonNull(intestazioni, "Le intestazioni non possono essere nulle");
            for (Intestazione intestazione: intestazioni)
                Objects.requireNonNull(intestazione, "Errore nella creazione della parte del messaggio -> intestazione nulla"); 
            
            if (Objects.requireNonNull(corpo, "Il corpo della parte non puo' essere nullo").isEmpty())
                throw new IllegalArgumentException("Il corpo della parte non puo' essere vuoto");

            this.intestazioni = List.copyOf(intestazioni); // per copiarlo rendendolo anche immutabile 
            this.corpo = corpo;
        }

        /**
         * Restituisce il corpo della parte del messaggio
         * 
         * @return il corpo della parte del messaggio
         */
        private String getCorpo() {
            return corpo;
        }

        /**
         * Restituisce le intestazioni della parte del messaggio
         * 
         * @return le intestazioni
         */
        private List<Intestazione> getIntestazioni() {
            return List.copyOf(intestazioni); // per copiarlo rendendolo anche immutabile 
        }

        /**
         * Restituisce il content type della parte
         * 
         * @return il content type di tipo {@link ContentType}
         * @throws NoSuchElementException se content type non esiste
         */
        public ContentType getContentType() throws NoSuchElementException{
            for (Intestazione i : intestazioni){
                if (i instanceof ContentType) return new ContentType((ContentType)i);
            }
            throw new NoSuchElementException("Nessun content type nella parte");
        }

        /**
         * Codifica una parte del messaggio, sia le intestazioni che il corpo
         * 
         * @return la codifica della parte del messaggio
         */
        public String codifica(){
            return codificaIntestazioni() + "\n" + codificaCorpo();
        }

        /**
         * Codifica le intestazioni della parte
         * 
         * @return la codifica delle intestazioni
         */
        private String codificaIntestazioni(){
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < intestazioni.size(); i++){
                sb.append(intestazioni.get(i).codifica() + "\n");
            }
            return sb.toString();
        }

        /**
         * Informa se il corpo e' testo o html
         * 
         * @return true se html, false se testo
         */
        private boolean isCorpoHtml(){
            for (int i = 0; i < intestazioni.size(); i++){
                if (intestazioni.get(i) instanceof ContentType){
                    ContentType content = (ContentType)intestazioni.get(i);
                    return content.getContent().equals("text/html");
                }
            }
            return false;
        }

        /**
         * Codifica il corpo della parte
         * 
         * @return la codifica del corpo
         */
        private String codificaCorpo(){
            if (isCorpoHtml()){
                return Base64Encoding.encode(String.valueOf(corpo));
            }else{
                if (ASCIICharSequence.isAscii(String.valueOf(corpo))){
                    // oggetto contiene solo caratteri ascii
                    return String.valueOf(corpo);
                }else{
                    // oggetto contiene caratteri non ascii
                    return Base64Encoding.encode(String.valueOf(corpo));
                }
            }
        }

        /**
         * Decodifica il corpo della parte
         * 
         * @param codifica la codifica della parte del corpo
         * @return la decodifica del corpo
         * @throws IllegalArgumentException se codifica null
         * @throws NullPointerException se codifica vuoto
         */
        public static String decodificaCorpo(final String codifica) throws IllegalArgumentException, NullPointerException{
            if (Objects.requireNonNull(codifica, "Non e' possibile decodificare un corpo null").isEmpty())
                throw new IllegalArgumentException("Non e' possibile decodificare un corpo vuoto");
            
                if (codifica.startsWith("PGh0bWw+")){
                    // e' stato utilizzato encode per codifica 
                    return Base64Encoding.decode(ASCIICharSequence.of(codifica));
                }else{
                    // la codifica/encode conteneva solo caratteri ascii
                    return codifica;
                }
        }
    }

    /**
     * Le parti di un messaggio di cui e' composto
     */
    private final List<Parte> parti;

    // RI: parti non null e non contiene null, inoltre deve contenere almeno un elemento
    // AF: un messaggio e' una collezione di parti, se e' formato da una sola parte sara' chiamato singlepart, multipart viceversa
    
    /**
     * Costruisce un messaggio singlepart di testo o html date le intestazioni obbligatorie il corpo ed un flag
     * 
     * @param mittente il mittente 
     * @param destinatari i destinatari
     * @param oggetto l'oggetto del messaggio
     * @param data la data di invio del messaggio
     * @param corpo il corpo del messaggio
     * @param isCorpoHtml flag true se corpo e' html, false se e' testo
     * @throws NullPointerException se mittente destinatari, oggetto, data o corpo sono null
     * @throws IllegalArgumentException se corpo vuoto
     */
    public Messaggio(final Mittente mittente, final Destinatari destinatari, final Oggetto oggetto, final Data data, final String corpo, final boolean isCorpoHtml) throws NullPointerException, IllegalArgumentException{
        Objects.requireNonNull(mittente, "Il mittente di un messaggio non puo' essere null");
        Objects.requireNonNull(destinatari, "I destinatari di un messaggio non possono essere null");
        Objects.requireNonNull(oggetto, "L'oggetto di un messaggio non puo' essere null");
        Objects.requireNonNull(data, "La data di un messaggio non puo' essere null");
        if (Objects.requireNonNull(corpo, "Il corpo di un messaggio non puo' essere null").isEmpty())
            throw new IllegalArgumentException("Il corpo di un messaggio non puo' essere vuoto");

        final List<Intestazione> intestazioni = new ArrayList<>();
        intestazioni.add(mittente);
        intestazioni.add(destinatari);
        intestazioni.add(oggetto);
        intestazioni.add(data);

        if (isCorpoHtml){
            // costruisco un messaggio singlepart con corpo text/html
            final Intestazione contentType = new ContentType("text/html", "utf-8");
            final Intestazione contentTransferEncoding = new ContentTransferEncoding("base64");
            intestazioni.add(contentType);
            intestazioni.add(contentTransferEncoding);
        }else{
            // costruisco un messaggio singlepart con corpo text/plain
            if (ASCIICharSequence.isAscii(String.valueOf(corpo))){
                final Intestazione contentType = new ContentType("text/plain", "us-ascii");
                intestazioni.add(contentType);
            }else{
                final Intestazione contentType = new ContentType("text/plain", "utf-8");
                final Intestazione contentTransferEncoding = new ContentTransferEncoding("base64");
                intestazioni.add(contentType);
                intestazioni.add(contentTransferEncoding);
            }
        }
        final Parte parte = new Parte(intestazioni, corpo);
        this.parti = new ArrayList<Parte>(List.of(parte));
    }

    /**
     * Costruisco un messaggio multipart date le intestazioni obbligatorie e i corpi
     * 
     * @param mittente il mittente
     * @param destinatari il destinatario
     * @param oggetto l'oggetto del messaggio
     * @param data la data
     * @param corpoText il corpo testo
     * @param corpoHtml il corpo html
     * @throws NullPointerException se mittente destinatari, oggetto, data, corpoText o corpoHtml sono null
     * @throws IllegalArgumentException se corpoText o corpoHtml sono vuoti
     */
    public Messaggio(final Mittente mittente, final Destinatari destinatari, final Oggetto oggetto, final Data data, final String corpoText, final String corpoHtml) throws NullPointerException, IllegalArgumentException{
        Objects.requireNonNull(mittente, "Il mittente di un messaggio non puo' essere null");
        Objects.requireNonNull(destinatari, "I destinatari di un messaggio non possono essere null");
        Objects.requireNonNull(oggetto, "L'oggetto di un messaggio non puo' essere null");
        Objects.requireNonNull(data, "La data di un messaggio non puo' essere null");
        if (Objects.requireNonNull(corpoText, "Il corpo testo di un messaggio non puo' essere null").isEmpty())
            throw new IllegalArgumentException("Il corpo di un messaggio non puo' essere vuoto");
        if (Objects.requireNonNull(corpoHtml, "Il corpo html di un messaggio non puo' essere null").isEmpty())
            throw new IllegalArgumentException("Il corpo di un messaggio non puo' essere vuoto");

        final List<Intestazione> intestazioni1 = new ArrayList<>();
        final List<Intestazione> intestazioni2 = new ArrayList<>();
        final List<Intestazione> intestazioni3 = new ArrayList<>();
        intestazioni1.add(mittente);
        intestazioni1.add(destinatari);
        intestazioni1.add(oggetto);
        intestazioni1.add(data);

        // costruisco la prima parte di un messaggio multipart 
        final Intestazione mimeVersion = new MimeVersion("1.0");
        final Intestazione contentType1 = new ContentType("multipart/alternative", "");
        intestazioni1.add(mimeVersion);
        intestazioni1.add(contentType1);
        final String corpo1 = "This is a message with multiple parts in MIME format.";
        final Parte parte1 = new Parte(intestazioni1, corpo1);

        // costruisco la seconda parte
        if (ASCIICharSequence.isAscii(String.valueOf(corpoText))){
            final Intestazione contentType2 = new ContentType("text/plain", "us-ascii");
            intestazioni2.add(contentType2);
            }else{
            final Intestazione contentType2 = new ContentType("text/plain", "utf-8");
            final Intestazione contentTransferEncoding2 = new ContentTransferEncoding("base64");
            intestazioni2.add(contentType2);
            intestazioni2.add(contentTransferEncoding2);
        }
        final Parte parte2 = new Parte(intestazioni2, corpoText);

        // costruisco la terza parte
        final Intestazione contentType3 = new ContentType("text/html", "utf-8");
        final Intestazione contentTransferEncoding3 = new ContentTransferEncoding("base64");
        intestazioni3.add(contentType3);
        intestazioni3.add(contentTransferEncoding3);
        final Parte parte3 = new Parte(intestazioni3, corpoHtml);

        // le aggiungo a this
        this.parti = new ArrayList<Parte>(List.of(parte1, parte2, parte3));
    }

    /**
     * Costruisce un messaggio copiando le parti di un messaggio dato
     * 
     * @param messaggio il messaggio da copiare
     * @throws NullPointerException se messaggio null
     */
    public Messaggio(final Messaggio messaggio) throws NullPointerException{
        Objects.requireNonNull(messaggio, "Il messaggio da copiare non puo' essere null");
        final List<Parte> copia = new ArrayList<Parte>();
        for (final Parte parte : messaggio){
            copia.add(parte);
        }
        this.parti = new ArrayList<>(copia);
    }
    
    /**
     * Costruisce un messaggio dati i fragments che lo compongono
     * 
     * @param fragments i fragments
     * @return un oggetto di tipo messaggio
     * @throws NullPointerException se fragments null o contengono null
     */
    public static Messaggio fromFragments(final List<Fragment> fragments)throws NullPointerException{
        Objects.requireNonNull(fragments, "I fragments non possono essere nulli");
        for (final Fragment fragment: fragments)
            Objects.requireNonNull(fragment, "Il fragment non puo' essere nullo"); 

        final List<Intestazione> intestazioni = new ArrayList<>();
        final List<String> corpi = new ArrayList<String>();
        boolean flag = false;

        for (final Fragment fragment : fragments) {
            for (final List<ASCIICharSequence> rawHeader : fragment.rawHeaders()){
                final String type = rawHeader.get(0).toString();
                final String value = rawHeader.get(1).toString();
                
                switch (type) {
                case "from":
                    final Intestazione mittente = Mittente.decodifica(value);
                    intestazioni.add(mittente);
                    break;
                case "to":
                    final Intestazione destinatari = Destinatari.decodifica(value);
                    intestazioni.add(destinatari);
                    break;
                case "subject":
                    final Intestazione oggetto = Oggetto.decodifica(value);
                    intestazioni.add(oggetto);
                    break;
                case "date":
                    final Intestazione date = Data.decodifica(value);
                    intestazioni.add(date);
                    break;
                case "content-type":
                    ContentType contentType = ContentType.decodifica(value);
                    switch (contentType.getContent()) {
                    case "text/html":
                        flag = true;
                    default:
                        break;
                    }
                }
            }
            final String corpo = Parte.decodificaCorpo(fragment.rawBody().toString());
            corpi.add(corpo);
        }

        final Messaggio m;
        if (corpi.size()!=1){
            // e' multipart
            m = new Messaggio((Mittente)intestazioni.get(0), (Destinatari)intestazioni.get(1), (Oggetto)intestazioni.get(2), (Data)intestazioni.get(3), corpi.get(1), corpi.get(2));
        }else{
            if (flag){
                // e' html
                m = new Messaggio((Mittente)intestazioni.get(0), (Destinatari)intestazioni.get(1), (Oggetto)intestazioni.get(2), (Data)intestazioni.get(3), corpi.get(0), true);
            }else{
                // e' testo
                m = new Messaggio((Mittente)intestazioni.get(0), (Destinatari)intestazioni.get(1), (Oggetto)intestazioni.get(2), (Data)intestazioni.get(3), corpi.get(0), false);
            }
        } 
        return m;
    }

    /**
     * Controlla se il messaggio e' multipart o singlepart
     * 
     * @return true se multipart, false altrimenti
     */
    public boolean isMultiPart(){
        return this.parti.size() != 1;
    }

    /**
     * Effettua la codifica di un messaggio
     * 
     * @return la codifica del messaggio
     */
    public String codifica(){
        if (isMultiPart()==false){
            return parti.get(0).codifica();
        }else{
            final StringBuilder sb = new StringBuilder();
            for (int i=0; i<parti.size()-1; i++){
                sb.append(parti.get(i).codifica() + "\n--frontier\n");
            }
            sb.append(parti.get(parti.size()-1).codifica() + "\n--frontier--\n");
            return sb.toString();
        }
    }

    /**
     * Metodo statico di utilita' che permette di decodificare in fragments una codifica di un messaggio
     * 
     * @param codifica la codifica del messaggio
     * @return la sua decodifica in fragments
     */
    public static String decodifica(final String codifica){
        String res = "";
        final ASCIICharSequence sequence = ASCIICharSequence.of(codifica);
        final List<Fragment> fragments = EntryEncoding.decode(sequence);
        for (final Fragment fragment : fragments) {
            res += "Fragment\n\tRaw headers:\n";
            for (List<ASCIICharSequence> rawHeader : fragment.rawHeaders())
                res +="\t\tRaw type = " + rawHeader.get(0) + ", value = " + rawHeader.get(1) + "\n";
            res +="\tRaw body: \n\t\t" + fragment.rawBody().toString().split("\n")[0] + "\n";
        }
        return res;
    }

    /**
     * Restituisce la data del messaggio
     * 
     * @return la data del messaggio
     * @throws NoSuchElementException se data non presente nel messaggio
     */
    public Data data() throws NoSuchElementException{
        final List<Intestazione> prima_parte = parti.get(0).getIntestazioni();
        for (int i = 0; i < prima_parte.size(); i++){
            if (prima_parte.get(i) instanceof Data){
                return (Data)prima_parte.get(i);
            }
        }
        throw new NoSuchElementException("Non e' presente il mittente del messaggio");
    }

    /**
     * Restituisce gli indirizzi dei del messaggio
     * 
     * @return gli indirizzi dei destinatari del messaggio
     * @throws NoSuchElementException se destinatari non presente nel messaggio
     */
    public Destinatari destinatari() throws NoSuchElementException{
        final List<Intestazione> prima_parte = parti.get(0).getIntestazioni();
        for (int i = 0; i < prima_parte.size(); i++){
            if (prima_parte.get(i) instanceof Destinatari){
                return (Destinatari)prima_parte.get(i);
            }
        }
        throw new NoSuchElementException("Non sono presenti destinatari nel messaggio");
    }

    /**
     * Restituisce l'oggetto del messaggio
     * 
     * @return l'oggetto del messaggio
     * @throws NoSuchElementException se oggetto non presente nel messaggio
     */
    public Oggetto oggetto() throws NoSuchElementException{
        final List<Intestazione> prima_parte = parti.get(0).getIntestazioni();
        for (int i = 0; i < prima_parte.size(); i++){
            if (prima_parte.get(i) instanceof Oggetto){
                return (Oggetto)prima_parte.get(i);
            }
        }
        throw new NoSuchElementException("Non e' presente l'oggetto nel messaggio");
    }


    /**
     * Restituisce il mittente del messaggio
     * 
     * @return il mittente del messaggio
     * @throws NoSuchElementException se mittente non presente nel messaggio
     */
    public Mittente mittente() throws NoSuchElementException{
        final List<Intestazione> prima_parte = parti.get(0).getIntestazioni();
        for (int i = 0; i < prima_parte.size(); i++){
            if (prima_parte.get(i) instanceof Mittente){
                return (Mittente)prima_parte.get(i);
            }
        }
        throw new NoSuchElementException("Non sono presenti date nel messaggio");
    }

    @Override
    public int compareTo(final Messaggio other) {
        if (this.data().getDate().isAfter(other.data().getDate())) return -1;
        else if (this.data().getDate().isBefore(other.data().getDate()))return 1;
        return 0;
    }

    @Override
    public Iterator<Parte> iterator() {
        return Collections.unmodifiableList(parti).iterator();
    }

    @Override
    public String toString() {
        final List<String> headers = new ArrayList<>(List.of("From" , "To" , "Subject", "Date"));
        final List<String> values = new ArrayList<>(List.of(
            mittente().getMittente().toString(), 
            destinatari().toString(),
            oggetto().getOggetto(),
            data().getDate().toString()
        ));

        for (Parte parte: parti){
            headers.add(parte.getContentType().toString());
            values.add(parte.getCorpo());
        }

        return UICard.card(headers, values);
    }
}