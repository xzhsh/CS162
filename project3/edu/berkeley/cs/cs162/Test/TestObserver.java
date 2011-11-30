package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import edu.berkeley.cs.cs162.Client.Observer;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ResponseMessages;
import edu.berkeley.cs.cs162.Writable.ServerMessages;
import edu.berkeley.cs.cs162.Writable.ServerMessages.GameOverMessage;
import edu.berkeley.cs.cs162.Writable.Writable;
import edu.berkeley.cs.cs162.Writable.WritableList;

public class TestObserver extends Observer{
	private Set<String> gameOverMessages;
	private int goodGameOvers;
	private int badMessages;
	public TestObserver(String name, Set<String> gameOverMessages)
	{
		super(name, name + "jaysucks");
		this.gameOverMessages = new HashSet<String>(gameOverMessages);
		goodGameOvers = 0;
		setBadMessages(0);
	}
	private boolean joinGames() throws IOException {
		Message listResponse = getConnection().sendSyncToServer(MessageFactory.createListGamesMessage());
        if (listResponse.isOK()) {
            WritableList gameList = ((ResponseMessages.ListGamesStatusOkMessage) listResponse).getGameList();
            
            for (Writable game : gameList) {
                GameInfo g = (GameInfo) game;
                Message joinResponse = getConnection().sendSyncToServer(MessageFactory.createJoinMessage(g));
                if (joinResponse.isOK()) {
                    joinedGames++;
                }
            }
        }

        return joinedGames > 0;
    }
	   
	public static Thread runInstance(final TestObserver obs, final ReaderWriterLock lock, final int port) {
		lock.writeLock();
		Thread t = new Thread() {
			public void run() {
		        if (obs.connectTo("localhost", port)) {
		            try {
		            	obs.joinGames();
		            	lock.writeUnlock();
						obs.runExecutionLoop();
					} catch (IOException e) {
						//the obs may be d/ced or w/e, just terminate.
					}
		        }
			}
		};
		t.start();
		return t;
	}
	
    @Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
    	String msg = formatGameOverMessage(m);
    	if (m.getReason() == MessageProtocol.GAME_OK) {goodGameOvers++;}
        if (gameOverMessages.contains(msg))
        {
        	gameOverMessages.remove(msg);
        	getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
        }
        else {
        	setBadMessages(getBadMessages() + 1);
        }
    }
    
    public void assertCorrect()
    {
    	if (!gameOverMessages.isEmpty()) { throw new AssertionError();}
    }
    
    public String formatGameOverMessage(GameOverMessage m)
    {
    	String gameName = m.getGameInfo().getName();
        double blackScore = m.getBlackScore();
        double whiteScore = m.getWhiteScore();
        String winner = m.getWinner().getName();
        byte reason = m.getReason();
        String msg;
    	if (reason != MessageProtocol.GAME_OK) {
            String playerName = m.getErrorPlayer().getName();
            String errorMsg = m.getErrorMessage();
            msg = "Game " + gameName + " ended with an error by " + playerName + ": " + errorMsg + ". Black score " + blackScore + ", White score " + whiteScore + ". WINNER: " + winner + "!";
        } else {
            msg = "Game " + gameName + " ended with Black score " + blackScore + ", White score " + whiteScore + ". WINNER: " + winner + "!";
        }
    	return msg;
    }
    
    public int getNumGoodGameOvers() {
		return goodGameOvers;
    }
	public int getBadMessages() {
		return badMessages;
	}
	public void setBadMessages(int badMessages) {
		this.badMessages = badMessages;
	}
}
