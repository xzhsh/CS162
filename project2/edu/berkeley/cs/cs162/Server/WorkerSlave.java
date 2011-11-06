package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.util.Collection;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

class WorkerSlave extends Thread {
    private static final int WORKER_MESSAGE_QUEUE_SIZE = 10;
    private boolean done;
    private ClientConnection connection;
    private Lock inputLock;
    private Lock outputLock;
    private ThreadSafeQueue<Runnable> messageQueue;

    public WorkerSlave(ClientConnection connection) {
        this.connection = connection;
        inputLock = new Lock();
        outputLock = new Lock();
        messageQueue = new ThreadSafeQueue<Runnable>(WORKER_MESSAGE_QUEUE_SIZE);
        done = false;
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
                    }
                }
        );
    }

    /**
     * Immediately sends an synchronous message to the client (in the current thread of execution.
     * <p/>
     * In addition to that, this will block until the return message is received, or a timeout has occurred.
     *
     * @param message
     */
    public Message handleSendMessageSync(final Message message) {
        try {
            outputLock.acquire();
            connection.sendToClient(message);
            outputLock.release();
            inputLock.acquire();
            Message returnMessage = connection.readReplyFromClient(message);
            inputLock.release();
            return returnMessage;
        } catch (IOException e) {
            connection.invalidate(e);
        }
        return null;
    }

    
    /**
     * Tells the worker to send an asynchronous message to the client.
     * @param message
     */
    private void handleSendMessage(final Message message) {
    	messageQueue.add(new Runnable() {
    		public void run() {
			        try {
			            outputLock.acquire();
			            connection.sendToClient(message);
			            outputLock.release();
			            inputLock.acquire();
			            Message returnMessage = connection.readReplyFromClient(message);
			            inputLock.release();
			            
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
    }
    
	public void closeAndCleanup() {
		messageQueue.clear();
	}
}