package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.berkeley.cs.cs162.Server.DatabaseConnection;
import edu.berkeley.cs.cs162.Server.Game;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.PlayerLogic;
import edu.berkeley.cs.cs162.Server.ServerStateManager;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

/**
 * Test the ServerStateManager.
 */

public class ServerStateManagerTest {

    static ServerStateManager sm;
    static ClientInfo kunal;
    static String password;
    static DatabaseConnection db;
	static Game testGame;
	private static PlayerLogic p1;
	private static PlayerLogic p2;

    @BeforeClass
    public static void setup(){
        try{
            db = new DatabaseConnection("statemanager-test.db");
            //db.initializeDatabase();
            sm = new ServerStateManager(db);
        }
        catch (SQLException e) { fail("SQL Exception in setup method."); }

        p1 = (PlayerLogic) PlayerLogic.getClientLogicForClientType(null, "Player1", MessageProtocol.TYPE_MACHINE, null);
        p2 = (PlayerLogic) PlayerLogic.getClientLogicForClientType(null, "Player2", MessageProtocol.TYPE_MACHINE, null);
        testGame = new Game("TestGame", p1, p2, new GoBoard(10));
    }

    @AfterClass
    public static void teardown() {
        db.wipeDatabase();
    }

    @Test /* Test that a client can successfully register, and cannot register twice. */
    public void testCreateGameEntry() throws SQLException {
    	sm.createGameEntry(testGame);
    }
}
