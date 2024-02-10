package mua;

import java.io.IOException;
import java.util.NoSuchElementException;
import utils.UIInteract;

/** The application class */
public class App {

  /**
   * Gestisce il comando compose
   * 
   * @throws IllegalArgumentException se corpi vuoti
   * @param ui la ui
   * @param mua la lista di mailbox
   */
  private static void handleCompose(final UIInteract ui, final Mua mua) throws IllegalArgumentException{
      final Mittente mittente = Mittente.decodifica(ui.line("From: "));
      final Destinatari destinatari = Destinatari.decodifica(ui.line("To: "));
      final Oggetto oggetto = Oggetto.decodifica(ui.line("Subject: "));
      final Data date = Data.decodifica(ui.line("Date: "));

      // text body
      ui.prompt("Text body (. to end): ");
      String corpo_text = "";
      for(;;){
        final String line = ui.line();
        if (line.equals(".")) break;
        corpo_text += line + "\n";
      }

      // html body
      ui.prompt("Html body (. to end): ");
      String corpo_html = "";
      for(;;){
        final String line = ui.line();
        if (line.equals(".")) break;
        corpo_html += line + "\n";
      }
      
      final Messaggio messaggio;

      // caso messaggio senza corpo
      if (corpo_html.isEmpty() && corpo_text.isEmpty()) throw new IllegalArgumentException("Impossibile creare un messaggio senza corpi");

      if (corpo_text.isEmpty()==false && corpo_html.isEmpty()==false){
        // creo un messaggio multipart
        messaggio = new Messaggio(mittente, destinatari, oggetto, date, corpo_text, corpo_html);
      }else{
        if (corpo_text.isEmpty()==false){
          // il messaggio contiene text/plain (e' un messaggio di testo)
          messaggio = new Messaggio(mittente, destinatari, oggetto, date, corpo_text, false);
        }else{
          // il messaggio contiene text/html (e' un messaggio con html)
          messaggio = new Messaggio(mittente, destinatari, oggetto, date, corpo_html, true);
        }
      } 

      mua.salvaMessaggio(messaggio);
  }
  
  /** Runs the REPL.
   * 
   * <p>Develop here the REPL, see the README.md for more details.
   * 
   * @param args the first argument is the mailbox base directory.
   * @throws IOException se errore i/o
   */
  public static void main(String[] args) throws IOException {
      final Mua mua = new Mua(args[1]);

      try (UIInteract ui = UIInteract.getInstance()) {
        for (;;) {
          String[] input = ui.command(mua.prompt());
          if (input == null) break;
          switch (input[0]) {
            case "LSM":
              ui.output(mua.toString());
              break;
            case "MBOX":
              try{
                final int index = Integer.parseInt(input[1]);
                mua.selectMailbox(index);
              }catch(NumberFormatException | IndexOutOfBoundsException e ){
                ui.error("Unknown command");
              }
              break;
            case "LSE":
              try{
                final Mailbox mailbox = mua.getMailbox();
                ui.output(mailbox.toString());
              }catch(NoSuchElementException e){
                ui.error("Unknown command");
              }
              break;
            case "READ":
              try{
                final int message_index = Integer.parseInt(input[1]);
                ui.output(mua.leggiMessaggio(message_index)+"\n");
              }catch (IndexOutOfBoundsException | NumberFormatException | NoSuchElementException e) {
                ui.error("Unknown command");
              }
              break;
            case "COMPOSE":
              try {
                handleCompose(ui, mua);
              } catch (NoSuchElementException | NullPointerException | IllegalArgumentException e) {
                ui.error("Unknown command");
              }
              break;
            case "DELETE":
            try{
                final int index = Integer.parseInt(input[1]);
                mua.cancellaMessaggio(index);
              }catch (NumberFormatException | IndexOutOfBoundsException e) {
                ui.error("Unknown command");
              }
              break;
            case "#": continue;
            case "EXIT": return;
            default:
              ui.error("Unknown command");
              break;
          }
        }
      }
    }
}