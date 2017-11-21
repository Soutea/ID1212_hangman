package common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.Charset;

public class PrintBuffer {

    private final WritableByteChannel client;
    private final ByteBuffer buffer;
    private final Charset utf8;

    public PrintBuffer(WritableByteChannel client, ByteBuffer buffer) {
        this.client = client;
        this.buffer = buffer;
        utf8 = Charset.forName("UTF-8");
    }

    public boolean flush() throws IOException {
        buffer.flip(); //vänder när vi är klara med att skriva, flip har restriktioner så som att vi bara kan läsa det
        //vi skrivit
        if (buffer.hasRemaining()) { //läsa från bufferten till anslutningen
            client.write(buffer); // ist för flush, då vi hanterar det manuellt, skickar till writer,
            // läser från bytebuffer
        }
        buffer.compact(); // vänder tillbaka, kan ej använda flip, pga restriktionerna
        return buffer.position() == 0;
    }

    public void print(String str) {
        buffer.put(utf8.encode(str));
    }
}
