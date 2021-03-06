package edu.berkeley.cs.cs162.Test;
import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.StoneColor;
import edu.berkeley.cs.cs162.Server.UnfinishedGame;
import edu.berkeley.cs.cs162.Server.GoBoard.IllegalMoveException;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class ReconnectionServerTest {
	private static final int TEST_PORT = 1234;
	private static final int NUM_PLAYERS = 2;

	@Test
	public void test() throws InterruptedException {
		final TestAuthenticationManager am = new TestAuthenticationManager(null, null);
		final TestServerStateManager sm = new TestServerStateManager(null);
		
		GoBoard board = new GoBoard(10);
		try {
			board.makeMove(new BoardLocation(1, 0), StoneColor.BLACK);
			board.makeMove(new BoardLocation(2, 2), StoneColor.WHITE);
			board.makeMove(new BoardLocation(1, 1), StoneColor.BLACK);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		sm.addUnfinishedGame(new UnfinishedGame("TestGameReconnect", board, 
				MessageFactory.createClientInfo("TestPlayer0", MessageProtocol.TYPE_MACHINE), 
				MessageFactory.createClientInfo("TestPlayer1", MessageProtocol.TYPE_MACHINE), 60, 0));
		
		final GameServer server = new GameServer("edu.berkeley.cs.cs162.Test.one-game-server-test.db", 100, 5, new PrintStream(System.out), am, sm);
		Thread t = new Thread() {
			public void run() {
				try {
					server.waitForConnectionsOnPort(TEST_PORT, InetAddress.getByName("localhost"));
				} catch (UnknownHostException e) {
					throw new AssertionError(e);
				}
			}
		};
		
		t.start();
		while (!server.isReady()) {
			Thread.sleep(10);//waits until server is ready;
		}
		
		ReaderWriterLock lock = new ReaderWriterLock();
		List<Thread> threads = new ArrayList<Thread>();
		AtomicInteger sharedCount = new AtomicInteger(0);
		for (int i = 0; i < NUM_PLAYERS; i++)
		{
			List<Message> moves = Arrays.asList(MessageFactory.createGetMoveStatusOkMessage(MessageProtocol.MOVE_PASS, MessageFactory.createLocationInfo(0, 0)));
			System.out.println(">>>> STARTING TestPlayer"+ i + ".");
			threads.add(TestPlayer.runInstance("TestPlayer"+i, MessageProtocol.TYPE_MACHINE, moves, TEST_PORT, lock, sharedCount));
		}
		
		for (Thread thread : threads)
		{
			thread.join();
		}
		
		assertEquals(sharedCount.get(), NUM_PLAYERS);
		Thread.sleep(100);
		System.out.println("Auth:");
		System.out.println(am.baos.toString());
		System.out.println("State:");
		//String stateString = sm.baos.toString();
		System.out.println(sm.baos.toString());
	}
}
