package edu.berkeley.cs.cs162.Server;

import java.util.HashSet;
import java.util.Set;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public class Game {
    private GoBoard board;
    private String name;
    private Worker blackPlayer;
    private Worker whitePlayer;
    private Set<Worker> observerList;
    private ReaderWriterLock observerLock;
    
    public Game(String name, Worker blackPlayer, Worker whitePlayer, int size) {
        board = new GoBoard(size);
        observerList = new HashSet<Worker>();
        observerLock = new ReaderWriterLock();
    }

    public GameInfo makeGameInfo() {
        return new GameInfo(name);
    }

    /**
     * Adds an observer to this game. can be done asynchronously by multiple threads.
     *
     * @param worker
     * @return 
     */
    public boolean addObserver(Worker worker) {
    	observerLock.writeLock();
    	boolean added = !observerList.contains(worker);
    	if (added)
    	{
    		observerList.add(worker);
    	}
    	observerLock.writeUnlock();
		return added;
    }

    public boolean removeObserver(Worker worker) {
    	observerLock.writeLock();
    	boolean removed = observerList.contains(worker);
    	if (removed)
    	{
    		observerList.remove(worker);
    	}
    	observerLock.writeUnlock();
		return removed;
    }

    public BoardInfo makeBoardInfo() {
        // TODO Auto-generated method stub
        return MessageFactory.createBoardInfo(board.getCurrentBoard());
    }

    public Worker getBlackPlayer() {
        return blackPlayer;
    }

    public Worker getWhitePlayer() {
        return whitePlayer;
    }

	public byte getGameOverReason() {
		// TODO Auto-generated method stub
		return 0;
	}
}
