package main;

import model.HangmanGame;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class PlayerThread extends Thread {

    private final Socket client;

    PlayerThread(Socket client) {
        this.client = client;
        setDaemon(true); // förhindrar att spelet är igång när gamla spelare håller spelet igång
    }

    @Override
    public void run() { //trådens main funktion
        try {
            Scanner reader = new Scanner(client.getInputStream()); // från client till server (läser)
            PrintWriter writer = new PrintWriter(client.getOutputStream()); // från server till client (skriver)


            HangmanGame gameround = new HangmanGame();

            while (gameround.attemptsLeft() > 0) {
                writer.println(gameround.clue());
                writer.println(gameround.attemptsLeft());
                writer.flush();

                String guess = reader.nextLine();
                gameround.tryALetter(guess.charAt(0));
            }
        } catch (IOException error) { // if error exit the gameround
            throw new RuntimeException(error);
        } finally {
            try {
                client.close();
            } catch(IOException error) {
                throw new RuntimeException(error);
            }
        }
    }
}
