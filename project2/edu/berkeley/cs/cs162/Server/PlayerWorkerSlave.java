package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ResponseMessages;

public class PlayerWorkerSlave extends WorkerSlave{
	
	private Game game;
	private int moveTimeout;
	public PlayerWorkerSlave(ClientConnection connection, Worker master, int moveTimeout) {
		super(connection, master);
		game = null;
		this.moveTimeout = moveTimeout;
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
	 * @throws TimeoutException 
	 * @throws IOException 
     */
    public void startNewGame(final Game game) throws IOException, TimeoutException
    {
    	getMessageQueue().add(new Runnable() {
			@Override
			public void run() {
		    	game.broadcastStartMessage();
		    	doGetMove(game);
			}
    	});
    }
    

    /**
     * Gets a move from the player
     * 
     * @param timeoutInMs how long the client has to make a move
     * @return The status_ok message from the client.
     * @throws IOException if the connection dies
     * @throws TimeoutException if the client takes too long
     */
    public void doGetMove(Game game) 
    {
    	Message getMoveMsg = MessageFactory.createGetMoveMessage();
    	
    	Message reply = sendSynchronousMessage(getMoveMsg, moveTimeout);
		if (reply != null && reply.isOK())
		{
			ResponseMessages.GetMoveStatusOkMessage moveMsg = (ResponseMessages.GetMoveStatusOkMessage)reply;
			if (moveMsg.getMoveType() == MessageProtocol.MOVE_PASS)
			{
				game.makePassMove();
			} 
			else 
			{
				game.makeMove(moveMsg.getLocation().makeBoardLocation());
			}
		}
		else 
		{
			game.broadcastGameover(MessageProtocol.PLAYER_TIMEOUT);
		}
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
    	game.sendMessageToAllObserversAndPlayers(message);
    	this.game = null;
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
    
}