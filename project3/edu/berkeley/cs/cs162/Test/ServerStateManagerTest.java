package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.Vector;

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
            testGame.setGameID(sm.createGameEntry(testGame));
            assertEquals(1, testGame.getGameID());

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

    @Test /* Tests recording of a stone move in the DB */
    public void testUpdateGameWithMove() {
    	BoardLocation loca = new BoardLocation(3, 2);
    	Vector<BoardLocation> capped = new Vector<BoardLocation>();
    	capped.add(new BoardLocation(2, 2));
    	ResultSet moveRes = null;
    	ResultSet cappedDB = null;
    	try {
    		sm.updateGameWithMove(testGame, p1, loca, capped);
    		moveRes = db.executeReadQuery("select * from moves where moveId=1");
    		 if(moveRes == null)
                 fail("There was an exception in the query");
             else if(!moveRes.next())
                 fail("The stone move was not recorded in the database");

             assertEquals(1, moveRes.getInt("gameId"));
             assertEquals(1, moveRes.getInt("clientId"));
             assertEquals(3, moveRes.getInt("x"));
             assertEquals(2, moveRes.getInt("y"));

             /**
              * This SHOULD be 1, but because we don't actually
              * execute the move inside the Game object, it will
              * be 0.
              */
             assertEquals(0, moveRes.getInt("moveNum"));
             db.closeReadQuery(moveRes);
             cappedDB = db.executeReadQuery("select * from captured_stones where moveID=1");
             if(cappedDB == null)
                 fail("There was an exception in the query");
             else if(!cappedDB.next())
                 fail("The captured stone was not recorded in the database");
             assertEquals(2, cappedDB.getInt("x"));
             assertEquals(2, cappedDB.getInt("y"));
             db.closeReadQuery(cappedDB);
    	}
        catch(SQLException e){
            e.printStackTrace();
            fail("SQL Exception in testUpdateGameWithMove");
        }
    }
    
    @Test /* Tests that the StateManager records a pass move in the database */
    public void testUpdateGameWithPass(){
        ResultSet results = null;
        try{
            sm.updateGameWithPass(testGame, p2);

            results = db.executeReadQuery("select * from moves where moveId=2");

            if(results == null)
                fail("There was an exception in the query");
            else if(!results.next())
                fail("The pass move was not recorded in the database");

            assertEquals(1, results.getInt("gameId"));
            assertEquals(2, results.getInt("clientId"));
            assertEquals(-1, results.getInt("x"));
            assertEquals(-1, results.getInt("y"));

            /**
             * This SHOULD be 1, but because we don't actually
             * execute the move inside the Game object, it will
             * be 0.
             */
            assertEquals(0, results.getInt("moveNum"));
        }
        catch(SQLException e){
            e.printStackTrace();
            fail("SQL Exception in testUpdateGameWithPassMove");
        }
        finally{
            if(results != null) db.closeReadQuery(results);
        }
    }

    @Test /* Tests that finishing the game fills out the correct fields */
    public void testFinishGame(){
        ResultSet results = null;
        try{
            sm.finishGame(1, p1, 120.5, 10.0, MessageProtocol.PLAYER_KO_RULE);

            results = db.executeReadQuery("select * from games where gameId=1");
            if(!results.next())
                fail("The game was not in the database.");

            assertEquals(1, results.getInt("blackPlayer"));
            assertEquals(1, results.getInt("winner"));
            assertEquals(120.5, results.getDouble("blackScore"), 0.0);
            assertEquals(10.0, results.getDouble("whiteScore"), 0.0);
            assertEquals((int)MessageProtocol.PLAYER_KO_RULE, results.getInt("reason"));
        }
        catch (SQLException e){
            e.printStackTrace();
            fail("There was an SQL Exception in testFinishGame");
        }
        finally{
            if(results != null) db.closeReadQuery(results);
        }
    }
}
