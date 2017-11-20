package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerMain {

    public static void main(String args[]) throws IOException {

        SocketChannel client = null;  //new

        ServerSocketChannel server = ServerSocketChannel.open(); //new
        server.bind(new InetSocketAddress(5555));
        while (true) {
            client = server.accept(); //om clienten accepterar så har vi en anslutning
            PlayerThread player = new PlayerThread(client);
            player.start();

        }

    }
}


