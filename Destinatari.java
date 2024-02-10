package mua;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Classe concreta immutabile che rappresenta dei destinatari di un {@link Messaggio}.
 * 
 * <p> I destinatari sono un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato da un <em>elenco di indirizzi dei destinatari</em> ({@link Indirizzo}).
 * 
 * <p> E' possibile accedere al contenuto dei destinatari tramite iterazione sui suoi indirizzi ({@link Indirizzo}).
 */
public class Destinatari extends Intestazione implements Iterable<Indirizzo>{
    /**
     * Un elenco di indirizzi dei destinatari
     */
    private final List<Indirizzo> destinatari; 

    // RI: destinatari non null e non contiene elementi null
    // AF: i destinatari sono un elenco di indirizzi di recapito per un messaggio

    /**
     * Costruisce dei destinatari da un elenco di indirizzi di essi
     * 
     * @param destinatari un elenco di indirizzi dei destinatari 
     * @throws NullPointerException se destinatari e' {@code null} o contiene {@code null}
     */
    public Destinatari(final List<Indirizzo> destinatari) throws NullPointerException {
        super("To");
        Objects.requireNonNull(destinatari, "L'elenco dei destinatari non puo' essere null");
        for (Indirizzo indirizzo : destinatari){
            Objects.requireNonNull(indirizzo, "L'elenco dei destinatari non puo' contenere un indirizzo null");
        }
        this.destinatari = List.copyOf(destinatari); // per copiarlo rendendolo anche immutabile
    }

    @Override
    public String codifica() {
        StringBuilder res = new StringBuilder();
        res.append("To: ");

        for (var i = 0; i < destinatari.size()-1; i++){
            String encode = destinatari.get(i).toString() + ", ";
            res.append(encode);
        }
        res.append(destinatari.get(destinatari.size()-1).toString());
        return res.toString();
    }    

    /**
     * Restituisce le email dei destinatari su ogni riga
     * 
     * @return le email dei destinatari uno per riga
     */
    public String emails(){
        StringBuilder sb = new StringBuilder();
        for (Indirizzo indirizzo : destinatari){
            sb.append(Indirizzo.email(indirizzo) + "\n");
        }
        return sb.toString();
    }

    /**
     * Restituisce la decodifica di una stringa rappresentante la codifica dei destinatari
     * 
     * @param codifica la codifica dei destinatari
     * @return un oggetto di tipo {@link Destinatari}
     * @throws NullPointerException se codifica {@code null}
     * @throws IllegalArgumentException se codifica vuota o non ben formato
     */
    public static Destinatari decodifica(final String codifica)throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(codifica, "Non e' possibile decodificare una codifica dei destinatari null").isEmpty())
            throw new IllegalArgumentException("Non e' possibile decodificare una codifica dei destinatari vuota");

        final String[] destinatari = codifica.replace("To: ", "").split(", ");
        final List<Indirizzo> indirizzi = new ArrayList<Indirizzo>();
        for (String destinatario : destinatari)
            indirizzi.add(Indirizzo.decodifica(destinatario));
        return new Destinatari(indirizzi);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Indirizzo indirizzo : destinatari){
            sb.append(indirizzo.toString() + "\n");
        }
        return sb.toString();
    }

    @Override
    public Iterator<Indirizzo> iterator() {
        return Collections.unmodifiableList(destinatari).iterator();
    }
}
