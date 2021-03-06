package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Server.GoBoard.IllegalMoveException;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
        int whiteID = game.getWhitePlayer().getID();
        int blackID = game.getBlackPlayer().getID();
        int boardSize = game.getBoardSize();

        connection.startTransaction();
        try {
            int gID = connection.executeWriteQuery("insert into games (blackPlayer, whitePlayer, boardSize, moveNum) values (" + blackID + ", " + whiteID + ", " + boardSize + ", 0)");
            assert gID != -1;
            connection.finishTransaction();
            return gID;
        }
        catch(SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	public void updateGameWithForfeitMove(Game game, ClientLogic client) throws SQLException {
		int gameID = game.getGameID();
        int playerID = client.getID();
        int moveNum = game.getBoard().getNumberOfMoves();
        int moveType = (int) MessageProtocol.MOVE_FORFEIT;
        
        connection.startTransaction();
        try{
            connection.executeWriteQuery("insert into moves (clientId, gameId, moveType, x, y, moveNum) values (" + playerID + ", " + gameID + ", " + moveType + ", -1, -1, " + moveNum + ")");
            connection.executeWriteQuery("update games set moveNum=" + moveNum + " where gameId=" + gameID);
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	    
	
	public void updateGameWithMove(Game game, ClientLogic client, BoardLocation loc, Vector<BoardLocation> capturedStones) throws SQLException {
        int gameID = game.getGameID();
        int playerID = client.getID();
        int moveNum = game.getBoard().getNumberOfMoves();
        int moveType = (int) MessageProtocol.MOVE_STONE;
        int x = loc.getX();
        int y = loc.getY();

        connection.startTransaction();
        try {
            int mID = connection.executeWriteQuery("insert into moves (clientId, gameId, moveType, x, y, moveNum) values (" + playerID + ", " + gameID + ", " + moveType + ", " + x + ", " + y + ", " + moveNum + ")");
            connection.executeWriteQuery("update games set moveNum=" + moveNum + " where gameId=" + gameID);
            for(BoardLocation location : capturedStones)
                connection.executeWriteQuery("INSERT INTO captured_stones (moveID, x ,y) VALUES (" + mID + ", " + location.getX() + ", " + location.getY() + ")");
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		int gameID = game.getGameID();
        int playerID = client.getID();
        int moveNum = game.getBoard().getNumberOfMoves();
        int moveType = (int) MessageProtocol.MOVE_PASS;

        connection.startTransaction();
        try{
            connection.executeWriteQuery("insert into moves (clientId, gameId, moveType, x, y, moveNum) values (" + playerID + ", " + gameID + ", " + moveType + ", -1, -1, " + moveNum + ")");
            connection.executeWriteQuery("update games set moveNum=" + moveNum + " where gameId=" + gameID);
            connection.finishTransaction();
        }
        catch (SQLException e){
            connection.abortTransaction();
            throw e;
        }
	}
	
	public void finishGame(int gameId, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
		int winnerID = winner.getID();

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

        ResultSet results = connection.executeReadQuery(getUnfinishedGamesQuery);
        ArrayList<Hashtable<String, Integer>> unfinishedGamesFromDB = new ArrayList<Hashtable<String, Integer>>();

        while (results.next()) {
            Hashtable<String, Integer> currentUnfinishedGameValues = new Hashtable<String, Integer>();
            currentUnfinishedGameValues.put("gameId", results.getInt("gameId"));
            currentUnfinishedGameValues.put("blackPlayer", results.getInt("blackPlayer"));
            currentUnfinishedGameValues.put("whitePlayer", results.getInt("whitePlayer"));
            currentUnfinishedGameValues.put("boardSize", results.getInt("boardSize"));

            unfinishedGamesFromDB.add(currentUnfinishedGameValues);
        }

        connection.closeReadQuery(results);

        for (Hashtable<String, Integer> info : unfinishedGamesFromDB) {
            int gameId = info.get("gameId");
            final int blackPlayerId = info.get("blackPlayer");
            final int whitePlayerId = info.get("whitePlayer");
            int boardSize = info.get("boardSize");

            ResultSet blackPlayerResult = connection.executeReadQuery("SELECT name, type FROM clients WHERE clientId=" + blackPlayerId);
            ClientInfo blackPlayer = MessageFactory.createClientInfo(blackPlayerResult.getString("name"), (byte) blackPlayerResult.getInt("type"));
            connection.closeReadQuery(blackPlayerResult);

            ResultSet whitePlayerResult = connection.executeReadQuery("SELECT name, type FROM clients WHERE clientId=" + whitePlayerId);
            ClientInfo whitePlayer = MessageFactory.createClientInfo(whitePlayerResult.getString("name"), (byte) whitePlayerResult.getInt("type"));
            connection.closeReadQuery(whitePlayerResult);
            class Move {
            	StoneColor color;
            	byte moveType;
            	int x;
            	int y;
            	public Move(int clientId, byte moveType, int x, int y) {
            		this.color = clientId == whitePlayerId ? StoneColor.WHITE : StoneColor.BLACK;
            		this.moveType = moveType;
            		this.x = x;
            		this.y = y;
            	}
            }
            
            //get game's moves
            ResultSet moves = connection.executeReadQuery("SELECT * FROM moves WHERE gameId=" + gameId + " ORDER BY moveNum ASC");
            
            ArrayList<Move> moveList = new ArrayList<Move>();
            
            while (moves.next()) {
                moveList.add(new Move(
                		moves.getInt("clientId"), 
                		(byte) moves.getInt("moveType"), 
                		moves.getInt("x"), 
                		moves.getInt("y")));
            }

            connection.closeReadQuery(moves);

            String gameName = blackPlayer.getName() + " vs " + whitePlayer.getName();
            GoBoard board = new GoBoard(boardSize);

            for (Move move : moveList) {
                if (move.moveType == MessageProtocol.MOVE_PASS) {
                	board.makePassMove(move.color);
                } else if (move.moveType == MessageProtocol.MOVE_STONE)
                {
                	try {
						board.makeMove(new BoardLocation(move.x, move.y), move.color);
					} catch (IllegalMoveException e) {
						//should not be an illegal move, rewrap and throw.
						throw new RuntimeException(e);
					}
                } else {
                	assert false : "Programmer error, should not be a forfeit move in an unfinished game";
                }
            }

            UnfinishedGame unfinishedGame = new UnfinishedGame(gameName, board, blackPlayer, whitePlayer, gameId);
            unfinishedGames.add(unfinishedGame);
        }

        return unfinishedGames;
	}
}