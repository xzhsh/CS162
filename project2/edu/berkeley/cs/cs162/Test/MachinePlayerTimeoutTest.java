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

        System.out.println("beginning message to client");
        //send a "get move" message to MachinePlayer
        con.sendToClient(MessageFactory.createGetMoveMessage());

        System.out.println("sleep you fools");
        //artificially timeout; game over message should be sent because of this
        Thread.sleep(2000);

        //check if player gets a "game over"
        //...how can i check if the game over message is sent without knowing what kind of message is sent to it? hm.
        //this hangs gosh darn it
        Message clientMsg = con.readReplyFromClient(MessageFactory.createGameOverMessage(gameInfo, 1.0, 0.0, clientInfo));
        assertEquals(clientMsg.getMsgType(), MessageProtocol.OP_TYPE_GAMEOVER);

        s1.close();
        s2.close();

        System.out.println("test complete");
    }
}
