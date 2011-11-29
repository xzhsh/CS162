package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

import edu.berkeley.cs.cs162.Client.Player;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ServerMessages;

public class TestPlayer extends Player {
	List<Message> moves;
	Iterator<Message> it;
	AtomicInteger goodCount;
	public TestPlayer(String name, byte type, List<Message> moves, AtomicInteger shared)
	{
		super(name, type);
		this.moves = new ArrayList<Message>(moves);
		it = this.moves.iterator(); 
		goodCount = shared;
	}
	
	/**
	 * Runs an instance of this test player in a new thread.
	 * 
	 * They will wait until they can acquire the read lock before starting to run 
	 * the execution loop, so that observers can synchronize onto that.
	 * 
	 * @param name
	 * @param type
	 * @param moves
	 * @param port
	 * @param lock
	 * @throws IOException
	 */
	public static Thread runInstance(final String name, final byte type, final List<Message> moves, final int port, final ReaderWriterLock lock, final AtomicInteger shared) {
		Thread t = new Thread() {
			public void run() {
				TestPlayer player = new TestPlayer(name, type, moves, shared);
		        if (player.connectTo("localhost", port)) {
					
		        	Message reply;
					try {
						reply = player.getConnection().sendSyncToServer(MessageFactory.createWaitForGameMessage());
					} catch (IOException e) {
						reply = MessageFactory.createErrorRejectedMessage();
					}
	                if (reply.isOK()) {
	                    player.setSentWFGMessage(true);
	                    //lock.readLock();
	                    try {
	                    	player.runExecutionLoop();
	                    }
	                    catch (IOException e){}
	                    //lock.readUnlock();
	                }
		        }
			}
		};
		t.start();
		return t;
	}

    protected void handleGetMove() throws IOException {
    	try {
    		Message m = it.next();
    		getConnection().sendReplyToServer(m);
    	}
    	catch (NoSuchElementException e)
    	{
    		getConnection().sendDisconnectToServer();
    		getConnection().close();
    	}
    }
    
    @Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
		if (m.getReason() == MessageProtocol.GAME_OK) {goodCount.addAndGet(1);}
		super.handleGameOver(m);
		getConnection().sendDisconnectToServer();
		getConnection().close();
    }
}