package main;

import common.net.NonBlockingScanner;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.NoSuchElementException;

public class ClientMain {
    public static void main(String args[]) throws IOException {

        SocketChannel client = SocketChannel.open(new InetSocketAddress("localhost", 5555)); // object client,
        // my connection

        NonBlockingScanner reader = new NonBlockingScanner(client); // från server till client (läser)
        Selector select = Selector.open(); //new, skapar selector, som ska skapa en delay
        client.configureBlocking(false); //non blocking
        SelectionKey clientKey = client.register(select, SelectionKey.OP_READ);

        Pipe pipe = Pipe.open(); //skapar pipe
        Pipe.SourceChannel stdin = pipe.source(); //kanalen
        stdin.configureBlocking(false);
        stdin.register(select, SelectionKey.OP_READ);
        ByteBuffer stdinBuffer = ByteBuffer.allocate(2000); //standard buffer, input från tangentbordet

        TransmittingThread thread = new TransmittingThread(pipe.sink());
        thread.start();

        System.out.println("Hello! You are now playing this simple version of hangman");
        System.out.println("");

        String clue = null;
        String triesLeft = null;
        try {
            while (client.isConnected()) {
                select.select();
                for (SelectionKey key : select.selectedKeys()) {
                    select.selectedKeys().remove(key);

                    if (key.channel() == client) {
                        if (key.isReadable()) {
                            // LÄS PAKETET
                            if (clue == null) {
                                clue = reader.nextLine();
                            }
                            if (clue != null && triesLeft == null) {
                                triesLeft = reader.nextLine();
                            }
                            if (triesLeft != null) {
                                if (!clue.contains("-")) {
                                    System.out.println("Congrats you won this incredible game!");
                                    break;
                                }
                                System.out.println(clue);
                                System.out.println("You have " + triesLeft + " attempts left");
                                clue = null;
                                triesLeft = null;
                            }
                        }
                        if (key.isWritable()) {
                            stdinBuffer.flip(); //skrivläge
                            client.write(stdinBuffer); //skriva
                            if (!stdinBuffer.hasRemaining()) { // om buffern är tom, vi är färdiga
                                clientKey.interestOps(clientKey.interestOps() & ~SelectionKey.OP_WRITE);
                                // intresset för att skriva är borta då vi är klara
                            }
                            stdinBuffer.compact();
                        }
                    } else if (key.channel() == stdin && key.isReadable()) {
                        //skicka meddelande till servern
                        stdin.read(stdinBuffer); //från kanalen läser vi och lagrar i buffer
                        clientKey.interestOps(clientKey.interestOps() | SelectionKey.OP_WRITE);
                        //intresserade av att skriva till servern
                    }
                }
            }
        } catch (NoSuchElementException error) {
            System.out.println("Oops, You lost!");
        }

    }
}


