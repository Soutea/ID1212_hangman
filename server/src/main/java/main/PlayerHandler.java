package main;

import common.net.NonBlockingScanner;
import common.net.PrintBuffer;
import model.HangmanGame;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class PlayerHandler {
    private final SocketChannel client;
    private final NonBlockingScanner reader; // new, scanner kan läsa från channel
    private final PrintBuffer writer;


    PlayerHandler(SocketChannel client) {
        this.client = client;
        reader = new NonBlockingScanner(client);
        writer = new PrintBuffer(client, ByteBuffer.allocate(2000)); // new, printwriter kan ej skriva till en channel,
        // måste bytas ut2000 för säkerhetsskull,
    }

    HangmanGame gameround = new HangmanGame();

    public void readMessage() throws IOException {
        try {
            while (true) {
                String guess = reader.nextLine();
                if (guess == null) {
                    break;
                }

                if (guess.length() > 0 && gameround.attemptsLeft() > 0) { //buggfix
                    gameround.tryALetter(guess.charAt(0));
                    sendStatus();
                }
            }
        } catch (IOException error) { // if error exit the gameround
            throw new RuntimeException(error);
        }
    }

    public boolean flush() throws IOException {
        return writer.flush();
    }

    public void sendStatus() throws IOException {
        writer.print(gameround.clue() + "\r\n"); //skriv , r= carriage return, n= new line
        writer.print(gameround.attemptsLeft() + "\r\n"); //skriv
        flush();
    }
}
