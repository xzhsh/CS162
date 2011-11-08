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
 * Time: 1:51 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerTimeoutTest {

    @Test
    public void test() throws IOException {

        final int PORT = 1234;
        final String GAME = "TestGame";
        GameInfo gameInfo;
        BoardInfo boardInfo;

        ServerSocket server;
        ClientConnection con;
        Socket c1;
        Socket c2;
        int synId1;
        int synId2;

        // Testing MachinePlayer Timeout
        final String MPTEST = "TestMachinePlayer";
        gameInfo = MessageFactory.createGameInfo(GAME);
        boardInfo = MessageFactory.createBoardInfo(new Board(8));
        server = new ServerSocket(PORT);
        Thread mpThread = new Thread() {
            public void run() {
                MachinePlayer.main(new String[] {"localhost", String.valueOf(PORT), MPTEST});
            }
        };

        mpThread.start();

        c1 = server.accept();
        c2 = server.accept();
        synId1 = (new DataInputStream(c1.getInputStream())).readInt();
        synId2 = (new DataInputStream(c2.getInputStream())).readInt();
        assertEquals(synId1, synId2);
        con = new ClientConnection(c1, c2, synId1);







        // Testing HumanPlayer Timeout
        final String HPTEST = "TestHumanPlayer";
        gameInfo = MessageFactory.createGameInfo(GAME);
        boardInfo = MessageFactory.createBoardInfo(new Board(8));
        server = new ServerSocket(PORT);
        Thread hpThread = new Thread() {
            public void run() {
                HumanPlayer.main(new String[] {"localhost", String.valueOf(PORT), HPTEST});
            }
        };

        hpThread.start();

        c1 = server.accept();
        c2 = server.accept();
        synId1 = (new DataInputStream(c1.getInputStream())).readInt();
        synId2 = (new DataInputStream(c2.getInputStream())).readInt();
        assertEquals(synId1, synId2);
        con = new ClientConnection(c1, c2, synId1);


    }
}
