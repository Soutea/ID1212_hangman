package common.net;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class NonBlockingScanner {

    private final SocketChannel channel;
    private final ByteBuffer bytes;
    private final CharsetDecoder decoder;
    private final CharBuffer chars;

    public NonBlockingScanner(SocketChannel channel) {
        this.channel = channel;
        bytes = ByteBuffer.allocate(2000);
        decoder = Charset.forName("UTF-8").newDecoder();
        chars = CharBuffer.allocate(2000);

    }

    public String nextLine() throws IOException {
        try {
            if (channel.read(bytes) == -1) { // skriver till bytes
                return null;
            }
        } catch (IOException error) {
            return null;
        }
        bytes.flip(); //gå till läsläge
        decoder.decode(bytes, chars, false); // läser från bytes
        bytes.compact(); // gå till skrivläge för nästa gång vi ropar
        chars.flip(); //läsläge för chars
        while (chars.hasRemaining()) {
            char chr = chars.get();
            if (chr == '\n') {
                char[] lineChars = new char[chars.position()];
                chars.rewind();
                chars.get(lineChars);
                chars.compact();
                return new String(lineChars).trim(); //ta bort sista tecknet för radbytet
            }
        }
        chars.rewind(); //går tillbaka till position 0 för att inte förlora data från compact
        chars.compact();
        return null; //har ingen rad att returnera
    }
}
