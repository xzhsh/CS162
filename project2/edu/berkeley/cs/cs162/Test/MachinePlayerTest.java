package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import edu.berkeley.cs.cs162.Client.MachinePlayer;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.ClientConnection;
import edu.berkeley.cs.cs162.Writable.*;
import org.junit.Test;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/7/11
 * Time: 7:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachinePlayerTest {

    @Test
    public void test() throws IOException {
        final int TEST_PORT = 1234;
        final String TEST_NAME = "TestMachinePlayer";
        final String TEST_GAME_NAME = "TestGame";

        GameInfo gameInfo = MessageFactory.createGameInfo(TEST_GAME_NAME);
        BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(8));
        ClientInfo blackPlayerInfo = MessageFactory.createMachinePlayerClientInfo(TEST_NAME);
        ClientInfo whitePlayerInfo = MessageFactory.createMachinePlayerClientInfo("DummyMachinePlayer");

        ServerSocket server = new ServerSocket(TEST_PORT);
        Thread machinePlayerThread = new Thread() {
            public void run() {
                MachinePlayer.main(new String[] {"localhost", String.valueOf(TEST_PORT), TEST_NAME});
            }
        };

        machinePlayerThread.start();

        ClientConnection con;
        Socket c1 = server.accept();
        Socket c2 = server.accept();

        int synId1 = (new DataInputStream(c1.getInputStream())).readInt();
        int synId2 = (new DataInputStream(c2.getInputStream())).readInt();
        assertEquals(synId1, synId2);
        con = new ClientConnection(c1, c2, synId1);

        // CONNECTION TEST
        con.receive3WayHandshake(new Random());
        Message connectMsg = con.readFromClient();
        assertEquals(connectMsg.getMsgType(), MessageProtocol.OP_TYPE_CONNECT);

        ClientInfo cInfo = ((ClientMessages.ConnectMessage) connectMsg).getClientInfo();
        assertEquals(cInfo.getName(), TEST_NAME);
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

        Message moveReplyGeneric = con.readReplyFromClient(getMoveMessage);
        assertTrue("Should have gotten a special STATUS_OK message", moveReplyGeneric instanceof ResponseMessages.GetMoveStatusOkMessage);
        ResponseMessages.GetMoveStatusOkMessage moveReply = (ResponseMessages.GetMoveStatusOkMessage) moveReplyGeneric;
        assertTrue(moveReply.isOK());
        assertTrue(moveReply.getMoveType() != -1);
        System.out.println(moveReply.getMoveType());
        System.out.println(moveReply.getLocation());

        // MAKE MOVE TEST
        Vector<BoardLocation> captured = new Vector<BoardLocation>();
        Message makeMoveMessage = MessageFactory.createMakeMoveMessage(gameInfo, blackPlayerInfo, moveReply.getMoveType(), moveReply.getLocation().makeBoardLocation(), captured);
        con.sendToClient(makeMoveMessage);

        assertTrue(con.readReplyFromClient(makeMoveMessage).isOK());

        // GET MOVE OPPONENT TEST


        // MAKE MOVE OPPONENT TEST


        // GAME OVER TEST
        Message gameOverMessage = MessageFactory.createGameOverMessage(gameInfo, 1.0, 0.5, blackPlayerInfo);
        con.sendToClient(gameOverMessage);

        assertTrue(con.readReplyFromClient(gameOverMessage).isOK());

        // WAITING FOR GAMES TEST (AFTER OTHER GAME)
        Message wfg2Message = con.readFromClient();
        assertEquals(MessageProtocol.OP_TYPE_WAITFORGAME, wfg2Message.getMsgType());
        con.sendReplyToClient(MessageFactory.createStatusOkMessage());
    }
}
