package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Vector;

import org.junit.Test;

import edu.berkeley.cs.cs162.Board;
import edu.berkeley.cs.cs162.Game;
import edu.berkeley.cs.cs162.GameServer;
import edu.berkeley.cs.cs162.Location;
import edu.berkeley.cs.cs162.Observer;
import edu.berkeley.cs.cs162.Player;
import edu.berkeley.cs.cs162.PrintingObserver;
import edu.berkeley.cs.cs162.StoneColor;
import edu.berkeley.cs.cs162.TestObserver;
import edu.berkeley.cs.cs162.TestPlayer;

public class GameTest {
	@Test(timeout = 1000)
	public void KoRuleTest() throws InterruptedException {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		
		for (int i = 0; i < 100; i++)
		{
			final int index = i;
			threads.add(new Thread(new Runnable() {
				public void run() {
					GameServer server = new GameServer();
					Board initialBoard = new Board(5);
					
					initialBoard.addStone(new Location(0,1), StoneColor.BLACK);
					initialBoard.addStone(new Location(1,0), StoneColor.BLACK);
					initialBoard.addStone(new Location(2,1), StoneColor.BLACK);
					initialBoard.addStone(new Location(1,1), StoneColor.WHITE);
					initialBoard.addStone(new Location(0,2), StoneColor.WHITE);
					initialBoard.addStone(new Location(2,2), StoneColor.WHITE);
					initialBoard.addStone(new Location(1,3), StoneColor.WHITE);
					
					String daveName = "dave" + index;
					String blackName = "Black" + index;
					String whiteName = "White" + index;
					String derpName = "derp" + index;
					
					Vector<Observer> obs = new Vector<Observer>();
					TestObserver dave = new TestObserver(2);
					dave.setName(daveName);
					obs.add(dave);
					TestPlayer player1 = new TestPlayer();
					player1.setName(blackName);
					player1.addMove(new Location(1,2));
					
					PrintingObserver derp = new PrintingObserver();
					derp.setName(derpName);
					obs.add(derp);
					
					TestPlayer player2 = new TestPlayer();
					player2.setName(whiteName);
					player2.addMove(new Location(1,1));
					
					Thread daveThread = new Thread(dave);
					daveThread.start();
					Thread derpThread = new Thread(derp);
					derpThread.start();
					(new Thread(player1)).start();
					(new Thread(player2)).start();
					
					Thread serverThread = new Thread(server);
					serverThread.start();
					server.createGame("KoRuleTest" + index, player1, player2, initialBoard, 1000, obs);
					server.waitForGames();
					
					try {
						derpThread.join();
						daveThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						assertTrue(false);
					}
					
					assertTrue(dave.isCorrect());
				}
			}));
		}
		for(Thread t : threads)
		{
			t.start();
		}
		for(Thread t : threads)
		{
			t.join();
		}
	}

	@Test(timeout = 10000)
	public void PassRuleTest() throws InterruptedException {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 100; i++)
		{
			threads.add(new Thread(new Runnable() {
				public void run() {
					GameServer server = new GameServer();
					Board initialBoard = new Board(5);
					
					Vector<Observer> obs = new Vector<Observer>();
					TestObserver dave = new TestObserver(2);
					dave.setName("dave");
					obs.add(dave);
					TestPlayer player1 = new TestPlayer();
					player1.setName("Black");
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
					server.createGame("KoRuleTest", player1, player2, initialBoard, 1000, obs);
					server.waitForGames();
					try {
						daveThread.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						assertTrue(false);
					}					
					assertTrue(dave.isCorrect());
				}
			}));
		}
		for(Thread t : threads)
		{
			t.start();
		}
		for(Thread t : threads)
		{
			t.join();
		}
	}
	
	@Test
	public void GameMessageFunctionalityTest() throws InterruptedException
	{
		Board initialBoard = new Board(5);
		
		class GameTestServer extends GameServer{
			StringBuilder sb = new StringBuilder();
			boolean ready = true;
			public void sendGameStart(String targetObserverName, Game game, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) 
			{
				sb.append("GAMESTARTMESSAGE: " + targetObserverName + " " + game.getName() + " " + blackPlayerName + " " + whitePlayerName + " " + board.getSize() + " " + moveTimeoutInMs + "\n");
			}

		    public void sendGameOver(String targetObserverName, Game game, double blackPlayerScore, double whitePlayerScore) {
		    	sb.append("GAMEENDMESSAGE: " + targetObserverName + " " + game.getName() + " " + blackPlayerScore + " " + whitePlayerScore + "\n");
			}

		    public void sendMakeMove(String targetObserverName, Game game, String playerName) {
		    	sb.append("MAKEMOVE: " + targetObserverName + " " + game.getName() + " " + playerName + " " + "\n");
		    	ready = true;
			}

		    public void sendStonePlaced(String targetObserverName, Game game, String playerName, Location loc, StoneColor color) {
			}

		    public void sendStoneCaptured(String targetObserverName, Game game, String playerName, Location loc, StoneColor color) {
			}

		    public void sendPlayerPass(String targetObserverName, Game game, String playerName) {
		    	sb.append("PASSED: " + targetObserverName + " " + game.getName() + " " + playerName + " " + "\n");
			}

		    public void sendPlayerError(String targetObserverName, Game game, String playerName, String errorDescription) {
			}

		    /**
		       Methods for players to send move information to games. Players may only notify
		       games of moves they want to make by calling these methods.
		     */
		    
		    public void sendMoveToGame(Player player, String gameName, Location loc) {
			}

		    public void sendPassMoveToGame(Player player, String gameName) {
			}
		}
		GameTestServer server = new GameTestServer();
		
		Game game = new Game(server, "Game", "Black", "White", initialBoard, 1000);
		game.addObserver("Obs");
		Thread thread = new Thread(game);
		thread.start();
		game.makePassMove("Black");
		while(!server.ready) { Thread.sleep(10);}
		game.makePassMove("White");
		thread.join();
		System.out.println(server.sb);
		String correctString = "GAMESTARTMESSAGE: Black Game Black White 5 1000\nGAMESTARTMESSAGE: White Game Black White 5 1000\nGAMESTARTMESSAGE: Obs Game Black White 5 1000\nMAKEMOVE: Black Game Black \nMAKEMOVE: White Game Black \nMAKEMOVE: Obs Game Black \nPASSED: Black Game Black \nPASSED: White Game Black \nPASSED: Obs Game Black \nMAKEMOVE: Black Game White \nMAKEMOVE: White Game White \nMAKEMOVE: Obs Game White \nPASSED: Black Game White \nPASSED: White Game White \nPASSED: Obs Game White \nGAMEENDMESSAGE: Black Game 0.0 0.0\nGAMEENDMESSAGE: White Game 0.0 0.0\nGAMEENDMESSAGE: Obs Game 0.0 0.0\n";
		assertEquals(server.sb.toString(), correctString);
	}
}
