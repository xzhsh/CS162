package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ResponseMessages;

public class PlayerWorkerSlave extends WorkerSlave{
	
	private Game game;
	private int moveTimeout;
	private PlayerLogic master;
	public PlayerWorkerSlave(ClientConnection connection, PlayerLogic logic, int moveTimeout) {
		super(connection, logic.getServer());
		this.master = logic;
		game = null;
		this.moveTimeout = moveTimeout;
	}
	
	/**
	 * Should only be called from the handleTerminate function. This assumes that 
	 */
	@Override
	protected void closeAndCleanup() {
		super.closeAndCleanup();
		Game temp = game;
		if (temp != null)
		{
			getServer().removeGame(game);
			game = null;
			temp.doGameOverError(new GoBoard.IllegalMoveException(master.makeClientInfo().getName() + " disconnected.", MessageProtocol.PLAYER_FORFEIT));
		}
		master.disconnectState();
		System.out.println("PlayerWorker cleaned up, " + getServer().getNumberOfActiveGames() + " Games active");
	}
	
	protected void terminateGame() {
		System.out.println(master.makeClientInfo().getName() + " received terminateGame message!");
		Game temp = game;
		if (temp != null) {
			getServer().removeGame(game);
			game = null;
			temp.broadcastTerminate();
		}
		master.terminateGame();
	}

	/**
     * Tells this worker that the game has begun.
     * 
     * The worker should save the game if needed.
     * 
     * @param game
     */
    public void handleStartNewGame(final Game game)
    {
    	game.broadcastStartMessage();
    	getMessageQueue().add(new Runnable() {
			@Override
			public void run() {
		    	doGetMove(game);
			}
    	});
    }
    
    public void handleNextMove(final Game game)
    {
    	getMessageQueue().add(new Runnable() {
			@Override
			public void run() {
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
    private void doGetMove(Game game) 
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
			else if (moveMsg.getMoveType() == MessageProtocol.MOVE_FORFEIT)
			{
				game.doGameOverError(new GoBoard.IllegalMoveException(master.makeClientInfo().getName() + " timed out.", MessageProtocol.PLAYER_FORFEIT));
			}
			else if (moveMsg.getMoveType() == MessageProtocol.MOVE_STONE)
			{
				game.doMakeMove(moveMsg.getLocation().makeBoardLocation());
			}
		}
		else 
		{
			game.doGameOverError(new GoBoard.IllegalMoveException(master.makeClientInfo().getName() + " timed out.", MessageProtocol.PLAYER_FORFEIT));
		}
    }

	public void setGame(Game game) {
		this.game = game;
	}
}