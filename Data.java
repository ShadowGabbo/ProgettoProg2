package mua;

import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Objects;
import utils.ASCIICharSequence;
import utils.DateEncoding;

/**
 * Classe concreta immutabile che rappresenta una data di un {@link Messaggio}.
 * 
 * <p> Data e' un tipo di {@link Intestazione}
 * 
 * <p> Lo stato e' rappresentato da una <em>data</em> ({@link ZonedDateTime}).
 */
public class Data extends Intestazione{
    /**
     * La data dell'intestazione
     */
    private final ZonedDateTime date;

    // RI: date non null
    // AF: un oggetto di tipo Data e' rappresentato da una data

    /**
     * Costruisce una data da una
     * 
     * @param date la data dell'intestazione
     * @throws NullPointerException se data e' {@code null}
     */
    public Data(final ZonedDateTime date) throws NullPointerException {
        super("Date");
        this.date = Objects.requireNonNull(date);
    }

    /**
     * Restituisce la data 
     * 
     * @return la data
     */
    public ZonedDateTime getDate(){
        return date;
    }

    @Override
    public String codifica() {
        return "Date: " + DateEncoding.encode(date).toString();
    }

    /**
     * Restituisce la decodifica di una stringa rappresentante la codifica di una data
     * 
     * @param codifica la codifica della data
     * @return un oggetto di tipo {@link Data}
     * @throws NullPointerException se codifica {@code null}
     * @throws IllegalArgumentException se codifica vuoto o non ben formata
     */
    public static Data decodifica(String codifica) throws NullPointerException, IllegalArgumentException {
        if (Objects.requireNonNull(codifica, "Non e' possibile decodificare una codifica della data null").isEmpty())
            throw new IllegalArgumentException("Non e' possibile decodificare una codifica della data vuota");
        try{
            final Data data = new Data(DateEncoding.decode(ASCIICharSequence.of(codifica)));
            return data;
        }catch(IllegalArgumentException | DateTimeParseException| NullPointerException e){
            throw new IllegalArgumentException("La codifica della data non e' ben formata");
        }
    }   

    @Override
    public String toString() {
        final String[] parts = date.toString().split("T");
        final String datetime = parts[0];
        final String time = parts[1].split("\\+")[0];
        return String.format("%s\n%s", datetime, time);
    }
}