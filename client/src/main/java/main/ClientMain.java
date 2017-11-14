package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ClientMain {

    public static void main(String args[]) throws IOException {

        Socket client = new Socket("localhost", 5555); // object client, my connection

        PrintWriter writer = new PrintWriter(client.getOutputStream()); // från client till server (skriver)
        Scanner reader = new Scanner(client.getInputStream()); // från server till client (läser)


        TransmittingThread thread = new TransmittingThread(client);
        thread.start();
        System.out.println("Hello! You are now playing this simple version of hangman");
        System.out.println("");
        try {
            while (true) {
                String clue = reader.nextLine();
                String triesLeft = reader.nextLine();

                if(!clue.contains("-")){
                    System.out.println("Congrats you won this incredible game!");
                    break;
                }
                System.out.println(clue);
                System.out.println("You have " + triesLeft + " attempts left");
                //System.out.println(triesLeft);

            }
        } catch (NoSuchElementException error) {
            System.out.println("Oops, You lost!");
        }

    }
}


