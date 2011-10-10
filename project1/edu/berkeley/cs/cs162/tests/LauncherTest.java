package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.junit.Test;

import edu.berkeley.cs.cs162.GameServer;
import edu.berkeley.cs.cs162.Launcher;
import edu.berkeley.cs.cs162.Observer;
import edu.berkeley.cs.cs162.TestObserver;
import edu.berkeley.cs.cs162.TestPlayer;


public class LauncherTest {
	private static final String SIMPLE_CONFIG = "Player MachinePlayer Patrik\nPlayer MachinePlayer Angela\nObserver TestLoggingObserver spy\nObserver PrintingObserver derp\nGame PatrikVsAngela Patrik Angela 10 1000 spy derp\n";
	private static final int NUM_GAMES = 100;
	
	@Test(timeout=10000)
	public void SingleGameTest() throws UnsupportedEncodingException {
		GameServer server = new GameServer();
		Thread serverThread = new Thread(server);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		HashMap<Observer, Thread> observerThreads = new HashMap<Observer, Thread>();
		serverThread.start();
		Launcher.loadFromConfigurationFile(new BufferedReader(new StringReader(SIMPLE_CONFIG)), server, observerThreads);
		server.redirectLoggingObserver("spy", output);
		
		server.waitForGames();
		Launcher.exitAndJoinAllThreads(serverThread, observerThreads);
		
		String log = output.toString("UTF-8");
		assertTrue(log.contains("Game over"));
	}
	
	@Test(timeout=100000)
	public void ManyGameTest() throws UnsupportedEncodingException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Observer TestLoggingObserver spy\n");
		
		for (int i = 0; i < NUM_GAMES; i++)
		{
			sb.append("Player MachinePlayer BlackPlayer" + i + "\n");
			sb.append("Player MachinePlayer WhitePlayer" + i + "\n");
			sb.append("Game Game" + i + " BlackPlayer" + i + " WhitePlayer" + i + " 5 1000 spy\n");
		}
		
		GameServer server = new GameServer();
		Thread serverThread = new Thread(server);
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		HashMap<Observer, Thread> observerThreads = new HashMap<Observer, Thread>();
		serverThread.start();
		Launcher.loadFromConfigurationFile(new BufferedReader(new StringReader(sb.toString())), server, observerThreads);
		server.redirectLoggingObserver("spy", output);
		
		server.waitForGames();
		Launcher.exitAndJoinAllThreads(serverThread, observerThreads);
		
		String log = output.toString("UTF-8");
		int index = 0;
		for (int i = 0; i < NUM_GAMES; i++)
		{
			index = log.indexOf("Game over", index);
			if (index == -1) { break; }
		}
		assertTrue(index > 0);
	}
	
	@Test(timeout=1000)
	public void EntityCreationTest() throws UnsupportedEncodingException, InterruptedException {
		
		StringBuilder sb = new StringBuilder();
		sb.append("Observer TestObserver spy1\n");
		sb.append("Observer TestObserver spy2\n");
		sb.append("Observer TestObserver spy3\n");
		sb.append("Player TestPlayer p1\n");
		sb.append("Player TestPlayer p2\n");
		
		GameServer server = new GameServer();
		Thread serverThread = new Thread(server);
		serverThread.start();//start the thread to consume messages
		HashMap<Observer, Thread> observerThreads = new HashMap<Observer, Thread>();
		
		TestPlayer.resetStatics();
		TestObserver.resetStatics();
		
		Launcher.loadFromConfigurationFile(new BufferedReader(new StringReader(sb.toString())), server, observerThreads);
		assertEquals(TestPlayer.getNumberOfInstantiatedPlayers(), 2);
		assertEquals(TestObserver.getNumberOfInstantiatedObservers(), 3);
		assertTrue(TestPlayer.hasPlayerWithName("p1"));
		assertTrue(TestPlayer.hasPlayerWithName("p2"));
		assertTrue(TestObserver.hasObserverWithName("spy1"));
		assertTrue(TestObserver.hasObserverWithName("spy2"));
		assertTrue(TestObserver.hasObserverWithName("spy3"));
	}
}
