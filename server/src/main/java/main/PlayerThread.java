package main;

import model.HangmanGame;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

public class PlayerThread extends Thread {
    private final SocketChannel client;

    PlayerThread(SocketChannel client) {
        this.client = client;
        setDaemon(true); // förhindrar att spelet är igång när gamla spelare håller spelet igång
    }

    @Override
    public void run() { //trådens main funktion
        try {
            Scanner reader = new Scanner(client); // new, scanner kan läsa från channel
            ByteBuffer writer = ByteBuffer.allocate(2000); // new, printwriter kan ej skriva till en channel,
            // måste bytas ut2000 för säkerhetsskull,
            Charset utf8 = Charset.forName("UTF-8"); // new

            HangmanGame gameround = new HangmanGame();

            while (gameround.attemptsLeft() > 0) {
                writer.put(utf8.encode(gameround.clue())); //skriv
                writer.put(utf8.encode(Integer.toString(gameround.attemptsLeft()))); //skriv
                writer.flip(); //vänder när vi är klara med att skriva, flip har restriktioner så som att vi bara kan läsa det
                //vi skrivit
                client.write(writer); // ist för flush, då vi hanterar det manuellt, skickar till writer,
                // läser från bytebuffer
                writer.compact(); // vänder tillbaka, kan ej använda flip, pga restriktionerna


                String guess = reader.nextLine();
                gameround.tryALetter(guess.charAt(0));
            }
        } catch (IOException error) { // if error exit the gameround
            throw new RuntimeException(error);
        } finally {
            try {
                client.close();
            } catch (IOException error) {
                throw new RuntimeException(error);
            }
        }
    }
}
