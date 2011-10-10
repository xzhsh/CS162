package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;

import edu.berkeley.cs.cs162.Board;
import edu.berkeley.cs.cs162.GameServer;
import edu.berkeley.cs.cs162.Location;
import edu.berkeley.cs.cs162.Observer;
import edu.berkeley.cs.cs162.TestObserver;
import edu.berkeley.cs.cs162.TestPlayer;

public class StressTest {

	/**
	 * Test that runs 100 games simultaneously that will trigger the Ko rule.
	 * 
	 * This is the easily way to test a set number of moves got executed correctly.
	 * @throws InterruptedException
	 */
	@Test
	public void koStressTest() throws InterruptedException {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<TestObserver> testObs = new ArrayList<TestObserver>();

		GameServer server = new GameServer();
		Thread serverThread = new Thread(server);
		serverThread.start();
		for (int i = 0; i < 100; i++)
		{
			Board initialBoard = new Board(5);
			
			Vector<Observer> obs = new Vector<Observer>();
			TestObserver dave = new TestObserver(9);
			dave.setName("dave" + i);
			
			obs.add(dave);
			
			testObs.add(dave);
			TestPlayer whitePlayer = new TestPlayer();
			whitePlayer.setName("White" + i);
			whitePlayer.addMove(new Location(0,1));
			whitePlayer.addMove(new Location(1,0));
			whitePlayer.addMove(new Location(2,1));
			whitePlayer.addMove(new Location(1,2));
			
			TestPlayer blackPlayer = new TestPlayer();
			blackPlayer.setName("Black" + i);
			blackPlayer.addMove(new Location(1,3));
			blackPlayer.addMove(new Location(0,2));
			blackPlayer.addMove(new Location(2,2));
			blackPlayer.addMove(new Location(1,1));
			blackPlayer.addMove(new Location(1,1));
			
			Thread daveThread = new Thread(dave);
			Thread blackThread = new Thread(whitePlayer);
			Thread whiteThread = new Thread(blackPlayer);
			
			daveThread.start();
			
			blackThread.start();
			whiteThread.start();
			threads.add(daveThread);
			threads.add(blackThread);
			threads.add(whiteThread);
			
			server.createGame("KoRuleTest" + i, blackPlayer, whitePlayer, initialBoard, 10000, obs);
		}
		server.waitForGames();
		
		for(Thread t : threads)
		{
			t.join();
		}
		
		serverThread.join();
		int wrongCount = 0;
		for(TestObserver o : testObs)
		{
			if (!o.isCorrect())
			{
				wrongCount++;
				System.out.println(o.getMoves());
			}
		}
		assertEquals(wrongCount, 0);
	}
}
