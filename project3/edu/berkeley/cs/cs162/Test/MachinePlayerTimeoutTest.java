package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/8/11
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachinePlayerTimeoutTest {
    @Test
    public void test() throws IOException, InterruptedException {
        final String address = "localhost";
        final int port = 1234;
		byte type = MessageProtocol.TYPE_MACHINE;
		int timeout = 3000;
		
        final GameServer server =new GameServer("MachinePlayerTest.db", 100, 5, new PrintStream(new NullOutputStream()));
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
