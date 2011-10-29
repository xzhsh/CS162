package edu.berkeley.cs.cs162;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
@SuppressWarnings("unused")
public abstract class Worker implements Runnable {

	private Thread slaveThread;
	private WorkerSlave slave;
	private Socket returnConnection;
	private boolean done;
	private InputStream input;
	private OutputStream output;
	private GameServer server;
	
	public Worker(Socket C2S, GameServer server) throws IOException {
		this.returnConnection = C2S;
		this.server = server;
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
	
	/**
	 * Tries to receive a 3-way handshake.
	 * 
	 * @param s1 first socket that the client connected with
	 * @param s2 second socket that the client connected with
	 * @param SYNid the syn id that the client identified itself with
	 * @throws IOException 
	 */
	public boolean receive3WayHandshake(Socket S2C, Socket C2S, int SYNid) throws IOException {
		Random rng = server.getRNG();
		int ackID1 = 0;
		int ackID2 = 0;
		
		//generate two unique acknowledgment identifiers
		while (ackID1 == ackID2)
		{
			ackID1 = rng.nextInt();
			ackID2 = rng.nextInt();
		}
		int C2SackID = Math.min(ackID1, ackID2);
		int S2CackID = Math.max(ackID1, ackID2);
		
		OutputStream C2Sout = C2S.getOutputStream();
		OutputStream S2Cout = S2C.getOutputStream();
		InputStream C2Sin = C2S.getInputStream();
		InputStream S2Cin = S2C.getInputStream();
		
		try 
		{
			S2Cout.write(S2CackID);
			S2Cout.write(SYNid+1);
			
			C2Sout.write(C2SackID);
			C2Sout.write(SYNid+1);
			
			
			int ackC2S = C2Sin.read();
			int ackS2C = S2Cin.read();
			if (ackC2S == C2SackID + 1 && ackS2C == S2CackID + 1)
			{
				returnConnection = C2S;
				spawnSlaveThread(S2C);
				//correct. break and finish
				return true;
			}
			else
			{
				//wrong ack... something went wrong.
				return false;
			}
		} 
		catch (SocketTimeoutException e)
		{
			//socket timed out. try again, perhaps the data was lost?
			return false;
		}
	}

	private void spawnSlaveThread(Socket S2C) {
		slave = new WorkerSlave(S2C);
		slaveThread = new Thread(slave);
		slaveThread.start();
	}
}