package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public class PlayerWorkerSlave extends WorkerSlave{
	Game game;
	public PlayerWorkerSlave(ClientConnection connection, Worker master) {
		super(connection, master);
		game = null;
	}
	
	protected void closeAndCleanup() {
		if (game != null) {
			if (game.getBlackPlayer() == getMaster())
			{
				//the black player is always in charge of initializing and cleaning up
				getMaster().getServer().removeGame(game);
			}
		}
		super.closeAndCleanup();
	}

	/**
     * Tells this worker that the game has begun.
     * 
     * The worker should save the game if needed.
     * 
     * @param game
     */
    public void handleGameStart(Game game)
    {
    	final Message message = MessageFactory.createGameStartMessage(game);
    	game.sendMessageToAllObserversAndPlayers(message);
    }
    
    /**
     * Tells this worker that the game has finished.
     * 
     * The worker should clean up after itself when this message is received.
     * 
     * @param game
     */
    public void handleGameOver(Game game, double blackScore, double whiteScore, Worker winner)
    {
    	final Message message = MessageFactory.createGameOverMessage(game.makeGameInfo(), blackScore, whiteScore, winner.makeClientInfo());
    	handleSendMessage(message);
    }
    
    /**
     * Tells this worker that an error has occurred.
     * 
     * The worker should clean up after itself when this message is received.
     * 
     * @param game
     */
    public void handleGameOverError(Game game, double blackScore, double whiteScore, Worker winner, byte reason, Worker errorPlayer, String errorMessage)
    {
    	final Message message = MessageFactory.createGameOverErrorMessage(game.makeGameInfo(), blackScore, whiteScore, winner.makeClientInfo(), reason, errorPlayer.makeClientInfo(), errorMessage);
    	handleSendMessage(message);
    }
    
    public void handleMakeMove(Game game, Worker currentPlayer, byte moveType, BoardLocation loc, Collection<BoardLocation> capturedList)
    {
    	final Message message = MessageFactory.createMakeMoveMessage(game.makeGameInfo(), currentPlayer.makeClientInfo(), moveType, loc, capturedList);
    	handleSendMessage(message);
    }
    
    /**
     * Gets a move from the player
     * 
     * @param timeoutInMs how long the client has to make a move
     * @return The status_ok message from the client.
     * @throws IOException if the connection dies
     * @throws TimeoutException if the client takes too long
     */
    public Message doGetMove(long timeoutInMs) throws IOException, TimeoutException
    {
    	throw new AssertionError("Unimplemented method");
    }
}