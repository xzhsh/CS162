package edu.berkeley.cs.cs162.Server;

import java.io.IOException;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;

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
		while(!done) {
			messageQueue.get().run();
		}
	}
	
	public void handleTerminate() {
		messageQueue.add(
			new Runnable(){
				public void run() {
					done = true;
				}
			}
		);
	}
	
	/**
	 * Queues up an asynchronous message in this WorkerSlave's message queue.
	 * This will be sent at this thread's leisure.
	 * 
	 * @param message
	 */
	public void handleSendMessageAsync(final Message message)
	{
		messageQueue.add(
				new Runnable(){
					public void run() {
						try {
							outputLock.acquire();
							connection.sendToClient(message);
							outputLock.release();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			);
	}
	
	/**
	 * Immediately sends an synchronous message to the client (in the current thread of execution.
	 * 
	 * In addition to that, this will block until the return message is received, or a timeout has occurred.
	 * 
	 * @param message
	 */
	public Message handleSendMessageSync(final Message message)
	{
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
}