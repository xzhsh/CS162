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

public class ServerStressTest {
	private static final int TEST_PORT = 1234;
	private static final int NUM_PLAYERS = 100;

	@Test
	public void test() throws InterruptedException {
<<<<<<< HEAD
		final GameServer server = new GameServer(100, 5, new PrintStream(new NullOutputStream()));
=======
		final GameServer server = new GameServer(100, 5, new PrintStream(System.out));//new NullOutputStream()));
>>>>>>> b70978eefef586adb4b41ceaa2cc1fb5f06c2212
		
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
	}
}
