package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.assertEquals;

import java.util.Vector;

import org.junit.Test;

import edu.berkeley.cs.cs162.Board;
import edu.berkeley.cs.cs162.GameServer;
import edu.berkeley.cs.cs162.Location;
import edu.berkeley.cs.cs162.Observer;
import edu.berkeley.cs.cs162.TestObserver;
import edu.berkeley.cs.cs162.TestPlayer;

/**
 * Tests the Observer class
 */
public class ObserverTest {

    @Test
    public void testReceiveMessages() throws InterruptedException{

        GameServer server = new GameServer();
	    Board board = new Board(3);

        Vector<Observer> obs = new Vector<Observer>();
        TestObserver dave = new TestObserver(2);
		dave.setName("dave");
		obs.add(dave);

        TestPlayer player1 = new TestPlayer();
		player1.setName("Black");
		player1.addMove(new Location(1,2));
        player1.addPass();

        TestPlayer player2 = new TestPlayer();
		player2.setName("White");
		player2.addPass();

        Thread daveThread = new Thread(dave);
		daveThread.start();
        (new Thread(player1)).start();
		(new Thread(player2)).start();
		
        Thread serverThread = new Thread(server);
		serverThread.start();
        server.createGame("ObserverTest", player1, player2, board, 1000, obs);
		server.waitForGames();
		daveThread.join();
        String[] expectedMessages = {"gs", "mm", "sp", "mm", "pp", "mm", "pp", "go"};
        
        System.out.println("Messages received:");
        for (String s : dave.getMessagesReceived())
    	{
    		System.out.print(s + " ");
    	}
        System.out.println();
        assertEquals(expectedMessages.length, dave.getMessagesReceived().size());
        for (int i = 0; i < expectedMessages.length; i++)
        {
            assertEquals(expectedMessages[i], dave.getMessagesReceived().get(i));
        }
    }
}
