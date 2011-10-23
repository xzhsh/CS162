package edu.berkeley.cs.cs162;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
@SuppressWarnings("unused")
public abstract class Worker implements Runnable {
	private WorkerSlave slaveThread;
	private Socket returnConnection;
	private boolean done;
	private InputStream input;
	private OutputStream output;
	
	public Worker(Socket C2S) throws IOException {
		this.returnConnection = C2S;
		done = false;
	}
	
	/**
	 * Run loop for the worker
	 */
	public void run()
	{
		try {
			input = returnConnection.getInputStream();
			output = returnConnection.getOutputStream();
			
			while (!done)
			{
				
			}
			
			assert returnConnection != null;
			returnConnection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}