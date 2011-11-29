package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Synchronization.Lock;

/**
 * Test to see if half open connections properly times out.
 * 
 * @author xshi
 */
public class HalfOpenConnectionTest {
	
	protected static final int TEST_PORT = 1234;

	@Test
	public void test() throws UnknownHostException, IOException, InterruptedException {
		final OutputStream nullout = new NullOutputStream();
		final Lock lock = new Lock();
		lock.acquire();
		Thread t = new Thread() {
			public void run() {
				GameServer g = new GameServer(100, 5, new PrintStream(nullout));
				try {
					lock.release();
					g.waitForConnectionsOnPort(TEST_PORT, InetAddress.getByName("localhost"));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					throw new AssertionError(e);
				}
			}
		};
		t.start();
		lock.acquire();
		lock.release();
		Thread.sleep(10);
		System.out.println("Server Started");
		Random rng = new Random();
		Map<Socket, Integer> socketList = new HashMap<Socket,Integer>();
		for (int i = 0; i < 10; i++) {
			Socket s = new Socket("localhost", TEST_PORT);
			s.setSoTimeout(10);
			int syn = rng.nextInt();
			(new DataOutputStream(s.getOutputStream())).writeInt(syn);
			socketList.put(s,syn);
			System.out.println("Socket " + i + " connected with id = " + syn);
		}
		Thread.sleep(2000);
		//Test that all sockets are still open after 2 seconds
		for (Socket s : socketList.keySet())
		{
			System.out.println("Socket with id = " + socketList.get(s));
			try {
				assertFalse(s.getInputStream().read() == -1);
			}
			catch (IOException e) {
				assertFalse(s.isClosed());
			}
		}
		Thread.sleep(1000);
		//Test that all sockets are closed after 3 seconds.
		for (Socket s : socketList.keySet())
		{
			System.out.println("Socket with id = " + socketList.get(s));
			try {
				assertEquals(s.getInputStream().read(),-1);
			}
			catch (IOException e) {
				assertTrue(s.isClosed());
			}
		}
	}

}
