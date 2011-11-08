package edu.berkeley.cs.cs162.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
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
    private boolean valid;
    List<Exception> errors;

    public ClientConnection(Socket connection1, Socket connection2, int SYN_ID) {
        S2C = connection1;
        C2S = connection2;
        this.SYN_ID = SYN_ID;
        errors = new ArrayList<Exception>();
        valid = false;
    }

    /**
     * Tries to receive a 3-way handshake.
     *
     * @param rng The random number generator that will be used.
     * @throws IOException
     */
    public boolean receive3WayHandshake(Random rng) {
        int ackID1 = 0;
        int ackID2 = 0;

        //generate two unique acknowledgment identifiers
        while (ackID1 == ackID2) {
            ackID1 = rng.nextInt();
            ackID2 = rng.nextInt();
        }
        int C2SackID = Math.min(ackID1, ackID2);
        int S2CackID = Math.max(ackID1, ackID2);


        try {

            C2Sout = new DataOutputStream(C2S.getOutputStream());
            S2Cout = new DataOutputStream(S2C.getOutputStream());
            C2Sin = new DataInputStream(C2S.getInputStream());
            S2Cin = new DataInputStream(S2C.getInputStream());
            S2Cout.writeInt(S2CackID);
            S2Cout.writeInt(SYN_ID + 1);
            S2Cout.flush();
            C2Sout.writeInt(C2SackID);
            C2Sout.writeInt(SYN_ID + 1);
            C2Sout.flush();

            int ackC2S = C2Sin.readInt();
            int ackS2C = S2Cin.readInt();
            if (ackC2S == C2SackID + 1 && ackS2C == S2CackID + 1) {
                //correct. break and finish
                valid = true;
            } else {
                //wrong ack... something went wrong.
                System.out.printf("Wrong ack received from client. Expecting %d and %d, but received %d and %d\n", C2SackID + 1, S2CackID + 1, ackC2S, ackS2C);
                valid = false;
            }
        } catch (SocketTimeoutException e) {
            //socket timed out.
            System.out.printf("Connection from client timed out\n");
            valid = false;
        } catch (IOException e) {
            System.out.printf("Connection error:\n");
            e.printStackTrace();
            valid = false;
        }
        return valid;
    }

    public void sendToClient(Message message) throws IOException {
        if (!valid) {
            throw new IOException("Invalid connection used");
        }
        message.writeTo(S2Cout);
        S2Cout.flush();
    }

    public void close(){
        valid = false;
        try {
        	S2C.close();
        }
        catch (IOException e)
        {
        	//already closed, just continue
        }
        try {
        	C2S.close();
        }
        catch (IOException e)
        {
        	//already closed, just continue
        }
    }

    public Message readFromClient() throws IOException {
        if (!valid) {
            throw new IOException("Invalid connection used");
        }
        return MessageFactory.readClientMessage(C2Sin);
    }

    public void sendReplyToClient(Message message) throws IOException {
        if (!valid) {
            throw new IOException("Invalid connection used");
        }
        message.writeTo(C2Sout);
        C2Sout.flush();
    }

    public Message readReplyFromClient(Message message) throws IOException {
        if (!valid) {
            throw new IOException("Invalid connection used");
        }
        //return ResponseMessages.readReplyFromInput(message, S2Cin);
        return MessageFactory.readResponseMessage(S2Cin, message);
    }

    /**
     * Invalidates the connection.
     *
     * @param e the exception that caused this.
     */
    public void invalidate(IOException e) {
        valid = false;
        errors.add(e);
    }

    public boolean isValid() {
        return valid;
    }

	public Message readReplyFromClient(Message message, int timeout) throws IOException {
		S2C.setSoTimeout(timeout);
		Message reply = readReplyFromClient(message);
		S2C.setSoTimeout(GameServer.GLOBAL_TIMEOUT_IN_MS);
		return reply;
	}

    // TESTING PURPOSES ONLY
    public void setValid() throws IOException {
        valid = true;
        C2Sout = new DataOutputStream(C2S.getOutputStream());
        S2Cout = new DataOutputStream(S2C.getOutputStream());
        C2Sin = new DataInputStream(C2S.getInputStream());
        S2Cin = new DataInputStream(S2C.getInputStream());
    }
}