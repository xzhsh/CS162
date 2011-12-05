package edu.berkeley.cs.cs162.Server;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import edu.berkeley.cs.cs162.Writable.*;


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