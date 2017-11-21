package main;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class ServerMain {

    public static void main(String args[]) throws IOException {

        ServerSocketChannel server = ServerSocketChannel.open(); //new
        server.bind(new InetSocketAddress(5555));
        Selector select = Selector.open(); //new, skapar selector, som ska skapa en delay
        server.configureBlocking(false); //non blocking

        server.register(select, SelectionKey.OP_ACCEPT); // new, krävs
        while (true) {
            select.select();  //new, väntar på att något ska hända i en av kanalerna
            for (SelectionKey key : select.selectedKeys()) {
                if (key.isAcceptable()) {
                    SocketChannel client = server.accept(); //om clienten accepterar så har vi en anslutning
                    if (client != null) {
                        client.configureBlocking(false);
                        PlayerHandler player = new PlayerHandler(client);
                        client.register(select, SelectionKey.OP_READ | SelectionKey.OP_WRITE, player);
                        player.sendStatus();
                    }
                }
                if (key.isReadable()) {
                    ((PlayerHandler) key.attachment()).readMessage();
                    if (!((SocketChannel) key.channel()).isConnected()) {
                        key.channel().close();
                        continue;
                    }
                    key.interestOps(key.interestOps() | SelectionKey.OP_WRITE);
                }
                if (key.isWritable()) {
                    if (((PlayerHandler) key.attachment()).flush()) {
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                }
            }
            select.selectedKeys().clear();
        }

    }
}


