package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

class WorkerSlave extends Thread {
    private static final int WORKER_MESSAGE_QUEUE_SIZE = 10;
    private boolean done;
    private Worker master;
    private ClientConnection connection;
    private ThreadSafeQueue<Runnable> messageQueue;
    
    public WorkerSlave(ClientConnection connection, Worker master) {
        this.connection = connection;
        this.master = master;
        messageQueue = new ThreadSafeQueue<Runnable>(WORKER_MESSAGE_QUEUE_SIZE);
        done = false;
    }
    
    public Worker getMaster()
    {
    	return master;
    }

    public void run() {
        while (!done) {
            messageQueue.get().run();
        }
    }

    public void handleTerminate() {
        messageQueue.add(
                new Runnable() {
                    public void run() {
                        done = true;
                        closeAndCleanup();
                    }
                }
        );
    }
    
    /**
     * Tells the worker to send an asynchronous message to the client.
     * @param message
     */
    public void handleSendMessage(final Message message) {
    	messageQueue.add(new Runnable() {
    		public void run() {
			        try {
			            connection.sendToClient(message);
			            Message returnMessage = connection.readReplyFromClient(message);
			            
			            if (returnMessage.getMsgType() != MessageProtocol.OP_STATUS_OK) {
			            	connection.invalidate(new IOException("Illegal return message"));
			            }
			        } catch (IOException e) {
			            connection.invalidate(e);
			        }
		    	}
    	});
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
    	handleSendMessage(message);
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
    
	protected void closeAndCleanup() {
		messageQueue.clear();
	}
}