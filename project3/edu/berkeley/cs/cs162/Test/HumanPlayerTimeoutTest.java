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
 * Harvey
 */
public class HumanPlayerTimeoutTest {
    @Test
    public void test() throws IOException, InterruptedException {
    	final String address = "localhost";
        final int port = 1337;
		byte type = MessageProtocol.TYPE_HUMAN;
		int pTimeout = 31000;
		
        final GameServer server =new GameServer("HumanPlayerTest.db",100, 5, new PrintStream(new NullOutputStream()));
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
		
		
		final TimeOutTestPlayer p1 = new TimeOutTestPlayer("p1", type, pTimeout);
		final TimeOutTestPlayer p2 = new TimeOutTestPlayer("p2", type, pTimeout);
		
		//create the timeoutplayers
		Thread t1 = TimeOutTestPlayer.runInstance(p1, port);
		Thread t2 = TimeOutTestPlayer.runInstance(p2, port);
		
		//launch the players
		t1.join();
		t2.join();
		//one of them should have the message we are looking for.
		assertTrue(p1.getSuccess() || p2.getSuccess());
        System.out.println("Player Timeout Worked");

    }
}
