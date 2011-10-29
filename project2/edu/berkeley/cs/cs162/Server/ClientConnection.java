package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

public class ClientConnection {
	private Socket S2C;
	private Socket C2S;
	private int SYN_ID;
	public ClientConnection(Socket connection1, Socket connection2, int SYN_ID) {
		S2C = connection1;
		C2S = connection2;
	}
	
	public Socket getS2C(){
		return S2C;
	}
	
	public Socket getC2S(){
		return C2S;
	}
	
	
	/**
	 * Tries to receive a 3-way handshake.
	 * 
	 * @param rng The random number generator that will be used.
	 * @throws IOException 
	 */
	public boolean receive3WayHandshake(Random rng) throws IOException {
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
			S2Cout.write(SYN_ID+1);
			
			C2Sout.write(C2SackID);
			C2Sout.write(SYN_ID+1);
			
			
			int ackC2S = C2Sin.read();
			int ackS2C = S2Cin.read();
			if (ackC2S == C2SackID + 1 && ackS2C == S2CackID + 1)
			{
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

	public void close() throws IOException {
		S2C.close();
		C2S.close();
	}
}
