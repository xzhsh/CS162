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
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.GoBoard.IllegalMoveException;
import edu.berkeley.cs.cs162.Server.ServerStateManager;
import edu.berkeley.cs.cs162.Server.StoneColor;
import edu.berkeley.cs.cs162.Server.UnfinishedGame;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class TestServerStateManager extends ServerStateManager {

	public ByteArrayOutputStream baos;
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
		ps.println("Updated game " + game.getName() + " with move by " +client.makeClientInfo() + " at "  + loc);
	}
	
	public void updateGameWithPass(Game game, ClientLogic client) throws SQLException {
		ps.println("Updated game " + game.getName() + " with pass by " + client.makeClientInfo());
	}
	
	public void finishGame(int gameId, ClientLogic winner, double blackScore, double whiteScore, int reason) throws SQLException {
		ps.println("Finished game with id " + gameId + " with " + winner.makeClientInfo() + " as winner. Final score "  + blackScore + " - " + whiteScore + " with reason byte: " + reason);
	}
	
	/**
	 * Constructs and loads a list of unfinished games by reading from the database.
	 * 
	 * @return a list of unfinished games.
	 * @throws SQLException
	 */
	public List<UnfinishedGame> loadUnfinishedGames() throws SQLException{
		ArrayList<UnfinishedGame> l = new ArrayList<UnfinishedGame>();
		GoBoard board = new GoBoard(10);
		try {
			board.makeMove(new BoardLocation(1, 0), StoneColor.BLACK);
			board.makeMove(new BoardLocation(2, 2), StoneColor.WHITE);
		} catch (IllegalMoveException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		l.add(new UnfinishedGame("TestGameReconnect", board, MessageFactory.createClientInfo("TestPlayer0", MessageProtocol.TYPE_MACHINE), MessageFactory.createClientInfo("TestPlayer0", MessageProtocol.TYPE_MACHINE), 1));
		idCount = 2;
		return l;
	}
}
