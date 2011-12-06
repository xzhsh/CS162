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

import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class ClientLimitTest {
	private static final int TEST_PORT = 1234;
	private static final int NUM_PLAYERS = 100;

	@Test(timeout=2000)
	public void test() throws InterruptedException {
		final GameServer server = new GameServer("edu.berkeley.cs.cs162.Test.server-stress-test.db", NUM_PLAYERS, 5, new PrintStream(System.out), new TestAuthenticationManager(null, "SALTY"), new TestServerStateManager(null));
		
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
		lock.writeLock();
		for (int i = 0; i < NUM_PLAYERS + 1; i++)
		{
			List<Message> moves = Arrays.asList(MessageFactory.createGetMoveStatusOkMessage(MessageProtocol.MOVE_PASS, MessageFactory.createLocationInfo(0, 0)));
			System.out.println(">>>> STARTING TestPlayer"+ i + ".");
			threads.add(TestPlayer.runInstance("TestPlayer"+i, MessageProtocol.TYPE_MACHINE, moves, TEST_PORT, lock, sharedCount));
		}
		Thread.sleep(1000);
		lock.writeUnlock();
		for (Thread thread : threads)
		{
			thread.join();
		}

		assertEquals(sharedCount.get(), NUM_PLAYERS);
	}
}
