package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
<<<<<<< HEAD
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import edu.berkeley.cs.cs162.Client.*;
import edu.berkeley.cs.cs162.Server.*;
import edu.berkeley.cs.cs162.Writable.*;

import java.net.InetAddress;
import java.net.UnknownHostException;


import org.junit.Test;
import org.junit.Test.Assert.*;

import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

/**
 * Harvey
 */
public class HumanPlayerTimeoutTest {
    @Test
    public void testPlayerTimeout() throws IOException, InterruptedException {


        String address = "localhost";
        final int port = 12345;

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

        ClientConnection connection = new ClientConnection(sock1, sock2, 0, new PrintStream(new NullOutputStream()));
        connection.setValid();

        GameInfo gameInfo = MessageFactory.createGameInfo("test_game");
        BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(9));
        ClientInfo cInfo = MessageFactory.createHumanPlayerClientInfo("test_human");
        ClientInfo opp = MessageFactory.createMachinePlayerClientInfo("opponentDummy");

        System.out.println("beginning message to client");
        //send a "get move" message to MachinePlayer
        connection.sendToClient(MessageFactory.createGetMoveMessage());

        System.out.println("pause for 20 seconds");
      
//        Thread.sleep(30000);

        //check if player gets a "game over"
        
        Message clientMsg = connection.readReplyFromClient(MessageFactory.createGameOverMessage(gameInfo, 1.0, 0.0, cInfo));
        assertEquals(clientMsg.getMsgType(), MessageProtocol.OP_TYPE_GAMEOVER);

        sock1.close();
        sock2.close();

    	final String address = "localhost";
        final int port = 1234;
		byte type = MessageProtocol.TYPE_MACHINE;
		int timeout = 3000;
		
        final GameServer server =new GameServer(100, 5, new PrintStream(new NullOutputStream()));
        Thread serverThread = new Thread() {
			public void run() {
				try {
					server.waitForConnectionsOnPort(port, InetAddress.getByName(address));
				} catch (UnknownHostException e) {
					throw new AssertionError(e);
				}
			}
		};
		serverThread.start();
		//waits for the server to set up
		while (!server.isReady()) {Thread.sleep(10);}
		
		
		final TimeOutTestPlayer p1 = new TimeOutTestPlayer("p1", type, timeout);
		final TimeOutTestPlayer p2 = new TimeOutTestPlayer("p2", type, timeout);
		
		//create the timeoutplayers
		Thread t1 = TimeOutTestPlayer.runInstance(p1, port);
		Thread t2 = TimeOutTestPlayer.runInstance(p2, port);
		
		//launch the players
		t1.join();
		t2.join();
		//one of them should have the message we are looking for.
		assertTrue(p1.getSuccess() || p2.getSuccess());
        System.out.println("test complete");

    }
}
