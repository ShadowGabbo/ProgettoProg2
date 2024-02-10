package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.Storage;
import utils.Storage.Box;
import utils.Storage.Box.Entry;
import utils.UITable;

/**
 * Classe concreta mutabile che rappresenta una collezione di mailbox {@link Mailbox}.
 * 
 * <p> Lo stato e' rappresentato da un <em>elenco di mailbox</em> {@link Mailbox} e da il nome di una <em>mailbox selezionata</em>.
 * 
 * <p> E' possibile costruire una mua a partire da una baseDirectory del disco
 * 
 * <p> Inoltre data una mua e' possibile:
 * <ul>
 *  <li> elencare le mailbox (in ordine alfabetico crescente del loro nome)
 *  <li> selezionare una delle mailbox e, una volta fatto:
 * <ul>
 *  <li> elencare i messaggi che contiene (in ordine cronologico decrescente)
 *  <li> visualizzare uno specifico messaggio
 *  <li> cancellare uno specifico messaggio
 *  <li> comporre un nuovo messaggio (ed aggiungerlo alla mailbox)
 * </ul>
 * </ul>
 * 
 * <p> E' possibile accedere alle mailbox per ordine alfabetico tramite iterazione {@link #iterator()}. 
 */
public class Mua implements Iterable<Mailbox>{
    /**
     * Una collezione di mailbox
     */
    private final List<Mailbox> mua;

    /**
     * Il nome della mailbox selezionata su cui si eseguono alcune operazioni (eventualmente vuota)
     */
    private String mailboxSelected = ""; 

    /**
     * La base directory su disco 
     */
    private final String baseDir;

    // RI: mua non null e non contiene mailbox null che non contiene messaggi null, mailboxSelected e baseDir non null,
    //     baseDir non null e ben formata, il contenuto di mua deve essere coerente con quello di storage(basedir),
    //     ad ogni box corrisponde una mailbox e ad ogni entry corrispondera' un messaggio,
    //     non ci possono essere due mailbox con lo stesso nome e non ci possono essere due mailbox senza nome
    // AF: una mua e' una collezione di mailbox creata a partire da una baseDir, inoltre per effettuare alcune operazioni e' necessaria avere una mailboxSelected selezionata

    /**
     * Costruisce una mua ossia un'elenco di mailbox a partire da una base directory del disco
     * 
     * @param baseDir la base directory del disco
     * @throws NullPointerException se baseDir e' null
     * @throws IllegalArgumentException se baseDir e' vuota
     */
    public Mua(final String baseDir) throws NullPointerException, IllegalArgumentException{
        if (Objects.requireNonNull(baseDir, "La base directory non puo' essere null").isEmpty())
            throw new IllegalArgumentException("La base directory non puo' essere vuota");
        
        final List<Mailbox> mua = new ArrayList<Mailbox>();

        final Storage s = new Storage(baseDir);
        final List<Box> boxes = s.boxes();

        for (final Box box : boxes) {
            mua.add(new Mailbox(box));
        }

        this.mua = new ArrayList<Mailbox>(mua);
        this.baseDir = baseDir;
    }

    /**
     * Restituisce l'indice della mailboxSelezionata
     * 
     * @return l'indice partendo da 0
     * @throws NoSuchElementException se nessuna mailbox e' stata selezionata o e' presente con il nome selezionato
     */
    private int getIndexMailbox()throws NoSuchElementException{
        if (mailboxSelected.equals(""))
            throw new NoSuchElementException("Nessuna mailbox selezionata");
        
        int counter = 0;
        for (Mailbox mailbox : mua){
            if (mailbox.getNome().equals(mailboxSelected)) return counter;
            counter++; 
        }
        throw new NoSuchElementException("Nessuna mailbox con nome: " + mailboxSelected);
    }

    /**
     * Restituisce il nome della mailbox selezionata
     * 
     * @return il nome della mailbox selezionata
     */
    public String getMailboxSelected() {
        return mailboxSelected;
    }

     /**
     * Restituisce il prompt da visualizzare a schermo puo' essere:
     * <ul>
     *  <li> "[*] > " -> se nessuna mailbox e' selezionata
     *  <li> "[mailbox] > " -> se e' stata selezionata mailbox 
     * </ul>
     * 
     * @return la stringa da visualizzare
     */
    public String prompt(){
        return mailboxSelected.equals("")? "[*] > " : String.format("[%s] > ", mailboxSelected);
    }

    /**
     * Seleziona una mailbox dato il suo numero
     * 
     * @param indexMailbox l'indice della mailbox selezionata che parte da 1
     * @throws IndexOutOfBoundsException se indexMailbox eccede i limiti consentiti
     */
    public void selectMailbox(final int indexMailbox) throws IndexOutOfBoundsException{
        if (indexMailbox < 1 || indexMailbox > mua.size())
            throw new IndexOutOfBoundsException(String.format("Mailbox numero %d non esiste\n", indexMailbox));
        this.mailboxSelected = mua.get(indexMailbox-1).getNome();
    }

    /**
     * Legge un messaggio della mailbox selezionata
     * 
     * @param indexMessage il numero del messaggio da leggere
     * @return la lettura del messaggio in forma testuale
     * @throws NoSuchElementException se nessuna mailbox e' stata selezionata
     * @throws IndexOutOfBoundsException se il numero del messaggio da leggere eccede il bound
     */
    public String leggiMessaggio(final int indexMessage) throws NoSuchElementException, IndexOutOfBoundsException{
        final Mailbox mailbox = getMailbox();    
        return mailbox.leggiMessaggio(indexMessage).toString();
    }


    /**
     * Restituisce la mailbox selezionata
     * 
     * @return la mailbox selezionata {@link Mailbox}
     * @throws NoSuchElementException se nessuna mailbox e' stata selezionata
      */
    public Mailbox getMailbox() throws NoSuchElementException{
        final int index = getIndexMailbox();
        return new Mailbox(mua.get(index));
    }

    /**
     * Salva un messaggio nella mua e su disco
     * 
     * @param messaggio il messaggio da salvare
     * @throws NullPointerException se messaggio null
     * @throws NoSuchElementException se non trova nessuna box con il nome della mailbox selezionata
     */
    public void salvaMessaggio(final Messaggio messaggio)throws NullPointerException, NoSuchElementException{
        Objects.requireNonNull(messaggio, "Il messaggio da salvare non puo' essere null");
        final String messaggio_codificato = messaggio.codifica();
        boolean boxTrovata = false;

        final Storage s = new Storage(baseDir);
        final List<Box> boxes = s.boxes();
        for (final Box box : boxes) {
            if (box.toString().equals(mailboxSelected)){
                box.entry(ASCIICharSequence.of(messaggio_codificato));
                mua.get(getIndexMailbox()).componiMessaggio(messaggio);
                boxTrovata = true;
                break;
            }    
        }

        if (boxTrovata == false){
            throw new NoSuchElementException("Nessuna box trovata sul disco con nome : " + mailboxSelected);
        }
    }

    /**
     * Cancella il messaggio nella mailbox selezionata dato il numero del messaggio
     * 
     * @param index il numero di messaggio da cancellare
     * @throws IndexOutOfBoundsException se l'index eccede i bound
     */
    public void cancellaMessaggio(final int index) throws IndexOutOfBoundsException{
        if (index < 1 || index > getMailbox().numeroMessaggi())
            throw new IndexOutOfBoundsException(String.format("Indice errato, la mailbox %s possiede %d messaggi, selezionato %d\n", mailboxSelected, getMailbox().numeroMessaggi(), index));

        final Storage s = new Storage(baseDir);
        final List<Box> boxes = s.boxes();
        final String messaggioCodificato = getMailbox().leggiMessaggio(index).codifica();

        for (final Box box : boxes) {
            if (box.toString().equals(mailboxSelected)){
                List<Entry> entries = box.entries();
                for (Entry e : entries){
                    if (e.content().toString().equals(messaggioCodificato)){
                        e.delete(); 
                        mua.get(getIndexMailbox()).cancellaMessaggio(index); 
                    }
                }
            }   
        }
    }

    @Override
    public String toString() {
        final List<List<String>> rows = new ArrayList<>();

        for (Mailbox m : mua){
            final List<String> row = new ArrayList<String>();
            final String numeroMessaggi = String.valueOf(m.numeroMessaggi());
            final String nomeMailbox = m.getNome();
            row.add(nomeMailbox);
            row.add(numeroMessaggi);
            rows.add(row);
        }
        return UITable.table(
            List.of("Mailbox", "# messages"), // headers
            rows,
            true, false
        );
    }

    @Override
    public Iterator<Mailbox> iterator() {
        return Collections.unmodifiableList(mua).iterator();
    }
}
