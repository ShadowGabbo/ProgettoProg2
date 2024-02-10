package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import utils.UITable;
import utils.ASCIICharSequence;
import utils.EntryEncoding;
import utils.Fragment;
import utils.Storage.Box;
import utils.Storage.Box.Entry;

/**
 * Classe concreta mutabile che rappresenta una collezione di messaggi {@link Messaggio}.
 * 
 * <p> Lo stato e' rappresentato da un <em>elenco di messaggi</em> {@link Messaggio}.
 * 
 * <p> E' possibile costruire una mailbox a partire da:
 * <ul>
 *  <li> un'altra mailbox (costruttore copia)
 *  <li> una box
 * </ul>
 * 
 * <p> Inoltre data una mailbox e' possibile:
 * <ul>
 *  <li> visualizzare un messaggio
 *  <li> comporre un nuovo messaggio e salvarlo (in memoria non su disco)
 *  <li> cancecellare messaggio (in memoria non su disco)
 * </ul>
 * 
 * <p> Le mailbox sono in ordine alfabetico crescente del loro nome {@link #compareTo(Mailbox)}
 * 
 * <p> E' possibile accedere al contenuto dei messaggi in ordine di data ({@link Messaggio}) tramite iterazione {@link #iterator()}. 
 */
public class Mailbox implements Iterable<Messaggio>, Comparable<Mailbox>{
    /**
     * La mailbox: collezione di messaggi
     */
    private final List<Messaggio> mailbox;
    
    /**
     * Il nome della mailbox
     */
    private final String nome;

    // RI: mailbox non null e non contiene oggetti null, nome non null eventualmente vuoto
    //     il nome deve corrispondere al nome di una box, mailbox deve corrispondere 
    //     al contenuto della box con quel nome (garantito in creazione)
    // AF: una mailbox e' una collezione di messaggi inoltre possiede un nome per essere 
    //     idenficato che puo' essere vuoto e unico

    /**
     * Costruisce una mailbox copiando i messaggi di una mailbox data
     * 
     * @param mailbox la mailbox da copiare
     * @throws NullPointerException se mailbox e'{@code null} o contiene messaggi {@code null}, oppure se il nome della mailbox e' null
     */
    public Mailbox(final Mailbox mailbox) throws NullPointerException{
        Objects.requireNonNull(mailbox, "La mailbox da copiare non puo' essere null");
        for (Messaggio messaggio : mailbox){
            Objects.requireNonNull(messaggio, "La mailbox non puo' contentere messaggi null");
        }
        Objects.requireNonNull(mailbox.getNome(), "Il nome della mailbox non puo' essere nullo");

        final List<Messaggio> copia = new ArrayList<Messaggio>();
        for (Messaggio messaggio : mailbox){
            copia.add(messaggio);
        }
        this.mailbox = new ArrayList<>(copia);
        this.nome = mailbox.getNome();
        ordinaMessaggi();
    }

    /**
     * Costruisce una mailbox a partire da una box (ogni messaggio corrisponde ad una {@code Entry})
     * 
     * @param box la box da copiare
     * @throws NullPointerException se box null
     */
    public Mailbox(final Box box)throws NullPointerException{
        Objects.requireNonNull(box, "La box non puo' essere null");
        final List<Entry> entrys = box.entries();
        final List<Messaggio> messaggi = new ArrayList<Messaggio>();
            for (Entry entry : entrys){
                final ASCIICharSequence sequence = entry.content();
                final List<Fragment> fragments = EntryEncoding.decode(sequence);
                final Messaggio messaggio = Messaggio.fromFragments(fragments);
                messaggi.add(messaggio);
            }
        this.mailbox = new ArrayList<>(messaggi);
        this.nome = box.toString();
        ordinaMessaggi();
    }

    /**
     * Restituisce il nome della mailbox
     * 
     * @return la mailbox
     */
    public String getNome() {
        return nome;
    }

    /**
     * Restitisce il messaggio num-esimo
     * 
     * @param num il numero del messaggio da leggere
     * @return il messaggio da leggere
     * @throws IndexOutOfBoundsException se il num eccede il bound
     */
    public Messaggio leggiMessaggio(final int num) throws IndexOutOfBoundsException {
        if (num < 1 || num > this.mailbox.size()) throw new IndexOutOfBoundsException(String.format("Il messaggio numero: %d non esiste", num));
        return new Messaggio(this.mailbox.get(num-1)); 
    }

    /**
     * Cancella il messaggio num-esimo
     * 
     * Modifica this
     * 
     * @param num il numero del messaggio da eliminare
     * @throws IndexOutOfBoundsException se il num eccede il bound
     */
    public void cancellaMessaggio(final int num)throws IndexOutOfBoundsException{
        if (num < 1 || num > this.mailbox.size()) throw new IndexOutOfBoundsException(String.format("Il messaggio numero: %d non esiste", num));
        this.mailbox.remove(this.mailbox.get(num-1));
    }

    /**
     * Aggiunge un nuovo messaggio alla mailbox
     * 
     * Modifica this e ordina i messaggi
     * 
     * @param messaggio il messaggio da aggiungere alla mailbox
     * @throws NullPointerException se il messaggio da aggiungere e' null
     */
    public void componiMessaggio(final Messaggio messaggio) throws NullPointerException{
        Objects.requireNonNull(messaggio, "Il messaggio da comporre non puo' essere nullo");
        this.mailbox.add(messaggio);
        ordinaMessaggi();
    }

    /**
     * Restituisce il numeri di messaggi della mailbox
     * 
     * @return il numero di messaggi
     */
    public int numeroMessaggi(){
        return this.mailbox.size();
    }

    /**
     * Ordina la mailbox per data decrescente dei messaggi
     * 
     * Modifica this
     */
    private void ordinaMessaggi(){
        mailbox.sort(null);
    }

    @Override
    public int compareTo(final Mailbox other) {
        return nome.compareTo(other.nome);
    } 

    @Override
    public Iterator<Messaggio> iterator() {
        return Collections.unmodifiableList(mailbox).iterator();
    }

    @Override
    public String toString() {
        final List<List<String>> rows = new ArrayList<>();
        for (Messaggio message : mailbox){
            final String from = message.mittente().email();
            final String data = message.data().toString();
            final String to = message.destinatari().emails();
            final String subject = message.oggetto().getOggetto();
            rows.add(List.of(data, from , to , subject));
        }

        return UITable.table(
            List.of("Date" , "From" , "To"  , "Subject"),
            rows,
            true, true
        );
    }
}
