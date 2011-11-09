package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import edu.berkeley.cs.cs162.Client.ServerConnection;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Writable.*;
import org.junit.Test;

import static org.junit.Assert.*;

import edu.berkeley.cs.cs162.Server.ClientConnection;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/8/11
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachinePlayerTimeoutTest {
    @Test
    public void test() throws IOException, InterruptedException {

        String address = "localhost";
        final int port = 1234;

        final Socket container[] = new Socket[1];

        Thread serverThread = new Thread() {
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                    container[0] = server.accept();
                } catch (IOException e) {

                }
            }
        };

        serverThread.start();
        Socket s1 = new Socket(address, port);
        serverThread.join();
        Socket s2 = container[0];

        ClientConnection con = new ClientConnection(s1, s2, 0, new PrintStream(new NullOutputStream()));
        con.setValid();

        GameInfo gameInfo = MessageFactory.createGameInfo("game");
        BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(9));
        ClientInfo clientInfo = MessageFactory.createMachinePlayerClientInfo("test");
        ClientInfo opp = MessageFactory.createMachinePlayerClientInfo("opponentDummy");

        ServerConnection client = new ServerConnection(s2, s1);

        Random rng = new Random();
        //client.initiate3WayHandshake(rng);
        con.receive3WayHandshake(rng);

        //"connect" message from MachinePlayer
        /*client.sendSyncToServer(MessageFactory.createConnectMessage(clientInfo));
        msg = con.readFromClient();
        assertEquals(msg.getMsgType(), MessageProtocol.OP_TYPE_CONNECT);

        //send an "ok" reply
        con.sendReplyToClient(MessageFactory.createStatusOkMessage());

        //"wfg" message from MachinePlayer
        Message wfgMessage = MessageFactory.createWaitForGameMessage();
        client.sendSyncToServer(wfgMessage);
        msg = con.readFromClient();
        assertEquals(msg.getMsgType(), MessageProtocol.OP_TYPE_WAITFORGAME);

        //send an "ok" reply
        con.sendReplyToClient(MessageFactory.createStatusOkMessage());

        //send a "game start" w/ params message to MachinePlayer and other player
        con.sendToClient(MessageFactory.createGameStartMessage(gameInfo, boardInfo, clientInfo, opp));

        //receive "ok" from both
        Message reply = con.readReplyFromClient(MessageFactory.createStatusOkMessage());
        assertTrue(reply.getMsgType() == MessageProtocol.OP_STATUS_OK);*/

        //send a "get move" message to MachinePlayer
        con.sendToClient(MessageFactory.createGetMoveMessage());

        //artificially timeout; game over message should be sent because of this
        Thread.sleep(2000);

        //check if player gets a "game over"
        Message clientMsg = con.readReplyFromClient(MessageFactory.createGameOverMessage(gameInfo, 1.0, 0.0, clientInfo));
        assertEquals(clientMsg.getMsgType(), MessageProtocol.OP_TYPE_GAMEOVER);

        s1.close();
        s2.close();

        System.out.println("done");
    }
}
