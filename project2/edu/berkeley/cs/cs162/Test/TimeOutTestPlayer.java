package edu.berkeley.cs.cs162.Test;


import java.io.IOException;

import edu.berkeley.cs.cs162.Client.Player;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ServerMessages;

public class TimeOutTestPlayer extends Player {
	boolean success;
	long timeout;
	public TimeOutTestPlayer(String name, byte type, long timeout) {
		super(name, type);
		success = false;
		this.timeout = timeout;
	}
	
	@Override
    protected void handleGetMove() throws IOException {
    	try {
			Thread.sleep(timeout);
		} catch (InterruptedException e) {
			//should never get interrupted.
		}
    }
	
	@Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
		if (m.getReason() == MessageProtocol.PLAYER_FORFEIT) {success = true;}
		super.handleGameOver(m);
		getConnection().sendDisconnectToServer();
		getConnection().close();
    }
	
	public boolean getSuccess()
	{
		return success;
	}
	
	public static Thread runInstance(final TimeOutTestPlayer player, final int port) {
		Thread t = new Thread() {
			public void run() {
		        if (player.connectTo("localhost", port)) {
		        	System.out.println("Connected to server");
		        	Message reply;
					try {
						reply = player.getConnection().sendSyncToServer(MessageFactory.createWaitForGameMessage());
						System.out.println("Sent wait for game to server");
					} catch (IOException e) {
						reply = MessageFactory.createErrorRejectedMessage();
						System.out.println("Sending messages failed");
					}
	                if (reply.isOK()) {
	                    player.setSentWFGMessage(true);
	                    try {
	                    	System.out.println("Doing execution");
	                    	player.runExecutionLoop();
	                    }
	                    catch (IOException e){}
	                }
		        } else {
		        	System.out.println("Connection Failed");
		        }
			}
		};
		t.start();
		return t;
	}
}
