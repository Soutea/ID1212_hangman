package main;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {

    public static void main(String args[]) throws IOException {

        Socket client = null; //anslutning till client
        //Trying to listen to port 5555
        ServerSocket socket = new ServerSocket(5555); //öppnar för anslutning hos servern, så att clienter kan ansluta

        while (true) {
            client = socket.accept(); //om clienten accepterar så har vi en anslutning
            PlayerThread player = new PlayerThread(client);
            player.start();

        }

    }
}
