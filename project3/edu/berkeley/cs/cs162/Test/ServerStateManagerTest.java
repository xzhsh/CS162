package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import edu.berkeley.cs.cs162.Server.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

/**
 * Test the ServerStateManager.
 */

public class ServerStateManagerTest {

    static ServerStateManager sm;
    static AuthenticationManager am;
    static ClientInfo kunal;
    static String password;
    static DatabaseConnection db;
	static Game testGame;
	private static PlayerLogic p1;
	private static PlayerLogic p2;

    @BeforeClass
    public static void setup(){
        try{
            // Initialize Managers
            db = new DatabaseConnection("statemanager-test.db");
            sm = new ServerStateManager(db);
            am = new AuthenticationManager(db, "cs162project3istasty");

            // Initialize clients and set their IDs
            p1 = (PlayerLogic) PlayerLogic.getClientLogicForClientType(null, "Player1", MessageProtocol.TYPE_MACHINE, null);
            p2 = (PlayerLogic) PlayerLogic.getClientLogicForClientType(null, "Player2", MessageProtocol.TYPE_MACHINE, null);
            am.registerClient(p1.makeClientInfo(), "p1");
            am.registerClient(p2.makeClientInfo(), "p2");
            try{
                p1.setID(am.authenticateClient(p1.makeClientInfo(), "p1"));
                p2.setID(am.authenticateClient(p2.makeClientInfo(), "p2"));
            }
            catch (AuthenticationManager.ServerAuthenticationException e) {
                fail("The clients were not written correctly to the database.");
            }
        }
        catch (SQLException e) { fail("SQL Exception in setup method."); }

        // Initialize a test Game
        testGame = new Game("TestGame", p1, p2, new GoBoard(10));
    }

    @AfterClass
    public static void teardown() {
        db.wipeDatabase();
    }

    @Test /* Test that the Manager successfully creates a game entry in the database */
    public void testCreateGameEntry() throws SQLException {
        ResultSet results = null;
    	try {
            int gid = sm.createGameEntry(testGame);
            assertEquals(1, gid);

            results = db.executeReadQuery("select * from games where gameId=1");
            if(!results.next())
                fail("A Game entry should have been created.");

            assertEquals(1, results.getInt("blackPlayer"));
            assertEquals(2, results.getInt("whitePlayer"));
            assertEquals(10, results.getInt("boardSize"));
            assertEquals(0, results.getInt("moveNum"));
            assertEquals(0.0, results.getDouble("blackScore"), 0.0);
            assertEquals(0.0, results.getDouble("whiteScore"), 0.0);
            assertEquals(0, results.getInt("winner"));
            assertEquals(0, results.getInt("reason"));
        }
        catch(SQLException e) {
            e.printStackTrace();
            fail("There was an SQL Exception in testCreateGameEntry");
        }
        finally{
            if(results != null) db.closeReadQuery(results);
        }
    }
}
