package edu.berkeley.cs.cs162.Server;

import java.io.IOException;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

/**
 * WorkerSlave. It's in charge of handling messages sent from server to client and shit. Yup.
 * 
 * @author xshi
 *
 */
class WorkerSlave extends Thread {
	
	protected Message sendSynchronousMessage (Message message) {
		Message returnMessage = null;
		try {
            connection.sendToClient(message);
            returnMessage = connection.readReplyFromClient(message);
            
            if (returnMessage.getMsgType() != MessageProtocol.OP_STATUS_OK) {
            	connection.invalidate(new IOException("Illegal return message"));
            }
        } catch (IOException e) {
            connection.invalidate(e);
        }
		return returnMessage;
	}
	
	protected Message sendSynchronousMessage (Message message, int timeout) {
		Message returnMessage = null;
		try {
            connection.sendToClient(message);
            returnMessage = connection.readReplyFromClient(message, timeout);
            
            if (returnMessage.getMsgType() != MessageProtocol.OP_STATUS_OK) {
            	connection.invalidate(new IOException("Illegal return message"));
            }
        } catch (IOException e) {
            connection.invalidate(e);
        }
		return returnMessage;
	}
    protected class MessageCourier implements Runnable {
		private final Message message;

		protected MessageCourier(Message message) {
			this.message = message;
		}
		public void run() {
	        sendSynchronousMessage(message);
		}
	}

	private static final int WORKER_MESSAGE_QUEUE_SIZE = 10;
    private boolean done;
    private Worker master;
    protected ClientConnection connection;
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
            getMessageQueue().get().run();
        }
    }

    public void handleTerminate() {
        getMessageQueue().add(
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
    	getMessageQueue().add(new MessageCourier(message));
    }
    
	protected void closeAndCleanup() {
		getMessageQueue().clear();
		connection.close();
		System.out.println("Worker cleaned up");
	}

	public ThreadSafeQueue<Runnable> getMessageQueue() {
		return messageQueue;
	}
}