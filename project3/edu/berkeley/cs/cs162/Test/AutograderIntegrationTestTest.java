package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.GameServer;
/**
 * Tests that the autograder integration tests' open and close socket works.
 * @author xshi
 *
 */
public class AutograderIntegrationTestTest {
	private static final int TEST_PORT = 1234;

	@Test(timeout=5000)
	public void test() throws InterruptedException {
		final GameServer server = new GameServer("server-stress-test.db", 100, 5, new PrintStream(System.out));
		
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
		while (true) {
			try {
				Socket newSocket = new Socket("localhost", 1234);
				newSocket.close();
				break;
			} catch (UnknownHostException e) {
				assertTrue(false);
			} catch (IOException e) {
				//not done yet, print and keep going.
				System.out.println(e.getMessage());
			}
			Thread.sleep(10);
		}
	}
}
