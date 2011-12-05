package edu.berkeley.cs.cs162.Server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ServerStateManager {
	@SuppressWarnings("unused")
	private DatabaseConnection connection;
	
	//Game id here. load these at the start and use them to populate updates/games.
	protected int gameId;
	protected int moveId;
	protected int capturedId;
	
	public ServerStateManager(DatabaseConnection connection) {
		this.connection = connection;
	}
	
	/**
	 * Creates an entry in the games table for this specific game.
	 * @param game
	 * @return the gameID of the entry created.
	 */
	public int createGameEntry (Game game) throws SQLException {
        int whiteID = connection.getPlayerID(game.getWhitePlayer().getName());
        int blackID = connection.getPlayerID(game.getBlackPlayer().getName());
        int boardSize = game.getBoardSize();

        connection.startTransaction();
        try {
            connection.executeWriteQuery("insert into games (blackPlayer, whitePlayer, boardSize, moveNum) values (" + blackID + ", " + whiteID + ", " + boardSize + ", 0)");
            connection.finishTransaction();
        }
        catch(SQLException e){
            connection.abortTransaction();
            throw e;
        }

        return connection.getGameID(blackID, whiteID);
	}
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		throw new RuntimeException("Unimplemented Method");
	}
	
	public void finishGame(int gameId, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
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