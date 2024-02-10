package mua;

import java.util.Objects;

/**
 * Classe concreta immutabile che rappresenta un content transfer encoding.
 * 
 * <p> Content Transfer Encoding e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato da un <em>encoding</em>.
 */
public class ContentTransferEncoding extends Intestazione{
    /**
     * Il valore dell'encoding
     */
    private final String encoding;

    // RI: encoding non null e non vuoto
    // AF: un content transfer encoding ossia una intestazione con un encoding usata per le text/html part

    /**
     * Costruisce un ContentTransferEncoding dato l'encoding
     * 
     * @param encoding il valore dell'encoding
     * @throws NullPointerException se encoding null
     * @throws IllegalArgumentException se encoding vuoto
     */
    public ContentTransferEncoding(final String encoding) throws NullPointerException, IllegalArgumentException {
        super("Content-Transfer-Encoding");
        if (Objects.requireNonNull(encoding, "Il valore di encoding non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("Il valore di encoding non puo' essere vuoto");
        this.encoding = encoding;
    }

    /**
     * Restituisce l'encoding scelto
     * 
     * @return l'encoding
     */
    public String getEncoding() {
        return encoding;
    }

    @Override
    public String codifica() {
        return String.format("Content-Transfer-Encoding: %s", encoding);
    }
}
