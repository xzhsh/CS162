package edu.berkeley.cs.cs162.Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ServerStateManager {
	private DatabaseConnection connection;
	
	public ServerStateManager(DatabaseConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Creates an entry in the games table for this specific game.
	 * @param game
	 */
	public void createGameEntry (Game game) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
	}
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
	}
	
	public void finishGame(Game game, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
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