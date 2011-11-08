package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Location;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.StoneColorInfo;
import edu.berkeley.cs.cs162.Writable.WritableByte;
import edu.berkeley.cs.cs162.Writable.WritableDouble;
import edu.berkeley.cs.cs162.Writable.WritableString;


public class WritablesTest {

    @Test
    public void testWritables() throws IOException, InterruptedException {

        String address = "localhost";
        final int port = 1234;

        // Initialize sockets and stuff...
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


        // TODO BoardInfo


        // ClientInfo
        ClientInfo c = MessageFactory.createMachinePlayerClientInfo("kunal");
        ClientInfo kunal = MessageFactory.createHumanPlayerClientInfo("");

        c.writeTo(out);
        kunal.readFrom(in);

        assertEquals("kunal", kunal.getName());
        assertEquals(MessageProtocol.TYPE_MACHINE, kunal.getPlayerType());


        // GameInfo
        GameInfo g = MessageFactory.createGameInfo("kunal's game");
        GameInfo kg = MessageFactory.createGameInfo("lawlawlawlawl");

        g.writeTo(out);
        kg.readFrom(in);

        assertEquals("kunal's game", kg.getName());


        // Location
        Location l = MessageFactory.createLocationInfo(54, 2);
        Location l2 = MessageFactory.createLocationInfo(0,0);

        l.writeTo(out);
        l2.readFrom(in);

        assertEquals("(54,2)", l2.toString());


        // StoneColorInfo
        StoneColorInfo s = MessageFactory.createStoneColorInfo(MessageProtocol.STONE_BLACK);
        StoneColorInfo s2 = MessageFactory.createStoneColorInfo(MessageProtocol.STONE_NONE);

        s.writeTo(out);
        s2.readFrom(in);

        assertEquals(MessageProtocol.STONE_BLACK, s2.getColor());


        // WritableByte
        WritableByte by = MessageFactory.createWritableByte((byte) 32);
        WritableByte by2 = MessageFactory.createWritableByte((byte) 0);

        by.writeTo(out);
        by2.readFrom(in);

        assertEquals(32, by2.getValue());


        // WritableDouble
        WritableDouble wd = MessageFactory.createWritableDouble(3.14159);
        WritableDouble wd2 = MessageFactory.createWritableDouble(0.00000);

        wd.writeTo(out);
        wd2.readFrom(in);

        assertEquals(3.14159, wd2.getValue(), 0.000001);


        // TODO WritableList


        // WritableString
        WritableString st = MessageFactory.createWritableString("Yo dawg I heard you like writables");
        WritableString st2 = MessageFactory.createWritableString("");

        st.writeTo(out);
        st2.readFrom(in);

        assertEquals("Yo dawg I heard you like writables", st2.getValue());

    }

}
