package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;
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

public class IntegrationAuthTest {
	protected static final int TEST_PORT = 1234;
	protected static final String password = "plainTextPassword";
	protected static final String hash = Security.computeHash(password);
	protected static final String salt = "cs162project3istasty";
	
	protected static final ClientInfo cinfo = MessageFactory.createClientInfo("TestPlayer", MessageProtocol.TYPE_MACHINE);
	@Test
	public void test() throws InterruptedException, IOException, SQLException {
		try {
			//create the database and entries.
			DatabaseConnection database_connection = new DatabaseConnection("integration-auth-test.db");
			database_connection.wipeDatabase();
			
			String startupquery = "insert into clients (name, type, passwordHash) values ('"+ cinfo.getName() + "', " 
					+ MessageProtocol.TYPE_MACHINE + ", '" + Security.computeHashWithSalt(hash, salt) + "')";
			System.out.println(startupquery);
			database_connection.initializeDatabase();
			database_connection.startTransaction();
			database_connection.executeWriteQuery(startupquery);
			database_connection.finishTransaction();
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
		
		assertTrue("Client should be able to connect", connection.initiate3WayHandshake("localhost", TEST_PORT, 42));
		
		
		
		
		Message registerMessage = MessageFactory.createRegisterMessage(cinfo, password);
        Message registerResponse = connection.sendSyncToServer(registerMessage);
        assertTrue("Client info should already be registered in the database", registerResponse.getMsgType() == MessageProtocol.OP_ERROR_REJECTED);
        
        
        ClientInfo cinfo_2 = MessageFactory.createClientInfo("TestPlayer2", MessageProtocol.TYPE_MACHINE);
		String password = "plainTextPassword";
		
        Message connectMessage = MessageFactory.createConnectMessage(cinfo_2, password);
        Message connectResponse = connection.sendSyncToServer(connectMessage);
        assertTrue("The connection should fail.", connectResponse.getMsgType() != MessageProtocol.OP_STATUS_OK);
        
		Message registerMessage2 = MessageFactory.createRegisterMessage(cinfo_2, password);
        Message registerResponse2 = connection.sendSyncToServer(registerMessage2);
        assertTrue("The registration should go through.", registerResponse2.getMsgType() == MessageProtocol.OP_STATUS_OK);
        
        /**
        Message connectMessage = MessageFactory.createConnectMessage(clientInfo, password);
        Message connectResponse = connection.sendSyncToServer(connectMessage);
        
        return (connectResponse.isOK());*/
	}
}
