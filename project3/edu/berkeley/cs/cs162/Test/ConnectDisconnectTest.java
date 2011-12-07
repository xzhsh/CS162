package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;

import org.junit.Test;

import edu.berkeley.cs.cs162.Client.ServerConnection;
import edu.berkeley.cs.cs162.Server.DatabaseConnection;
import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Server.Security;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class ConnectDisconnectTest {

	protected static final String password = "password";
	protected static final String hash = Security.computeHash(password);
	protected static final ClientInfo cinfo = MessageFactory.createClientInfo("TestPlayer", MessageProtocol.TYPE_MACHINE);
	
	private static final int TEST_PORT = 1234;
	
	@Test(timeout=5000)
	public void testConnectDisconnect() throws InterruptedException, IOException {
		try {
			//create the database and entries.
			DatabaseConnection database_connection = new DatabaseConnection("Connect-disconnect-test.db");
			database_connection.wipeDatabase();
			database_connection.close();
		} catch (SQLException e) {
			assertFalse(e.getMessage(), true);
		}
		
		final GameServer server = new GameServer("Connect-disconnect-test.db", 100, 5, new PrintStream(System.out));
		
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
			Thread.sleep(10);
		}
		ServerConnection connection = new ServerConnection();
		assertTrue("Client should be able to connect", connection.initiate3WayHandshake("localhost", 1234, 42));
		Message registerMessage = MessageFactory.createRegisterMessage(cinfo, hash);
        Message registerResponse = connection.sendSyncToServer(registerMessage);
        assertTrue("Registration should go through", registerResponse.getMsgType() == MessageProtocol.OP_STATUS_OK);
        
        Message connectMessage = MessageFactory.createConnectMessage(cinfo, hash);
        Message connectResponse = connection.sendSyncToServer(connectMessage);
        assertTrue("The connection should go thorugh.", connectResponse.getMsgType() == MessageProtocol.OP_STATUS_OK);
        connection.sendDisconnectToServer();
		Thread.sleep(1000);
		connection.close();
	}
}
