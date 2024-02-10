package mua;

import java.util.Objects;

/**
 * Classe astratta che rappresenta una intestazione di una messaggio definita da un {@code tipo}.
 * 
 * <p>Alcuni esempi di intestazione sono {@link Mittente}, {@link Destinatari}, {@link Data}, {@link Oggetto}, {@link ContentType}, {@link ContentTransferEncoding}, {@link MimeVersion}.
 *
 * <p>Questa classe descrive il contratto di {@link #codifica()} che dovra' essere implementato in ogni sua sottoclasse.
 */
public abstract class Intestazione {
    /**
     * Il tipo di intestazione
     */
    protected final String tipo;

    // RI: tipo non null e non vuoto 
    // AF: una intestazione generale con un tipo 

    /**
     * Costruisce una intestazione dato il tipo di essa
     * 
     * @param tipo il tipo dell'intestazione
     * @throws NullPointerException se tipo {@code null}
     * @throws IllegalArgumentException se tipo vuoto
     */
    public Intestazione(final String tipo)throws NullPointerException, IllegalArgumentException{
        if (Objects.requireNonNull(tipo, "Il tipo dell'intestazione non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("Il tipo dell'intestazione non puo' essere vuoto");
        this.tipo = tipo;
    }

    /**
     * Restituisce il tipo dell'intestazione
     * 
     * @return il tipo
     */
    public String tipo(){
        return tipo;
    }

    /**
     * Restituisce la codifica dell'intestazione
     * 
     * <p>La codifica dell'intestazione e' data dalla codifica del suo tipo seguita da quella del suo valore
     * 
     * @return la codifica dell'intestazione
     */
    public abstract String codifica();

    @Override
    public String toString() {
        return codifica();
    }
}
