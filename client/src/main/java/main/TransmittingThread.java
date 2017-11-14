package main;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class TransmittingThread extends Thread {

    private final Socket server;

    TransmittingThread(Socket server) {
        this.server = server;
    }


    public void run() {
        try {
            Scanner reader = new Scanner(System.in); // läser från användaren
            PrintWriter writer = new PrintWriter(server.getOutputStream());


            while (true) {
                String line = reader.nextLine();
                writer.println(line);
                writer.flush(); // skickar allt i kön (detta för att spelet inte ska fastna för att writer vill
                // skicka paket för paket
            }
        } catch (IOException error) {
            throw new RuntimeException(error);

        }

    }

}
