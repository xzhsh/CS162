package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;

import edu.berkeley.cs.cs162.Client.HumanPlayer;
import edu.berkeley.cs.cs162.Client.MachinePlayer;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.ClientConnection;
import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/8/11
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachinePlayerTimeoutTest {
    @Test
    public void test() throws IOException {

        final int PORT = 1234;
        final String GAME = "TestGame";
        final String MPTEST = "TestMachinePlayer";

        GameInfo gameInfo = MessageFactory.createGameInfo(GAME);
        BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(8));
        ServerSocket server = new ServerSocket(PORT);
        Thread mpThread = new Thread() {
            public void run() {
                MachinePlayer.main(new String[] {"localhost", String.valueOf(PORT), MPTEST});
            }
        };

        mpThread.start();

        Socket c1 = server.accept();
        Socket c2 = server.accept();
        int synId1 = (new DataInputStream(c1.getInputStream())).readInt();
        int synId2 = (new DataInputStream(c2.getInputStream())).readInt();
        assertEquals(synId1, synId2);
        ClientConnection con = new ClientConnection(c1, c2, synId1);


    }
}
