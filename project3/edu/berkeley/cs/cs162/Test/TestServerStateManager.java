package edu.berkeley.cs.cs162.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.ClientLogic;
import edu.berkeley.cs.cs162.Server.DatabaseConnection;
import edu.berkeley.cs.cs162.Server.Game;
import edu.berkeley.cs.cs162.Server.ServerStateManager;
import edu.berkeley.cs.cs162.Server.UnfinishedGame;

public class TestServerStateManager extends ServerStateManager {

	private ByteArrayOutputStream baos;
	private PrintStream ps;
	int idCount = 0;
	public TestServerStateManager(DatabaseConnection connection) {
		super(connection);
		baos = new ByteArrayOutputStream();
		ps = new PrintStream(baos);
	}
	
	/**
	 * Creates an entry in the games table for this specific game.
	 * @param game
	 * @return the gameID of the entry created.
	 */
	public int createGameEntry (Game game) throws SQLException {
		ps.println("Created game entry for game " + game.getName());
		return idCount++;
	}
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
		ps.println("Updated game " + game.getName() + " with move by " +client + " at "  + loc);
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		ps.println("Updated game " + game.getName() + " with pass by " + client);
	}
	
	public void finishGame(int gameId, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
		ps.println("Finished game with id " + gameId + " with " + winner+ " as winner. Final score "  + blackScore + " - " + whiteScore + " with reason byte: " + reason);
	}
	
	/**
	 * Constructs and loads a list of unfinished games by reading from the database.
	 * 
	 * @return a list of unfinished games.
	 * @throws SQLException
	 */
	public List<UnfinishedGame> loadUnfinishedGames() throws SQLException{
		//TODO fill in
		return new ArrayList<UnfinishedGame>();
	}
}
