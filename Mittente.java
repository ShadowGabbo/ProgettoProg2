package mua;

import java.util.Objects;

/**
 * Classe concreta immutabile che rappresenta un mittente di un {@link Messaggio}.
 * 
 * <p> Un mittente e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato dall'<em>indirizzo del mittente</em> ({@link Indirizzo}).
 */
public class Mittente extends Intestazione{
    /**
     * L'indirizzo del mittente
     */
    private final Indirizzo mittente;

    // RI: mittente non null
    // AF: un mittente di un messaggio e' rappresentato dal suo indirizzo 

    /**
     * Costruisce un mittente dato il suo indirizzo
     * 
     * @param mittente l'indirizzo del mittente
     * @throws NullPointerException se mittente {@code null}
     */
    public Mittente(Indirizzo mittente) throws NullPointerException{
        super("From");
        this.mittente = Objects.requireNonNull(mittente, "L'indirizzo del mittente non puo' essere nullo");
    }

    /**
     * Restituisce l'indirizzo del mittente
     * 
     * @return l'indirizzo del mittente 
     */
    public Indirizzo getMittente() {
        return mittente;
    }

    /**
     * Restituisce l'email del mittente
     * 
     * @return l'email del mittente
     */
    public String email(){
        return Indirizzo.email(mittente);
    }

    @Override
    public String codifica() {
        final Indirizzo indirizzo = (Indirizzo) this.mittente;
        return "From: " + indirizzo.toString();
    }

    /**
     * Restituisce la decodifica di una stringa rappresentante la codifica di un mittente
     * 
     * @param codifica la codifica dell'indirizzo del mittente
     * @return un oggetto di tipo {@link Mittente}
     * @throws NullPointerException se codifica {@code null}
     * @throws IllegalArgumentException se codifica vuoto o non ben formato
     */
    public static Mittente decodifica(final String codifica) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(codifica, "Non e' possibile decodificare una codifica del mittente null").isEmpty())
            throw new IllegalArgumentException("Non e' possibile decodificare una codifica del mittente vuoto");
        return new Mittente(Indirizzo.decodifica(codifica));
    }
}
