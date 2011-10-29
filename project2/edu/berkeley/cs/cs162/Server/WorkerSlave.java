package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

class WorkerSlave extends Thread {
	private static final int WORKER_MESSAGE_QUEUE_SIZE = 10;
	private boolean done;
	private Socket forwardConnection;
	private InputStream input;
	private OutputStream output;
	private Lock inputLock;
	private Lock outputLock;
	private ThreadSafeQueue<Runnable> messageQueue;
	
	public WorkerSlave(Socket S2Csocket) {
		forwardConnection = S2Csocket;
		inputLock = new Lock();
		outputLock = new Lock();
		try {
			input = forwardConnection.getInputStream();
			output = forwardConnection.getOutputStream();
		} catch (IOException e)
		{
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
					try {
						forwardConnection.close();
						done = true;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
							message.writeTo(output);
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
			message.writeTo(output);
			outputLock.release();
			inputLock.acquire();
			Message returnMessage = MessageFactory.readMessageFromInput(input);
			inputLock.release();
			return returnMessage;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}