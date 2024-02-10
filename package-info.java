/**
 * The MUA package.
 *
 * <ul>
 *   <li>{@link Intestazione} e' una intestazione generica, le sue sottoclassi piu' specifiche sono {@link Mittente}, {@link Destinatari}, {@link Data}, {@link Oggetto}, {@link ContentType}, {@link ContentTransferEncoding}, {@link MimeVersion}. </li>
 *   <li>un {@link Messaggio} e' formato da una o piu' {@link Messaggio.Parte} le quali sono formate da numerose {@link Intestazione} e un corpo
 *   <li>una {@link Mailbox} e' una collezione di {@link Messaggio}, e possiede alcune funzionalita' legate al messaggio quali visualizzazione aggiunta e cancellazione
 *   <li>una {@link Mua} e' una collezione di {@link Mailbox} e gestisce quindi diverse mailbox con alcune operazioni su di esse tra cui lettura e scrittura su disco di messaggi
 *   <li>la classe {@link App} invece e' applicazione che implementa un repl e usa la {@link Mua} con una interfaccia utente
 * </ul>
 */
package mua;
