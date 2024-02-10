package mua;

import java.util.Objects;

/**
 * Classe concreta immutabile che rappresenta una intestazione di tipo MimeVersion di un {@link Messaggio} di una {@link Messaggio.Parte}.
 * 
 * <p> MimeVersion e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato dalla <em>mimeVersion</em>.
 */
public class MimeVersion extends Intestazione{
    /**
     * la mime version
     */
    private final String mimeVersion;

    // RI: mimeVersion non null e vuota
    // AF: un oggetto mime version e' una intestazione tipica dei messaggi multipart descritto dalla version 

    /**
     * Costruisce una intestazione MimeVersion data la versione
     * 
     * @param mimeVersion la mime version
     * @throws IllegalArgumentException se mimeVersion null
     * @throws NullPointerException se mimeVersion vuota
     */
    public MimeVersion(final String mimeVersion) throws IllegalArgumentException, NullPointerException {
        super("MIME-Version");
        if (Objects.requireNonNull(mimeVersion, "La mime version dell'intestazione non puo' essere nulla").isEmpty())
            throw new IllegalArgumentException("La mime version dell'intestazione non puo' essere vuota");
        this.mimeVersion = mimeVersion;
    }

    /**
     * Restituisce la mime version
     * 
     * @return la mime version
     */
    public String getMimeVersion() {
        return mimeVersion;
    }

    @Override
    public String codifica() {
        return "MIME-Version: " + mimeVersion;
    }
}
