package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import edu.berkeley.cs.cs162.Client.HumanPlayer;
import edu.berkeley.cs.cs162.Client.MachinePlayer;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.ClientConnection;
import edu.berkeley.cs.cs162.Writable.*;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

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
        final String MPNAME = "TestMachinePlayer";

        GameInfo gameInfo = MessageFactory.createGameInfo(GAME);
        BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(8));
        ClientInfo blackPlayerInfo = MessageFactory.createMachinePlayerClientInfo(MPNAME);
        ClientInfo whitePlayerInfo = MessageFactory.createMachinePlayerClientInfo("DummyMachinePlayer");

        ServerSocket server = new ServerSocket(PORT);
        Thread mpThread = new Thread() {
            public void run() {
                MachinePlayer.main(new String[] {"localhost", String.valueOf(PORT), MPNAME});
            }
        };

        mpThread.start();

        Socket c1 = server.accept();
        Socket c2 = server.accept();
        int synId1 = (new DataInputStream(c1.getInputStream())).readInt();
        int synId2 = (new DataInputStream(c2.getInputStream())).readInt();
        assertEquals(synId1, synId2);
        ClientConnection con = new ClientConnection(c1, c2, synId1);

        // CONNECTION TEST
        con.receive3WayHandshake(new Random());
        Message connectMsg = con.readFromClient();
        assertEquals(connectMsg.getMsgType(), MessageProtocol.OP_TYPE_CONNECT);

        ClientInfo cInfo = ((ClientMessages.ConnectMessage) connectMsg).getClientInfo();
        assertEquals(cInfo.getName(), MPNAME);
		assertEquals(MessageProtocol.TYPE_MACHINE, cInfo.getPlayerType());
        con.sendReplyToClient(MessageFactory.createStatusOkMessage());

        // WAITING FOR GAMES TEST
        Message wfgMessage = con.readFromClient();
        assertEquals(MessageProtocol.OP_TYPE_WAITFORGAME, wfgMessage.getMsgType());
        con.sendReplyToClient(MessageFactory.createStatusOkMessage());

        // GAME START TEST
        Message gameStartMessage = MessageFactory.createGameStartMessage(gameInfo, boardInfo, blackPlayerInfo, whitePlayerInfo);
        con.sendToClient(gameStartMessage);

        assertTrue(con.readReplyFromClient(gameStartMessage).isOK());

        // GET MOVE TEST
        Message getMoveMessage = MessageFactory.createGetMoveMessage();
        con.sendToClient(getMoveMessage);

        // MOVE TIMEOUT TEST
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {

        }

        System.out.println("Delayed successfully for 2s");


    }
}
