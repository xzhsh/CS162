package edu.berkeley.cs.cs162.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public class ClientConnection {
	private Socket S2C;
	private Socket C2S;
	private int SYN_ID;
	private DataOutputStream C2Sout;
	private DataInputStream S2Cin;
	private DataInputStream C2Sin;
	private DataOutputStream S2Cout;
	public ClientConnection(Socket connection1, Socket connection2, int SYN_ID) {
		S2C = connection1;
		C2S = connection2;
		this.SYN_ID = SYN_ID;
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
		
		C2Sout = new DataOutputStream(C2S.getOutputStream());
		S2Cout = new DataOutputStream(S2C.getOutputStream());
		C2Sin = new DataInputStream(C2S.getInputStream());
		S2Cin = new DataInputStream(S2C.getInputStream());
		
		try 
		{
			S2Cout.writeInt(S2CackID);
			S2Cout.writeInt(SYN_ID+1);
			
			C2Sout.writeInt(C2SackID);
			C2Sout.writeInt(SYN_ID+1);
			
			
			int ackC2S = C2Sin.readInt();
			int ackS2C = S2Cin.readInt();
			if (ackC2S == C2SackID + 1 && ackS2C == S2CackID + 1)
			{
				//correct. break and finish
				return true;
			}
			else
			{
				//wrong ack... something went wrong.
				System.out.printf("Wrong ack received from client. Expecting %d and %d, but received %d and %d\n", C2SackID + 1, S2CackID + 1, ackC2S, ackS2C);
				return false;
			}
		} 
		catch (SocketTimeoutException e)
		{
			//socket timed out.
			System.out.printf("Connection from client timed out\n");
			
			return false;
		}
	}
	
	public void readFromClient(Message messageContainer) throws IOException
	{
		messageContainer.readFrom(C2Sin);
	}
	
	public void sendToClient(Message message) throws IOException
	{
		message.writeTo(S2Cout);
	}

	public void close() throws IOException {
		S2C.close();
		C2S.close();
	}

	public Message readFromClient() throws IOException {
		return MessageFactory.readMessageFromInput(C2Sin);
	}
}
