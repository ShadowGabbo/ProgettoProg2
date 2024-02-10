package mua;

import java.util.Objects;
import utils.ASCIICharSequence;
import utils.Base64Encoding;

/**
 * Classe concreta immutabile che rappresenta un oggetto di un {@link Messaggio}.
 * 
 * <p> Un oggetto e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato da il <em>testo dell'oggetto</em> eventualmente vuoto.
 */
public class Oggetto extends Intestazione{
    /**
     * Il contenuto testuale dell'oggetto
     */
    private final String oggetto;

    // RI: oggetto non null eventualmente vuoto
    // AF: un oggetto di una intestazione con un contenuto testuale

    /**
     * Costruisce un oggetto dato il suo contenuto testuale
     * 
     * @param oggetto il contenuto dell'oggetto
     * @throws NullPointerException se oggetto {@code null}
     */
    public Oggetto(final String oggetto) throws NullPointerException {
        super("Subject");
        this.oggetto = Objects.requireNonNull(oggetto, "L'oggetto dell'intestazione non puo' essere nullo");
    }

    /**
     * Restituisce il contenuto testuale dell'oggetto
     * 
     * @return l'oggetto 
     */
    public String getOggetto() {
        return oggetto;
    }

    @Override
    public String codifica() {
        if (ASCIICharSequence.isAscii(String.valueOf(oggetto))){
            // oggetto contiene solo caratteri ascii
            return "Subject: " + String.valueOf(oggetto);
        }else{
            // oggetto contiene caratteri non ascii
            return "Subject: " + Base64Encoding.encodeWord(String.valueOf(oggetto));
        }
    }

    /**
     * Restituisce la decodifica dell'oggetto
     * 
     * @param codifica la codifica dell'oggetto
     * @return un oggetto di tipo {@link Oggetto}
     * @throws NullPointerException se codifica {@code null}
     */
    public static Oggetto decodifica(final String codifica) throws NullPointerException {
        Objects.requireNonNull(codifica, "Non e' possibile decodificare una codifica dell'oggetto null");

        if (codifica.startsWith("=?utf-8?B?")){
            // la codifica/encode contiene caratteri non ascii
            return new Oggetto(Base64Encoding.decodeWord(ASCIICharSequence.of(codifica)));
        }else{
            // la codifica/encode conteneva solo caratteri ascii
            return new Oggetto(codifica);
        }
    }
}
