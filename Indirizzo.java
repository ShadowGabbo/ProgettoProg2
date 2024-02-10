package mua;

import java.util.List;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.AddressEncoding;

/**
 * Classe concreta immutabile che rappresenta un indirizzo di un {@link Mittente}, o di un destinatario ({@link Destinatari}) di un {@link Messaggio}.
 * 
 * <p>Lo stato e' rappresentato da un {@code display_name} (opzionale), da una parte {@code locale}, ed infine da una parte {@code dominio}
 */
public class Indirizzo {
    /**
     * Il nome/username visualizzato (opzionale)
     */
    private final String display_name;

    /**
     * La parte locale
     */
    private final String locale;

    /**
     * La parte del dominio
     */
    private final String dominio;

    // RI: locale e dominio non null o vuoti, display_name non null, dominio e locale formati solo da caratteri ascii
    // AF: un indirizzo con un email formata da una parte locale ed un dominio ed eventualmente anche un display_name opzionale
    
    /**
     * Costruisce un indizizzo dato un display name, un indirizzo locale ed un dominio
     * 
     * @param display_name, il nome visualizzato
     * @param locale, la parte locale
     * @param dominio, la parte del dominio
     * @throws NullPointerException se display_name locale o dominio sono {@code null}
     * @throws IllegalArgumentException se locale o dominio sono vuoti
     */
    public Indirizzo(final String display_name, final String locale, final String dominio)throws NullPointerException, IllegalArgumentException{
        if (Objects.requireNonNull(locale, "La parte locale dell'indirizzo non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("La parte locale dell'indirizzonon puo' essere vuota");
        if (Objects.requireNonNull(dominio, "Il dominio dell'indirizzo non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("Il dominio dell'indirizzo non puo' essere vuoto");
        Objects.requireNonNull(display_name, "Il display_namme dell'indirizzo non puo' essere nullo");
        if (AddressEncoding.isValidAddressPart(locale) == false)
            throw new IllegalArgumentException("La parte locale non e' ben formata");
        if (AddressEncoding.isValidAddressPart(dominio) == false)
            throw new IllegalArgumentException("La parte del dominio non e' ben formata");
        this.display_name = display_name;
        this.locale = locale;
        this.dominio = dominio;
    }

    /**
     * Costruisce un indizizzo data una parte locale ed un dominio
     * 
     * @param locale, la parte locale
     * @param dominio, la parte del dominio
     * @throws NullPointerException se parte locale o dominio sono {@code null}
     * @throws IllegalArgumentException se parte locale o dominio sono vuoti oppure se non sono ben formati
     */
    public Indirizzo(final String locale, final String dominio)throws NullPointerException, IllegalArgumentException{
        if (Objects.requireNonNull(locale, "La parte locale dell'indirizzo non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("La parte locale dell'indirizzonon puo' essere vuota");
        if (Objects.requireNonNull(dominio, "Il dominio dell'indirizzo non puo' essere nullo").isEmpty())
            throw new IllegalArgumentException("Il dominio dell'indirizzo non puo' essere vuoto");
        if (AddressEncoding.isValidAddressPart(locale) == false)
            throw new IllegalArgumentException("La parte locale non e' ben formata");
        if (AddressEncoding.isValidAddressPart(dominio) == false)
            throw new IllegalArgumentException("La parte del dominio non e' ben formata");
        this.display_name = "";
        this.locale = locale;
        this.dominio = dominio;
    }

    /**
     * Restituisce il display_name
     * 
     * @return il display_name
     */
    public String getDisplay_name() {
        return display_name;
    }

    /**
     * Restituisce l'indirizzo locale
     * 
     * @return l'indirizzo locale
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Restituisce il dominio
     * 
     * @return il dominio
     */
    public String getDominio() {
        return dominio;
    }

    /**
     * Effettua la decodifica di un indirizzo codificato passato come argomento
     * 
     * @param address, l'indirizzo codificato
     * @throws NullPointerException se address {@code null}
     * @throws IllegalArgumentException se address vuoto o non ben formato
     * @return l'oggetto indirizzo
     */
    public static Indirizzo decodifica(final String address)throws NullPointerException, IllegalArgumentException{
        if (Objects.requireNonNull(address, "La codifica dell'indirizzo non puo' essere nulla").isEmpty())
            throw new IllegalArgumentException("La codifica dell'indirizzo non puo' essere vuota");
        try{
            final List<String> parts = AddressEncoding.decode(ASCIICharSequence.of(address)).get(0);
            return new Indirizzo(parts.get(0),parts.get(1), parts.get(2));
        }catch(IndexOutOfBoundsException | IllegalArgumentException| NullPointerException e){
            throw new IllegalArgumentException("La codifica dell'indirizzo non e' ben formata");
        }
    }

    /**
     * Restituisce l'email di un indirizzo
     * 
     * @param indirizzo, l'indirizzo
     * @return l'email ossia la concatenazione tra la parte locale e la parte del dominio
     * @throws NullPointerException se indirizzo {@code null}
     */
    public static String email(final Indirizzo indirizzo) throws NullPointerException{
        Objects.requireNonNull("L'indirizzo non puo' essere null");
        return String.format("%s@%s",indirizzo.getLocale(), indirizzo.getDominio());
    }


    @Override
    public String toString() {
        if (display_name.equals("")) return String.format("%s@%s", locale, dominio); 
        if (display_name.split(" ").length > 2){
            return String.format("\"%s\" <%s@%s>", display_name, locale, dominio);
        }
        return String.format("%s <%s@%s>", display_name, locale, dominio);
    }
}
