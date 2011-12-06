package edu.berkeley.cs.cs162.Server;


import java.sql.ResultSet;
=======
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

>>>>>>> f072ed04f3ac3a3bd0431b2743df2d43785c8d07
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import edu.berkeley.cs.cs162.Writable.*;


public class ServerStateManager {
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

		
		String bName = game.getBlackPlayer().getName();
		String wName = game.getWhitePlayer().getName();
		int bSize = game.getBoard().getSize();
		int mNum = game.getBoard().getNumberOfMoves();
		
			String getBIDQuery = "SELECT clientID FROM clients WHERE name='" + bName + "'";
			String getWIDQuery = "SELECT clientID FROM clients WHERE name='" + wName + "'";
			ResultSet bPlayer = connection.executeReadQuery(getBIDQuery);
			bPlayer.next();
			int bPlayID = bPlayer.getInt("clientID");
			connection.closeReadQuery(bPlayer);
			ResultSet wPlayer = connection.executeReadQuery(getWIDQuery);
			wPlayer.next();
			int wPlayID = wPlayer.getInt("clientID");
			connection.closeReadQuery(wPlayer);
					
			String addGameQuery = "INSERT INTO games(blackPlayer whitePlayer boardsize moveNum) VALUES(" + bPlayID + "" + wPlayID + "" + Integer.toString(bSize) + Integer.toString(mNum) + ")";
			connection.startTransaction();
			try {
				connection.executeWriteQuery(addGameQuery);			
				ResultSet recentGame = connection.executeReadQuery("SELECT max(gameID) FROM games");							
				int gID =  recentGame.getInt(0);
				connection.closeReadQuery(recentGame);
				connection.finishTransaction();
				return gID;
			}
			catch (SQLException e) {
				connection.abortTransaction();
				return -1;
			}
		
		//throw new RuntimeException("Unimplemented Method");
	}
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
		String mType = Byte.toString(MessageProtocol.MOVE_PASS);
		String gName = game.getName();
		String pName = client.getName();
		int xCoord = loc.getX();
		int yCoord = loc.getY();
		
		String getPIDQuery = "SELECT clientID FROM clients WHERE name = '" + pName + "'";
		ResultSet playerID = connection.executeReadQuery(getPIDQuery);
		playerID.next();
		String pID = Integer.toString(playerID.getInt("clientID"));
		connection.closeReadQuery(playerID);
		String mNum = Integer.toString(game.getBoard().getNumberOfMoves());
		String gID = "how do I get this?";
		String addMoveQuery = "INSERT INTO moves (clientID gameID moveType x y moveNum) VALUES (" + pID + "" + gID + "" + mType + "" + xCoord + "" + yCoord + "" + mNum + ")";
		try {
			connection.executeWriteQuery(addMoveQuery);
			connection.finishTransaction();
		}
        catch (SQLException e) {
        	connection.abortTransaction();
        	
        }
        return;
		// throw new RuntimeException("Unimplemented Method");
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		String mType = Byte.toString(MessageProtocol.MOVE_PASS);
		String gName = game.getName();
		String pName = client.getName();
		
		String getPIDQuery = "SELECT clientID FROM clients WHERE name = '" + pName + "'";
		ResultSet playerID = connection.executeReadQuery(getPIDQuery);
		playerID.next();
		String pID = Integer.toString(playerID.getInt("clientID"));
		connection.closeReadQuery(playerID);
		String mNum = Integer.toString(game.getBoard().getNumberOfMoves());
		String gID = "how do I get this?";
		String addMoveQuery = "INSERT INTO moves (clientID gameID moveType moveNum) VALUES (" + pID + "" + gID + "" + mType + "" + mNum + ")";
		try {
			connection.executeWriteQuery(addMoveQuery);
			connection.finishTransaction();
		}
        catch (SQLException e) {
        	connection.abortTransaction();
        	
        }
        return;
		// throw new RuntimeException("Unimplemented Method");
	}
	
	public void finishGame(int gId, ClientLogic winner, double blackScore, double whiteScore, int reasn) throws SQLException {
		String pName = winner.getName();
		String wIDQuery = "SELECT clientID FROM clients WHERE name = '" + pName + "'";
		ResultSet winnerID = connection.executeReadQuery(wIDQuery);
		winnerID.next();
        int wID = winnerID.getInt("clientID");
        connection.closeReadQuery(winnerID);
        connection.startTransaction();
        try {
        	String finGameQuery = "UPDATE games SET winner = " + Integer.toString(wID) + ", blackScore= " + Double.toString(blackScore) + ", whiteScore= " + Double.toString(whiteScore) + ", reason= " + Integer.toString(reasn) + " WHERE gameID='" + Integer.toString(gID) + "'";
        	connection.executeWriteQuery(finGameQuery);
        	connection.finishTransaction();
        }
        catch (SQLException e) {
        	connection.abortTransaction();
        	
        }
        return;
		// throw new RuntimeException("Unimplemented Method");

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
		//TODO fill in
		ArrayList<UnfinishedGame> unfinishGames = new ArrayList<UnfinishedGame>();
		String getUnfinGames = "SELECT * FROM games WHERE winner is null";
		ClientInfo bPlayer = null;
		ClientInfo wPlayer = null;
		// connection.startTransaction();	
		
		return unfinishGames;
	}
}