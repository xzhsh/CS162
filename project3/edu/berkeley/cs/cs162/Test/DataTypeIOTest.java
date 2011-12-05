package edu.berkeley.cs.cs162.Test;

import edu.berkeley.cs.cs162.Writable.DataTypeIO;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;
import static org.junit.Assert.*;

public class DataTypeIOTest {

    @Test
    public void testDataTypeIO() throws IOException,InterruptedException {

        String address = "localhost";
        final int port = 1234;

        final Socket container[] = new Socket[1];
        Thread serverThread = new Thread()  {
            public void run(){
                try {
                ServerSocket server = new ServerSocket(port);
                container[0] = server.accept();
                } catch (IOException e)
                {

                }
            }
        };
        serverThread.start();
        Socket sock1 = new Socket(address, port);
        serverThread.join();
        Socket sock2 = container[0];
        InputStream in = sock1.getInputStream();
        OutputStream out = sock2.getOutputStream();

        DataTypeIO.writeInt(out, 1000);
        int lol = DataTypeIO.readInt(in);
        assertEquals(1000, lol);

        DataTypeIO.writeByte(out, (byte) 53);
        byte lolbyte = DataTypeIO.readByte(in);
        assertEquals((byte) 53, lolbyte);

        DataTypeIO.writeDouble(out, 34.657643);
        double loldouble = DataTypeIO.readDouble(in);
        assertEquals(34.657643, loldouble, 0.000001);

        DataTypeIO.writeString(out, "Yo dawg, I heard you like data");
        String lolstring = DataTypeIO.readString(in);
        assertEquals("Yo dawg, I heard you like data", lolstring);

        sock1.close();
        sock2.close();
    }
}
