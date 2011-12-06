package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

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
     * @throws SQLException if it fails at this point.
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

        return connection.getGameID(game);
	}
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
        int gameID = connection.getGameID(game);
        int playerID = connection.getPlayerID(client.getName());
        int moveNum = connection.getMoveNum(gameID) + 1;
        int moveType = (int) MessageProtocol.MOVE_STONE;
        int x = loc.getX();
        int y = loc.getY();

        connection.startTransaction();
        try {
            connection.executeWriteQuery("insert into moves (clientId, gameId, moveType, x, y, moveNum) values (" + playerID + ", " + gameID + ", " + moveType + ", " + x + ", " + y + ", " + moveNum + ")");
            connection.executeWriteQuery("update games set moveNum=" + moveNum + " where gameId=" + gameID);
            for(BoardLocation location : capturedStones){
                // TODO Write the captured stones to the database
            }
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		int gameID = connection.getGameID(game);
        int playerID = connection.getPlayerID(client.getName());
        int moveNum = connection.getMoveNum(gameID) + 1;
        int moveType = (int) MessageProtocol.MOVE_PASS;

        connection.startTransaction();
        try{
            connection.executeWriteQuery("insert into moves (clientId, gameId, moveType, moveNum) values (" + playerID + ", " + gameID + ", " + moveType + ", " + moveNum + ")");
            connection.executeWriteQuery("update games set moveNum=" + moveNum + " where gameId=" + gameID);
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	public void finishGame(int gameId, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
		int winnerID = connection.getPlayerID(winner.getName());

        connection.startTransaction();
        try{
            connection.executeWriteQuery("update games set winner=" + winnerID + ", blackScore=" + blackScore + ", whiteScore=" + whiteScore + ", reason=" + reason + " where gameId=" + gameId);
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	/**
	 * Constructs and loads a list of unfinished games by reading from the database.
	 * 
	 * @return a list of unfinished games.
	 * @throws SQLException
	 */
	public List<UnfinishedGame> loadUnfinishedGames() throws SQLException{
		ArrayList<UnfinishedGame> unfinishedGames = new ArrayList<UnfinishedGame>();

        String getUnfinishedGamesQuery = "SELECT * FROM games WHERE winner IS NULL";

        return unfinishedGames;
	}
}