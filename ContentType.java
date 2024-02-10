package mua;

import java.util.Objects;

/**
 * Classe concreta immutabile che rappresenta un content type che descrive come deve essere codificato il corpo del {@link Messaggio} di una {@link Messaggio.Parte}.
 * 
 * <p> Content type e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato da un <em>content</em> e da un <em>charset</em>.
 */
public class ContentType extends Intestazione{
    /**
     * Il valore del content type
     */
    private final String content;

    /**
     * Il Charset del content-type
     */
    private final String charset;

    // RI: content e charset non null, content non vuoto
    // AF: rappresenta una intestazione content-type ossia come un corpo di una parte del messaggio
    //     deve essere codificata attraverso il content e il charset

    /**
     * Costruisce un content-type dato il valore e il charset
     * 
     * @param content il valore del content-type
     * @param charset il charset
     * @throws NullPointerException se content o charset sono {@code null}
     * @throws IllegalArgumentException se content e' vuoto
     */
    public ContentType(final String content, final String charset) throws NullPointerException, IllegalArgumentException {
        super("Content-Type");
        if (Objects.requireNonNull(content, "Il valore del content-type non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("Il valore del content-type non puo' essere vuoto");
        Objects.requireNonNull(charset, "Il valore del charset non puo' essere nullo");
        this.content = content;
        this.charset = charset;
    }

    /**
     * Costruttore copia
     * 
     * @param c l'intestazione content type da copiare
     * @throws NullPointerException se c {@code null}
     */
    public ContentType(final ContentType c) throws NullPointerException{
        super("Content-Type");
        Objects.requireNonNull(c, "Il content-type non puo' essere null");
        this.content = c.getContent();
        this.charset = c.getCharset();
    }

    /**
     * Restituisce il valore del content type
     * 
     * @return il content type 
     */
    public String getContent() {
        return content;
    }

    /**
     * Restituisce il charset
     * 
     * @return il charset 
     */
    public String getCharset() {
        return charset;
    }

    @Override
    public String codifica() {
        if (!(charset.equals(""))){
            return String.format("Content-Type: %s; charset=\"%s\"", content, charset);
        }else{
            return String.format("Content-Type: %s; boundary=frontier", content);
        }
    }

    /**
     * Restituisce la decodifica di una stringa rappresentante la codifica di un content type
     * 
     * @param codifica la codifica di un content type
     * @return l'oggetto di tipo {@link ContentType}
     * @throws NullPointerException se codifica {@code null}
     * @throws IllegalArgumentException se codifica vuota
     */
    public static ContentType decodifica(String codifica) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(codifica, "Non e' possibile decodificare una codifica del content-type null").isEmpty())
            throw new IllegalArgumentException("Non e' possibile decodificare una codifica del content-type vuoto");
        final String[] parts = codifica.split("; ");
        if (parts[1].contains("charset")){
            return new ContentType(parts[0], parts[1].replace("charset=\"","").replace("\"", ""));
        }else{
            return new ContentType("multipart/alternative", "");
        }
    }

    @Override
    public String toString() {
        return String.format("Part\n%s", content);
    }
}
