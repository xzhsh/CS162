package edu.berkeley.cs.cs162;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

class WorkerSlave implements Runnable {
	private static final int WORKER_MESSAGE_QUEUE_SIZE = 10;
	private boolean done;
	private Socket forwardConnection;
	private InputStream input;
	private OutputStream output;
	private ThreadSafeQueue<Runnable> messageQueue;
	
	public WorkerSlave() {
		messageQueue = new ThreadSafeQueue<Runnable>(WORKER_MESSAGE_QUEUE_SIZE);
		done = false;
	}
	
	public void run() {
		while(!done) {
			messageQueue.get().run();
		}
	}
	
	public void handleAcceptS2CConnection(final Socket S2Csocket) {
		messageQueue.add(
			new Runnable(){
				public void run() {
					forwardConnection = S2Csocket;
					try {
						input = forwardConnection.getInputStream();
						output = forwardConnection.getOutputStream();
					} catch (IOException e)
					{
						//TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		);
	}
	
	public void handleCloseS2CConnection() {
		messageQueue.add(
			new Runnable(){
				public void run() {
					try {
						forwardConnection.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		);
	}
	
	public void handleSendMessageAsync(final Message message)
	{
		messageQueue.add(
				new Runnable(){
					public void run() {
						try {
							message.writeTo(output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			);
	}
	
	public void handleSendMessageSync(final Message message)
	{
		messageQueue.add(
				new Runnable(){
					public void run() {
						try {
							message.writeTo(output);
							MessageProtocol.readOpCodeFrom(input);
							//TODO READ RETURN MESSAGE
							//Message returnMessage = null;
							//returnMessage.readFrom(S2Cin);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			);
	}
}