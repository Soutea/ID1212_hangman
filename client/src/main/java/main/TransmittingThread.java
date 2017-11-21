package main;

import common.net.PrintBuffer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.Scanner;

public class TransmittingThread extends Thread {
    private final WritableByteChannel connection;

    TransmittingThread(WritableByteChannel connection) {
        this.connection = connection;
        reader = new Scanner(System.in);
        writer = new PrintBuffer(connection, ByteBuffer.allocate(2000));
    }

    private final Scanner reader;
    private final PrintBuffer writer;


    public void run() {
        try {
            while (true) {
                String line = reader.nextLine();
                writer.print(line + "\r\n");
                writer.flush();
            }
        } catch (IOException error) {
            throw new RuntimeException(error);

        }

    }

}
