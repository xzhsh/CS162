package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.ResultSet;
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
	protected static final String password = "password";
	protected static final String hash = Security.computeHash(password);
	protected static final String password2 = "plainTextPassword";
	protected static final String hash2 = Security.computeHash(password2);
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
		
		final GameServer server = new GameServer("integration-auth-test.db", 100, 5, new PrintStream(System.out));
		
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
		
		Message cpMessage = MessageFactory.createChangePasswordMessage(cinfo, hash2);
        
		Message registerMessage = MessageFactory.createRegisterMessage(cinfo, hash);
        Message registerResponse = connection.sendSyncToServer(registerMessage);
        assertTrue("Client info should already be registered in the database", registerResponse.getMsgType() == MessageProtocol.OP_ERROR_REJECTED);
        
        ClientInfo cinfo_2 = MessageFactory.createClientInfo("TestPlayer2", MessageProtocol.TYPE_MACHINE);
		
        Message connectMessage = MessageFactory.createConnectMessage(cinfo_2, hash);
        Message connectResponse = connection.sendSyncToServer(connectMessage);
        assertTrue("The connection should fail.", connectResponse.getMsgType() != MessageProtocol.OP_STATUS_OK);
        
		Message registerMessage2 = MessageFactory.createRegisterMessage(cinfo_2, hash);
        Message registerResponse2 = connection.sendSyncToServer(registerMessage2);
        assertTrue("The registration should go through.", registerResponse2.getMsgType() == MessageProtocol.OP_STATUS_OK);
        
        Message cpResponse = connection.sendSyncToServer(cpMessage);
        assertTrue("The change should fail.", cpResponse.getMsgType() == MessageProtocol.OP_ERROR_UNCONNECTED);
        
        assertTrue("Client should be able to connect", connection.initiate3WayHandshake("localhost", TEST_PORT, 43));
        
        Message connectMessage2 = MessageFactory.createConnectMessage(cinfo, hash);
        Message connectResponse2 = connection.sendSyncToServer(connectMessage2);
        assertTrue("The connection should go through.", connectResponse2.getMsgType() == MessageProtocol.OP_STATUS_OK);
        
        Message cpResponse2 = connection.sendSyncToServer(MessageFactory.createChangePasswordMessage(cinfo_2, hash2));
        assertTrue("The change should fail.", cpResponse2.getMsgType() == MessageProtocol.OP_ERROR_REJECTED);
        
        Message cpResponse4 = connection.sendSyncToServer(cpMessage);
        assertTrue("The change should go through.", cpResponse4.getMsgType() == MessageProtocol.OP_STATUS_OK);
        
        try {
			//create the database and entries.
			DatabaseConnection database_connection = new DatabaseConnection("integration-auth-test.db");
			String query = "SELECT passwordHash FROM clients WHERE name='" + cinfo.getName()  +"'";
			ResultSet rs = database_connection.executeReadQuery(query);
			assertTrue(rs.next());
			assertEquals(rs.getString("passwordHash"), Security.computeHashWithSalt(hash2, salt));
			database_connection.closeReadQuery(rs);
			database_connection.close();
		} catch (SQLException e) {
			assertFalse(e.getMessage(), true);
		}
	}
}
