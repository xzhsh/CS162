package edu.berkeley.cs.cs162.Server;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import edu.berkeley.cs.cs162.Server.GoBoard.IllegalMoveException;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class Game {
	enum GameState {
		BLACK_MOVE,
		WHITE_MOVE,
		GAME_OVER
	}
	private GameState state;
    private GoBoard board;
    private String name;
    private Worker blackPlayer;
    private Worker whitePlayer;
    private Set<Worker> observerList;
    private ReaderWriterLock observerLock;
	private boolean lastPassed;
	
    public Game(String name, Worker blackPlayer, Worker whitePlayer, int size) {
        board = new GoBoard(size);
        observerList = new HashSet<Worker>();
        observerLock = new ReaderWriterLock();
        state = GameState.BLACK_MOVE;
        lastPassed = false;
    }

    public GameInfo makeGameInfo() {
        return MessageFactory.createGameInfo(name);
    }

    /**
     * Adds an observer to this game. can be done asynchronously by multiple threads.
     * 
     * This will simultaneously also add this game to the worker's observer list. 
     * This is done atomically because otherwise, we could have a race condition and 
     * end up with an orphaned observer.
     *
     * @param worker
     * @return 
     */
    public boolean addObserver(Worker worker) {
    	if (state == GameState.GAME_OVER) {return false;}
    	observerLock.writeLock();
    	boolean added = !observerList.contains(worker);
    	if (added)
    	{
    		observerList.add(worker);
    		((ObserverLogic)worker.getLogic()).addGame(this);
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
    		((ObserverLogic)worker.getLogic()).removeGame(this);
    	}
    	observerLock.writeUnlock();
		return removed;
    }

    public BoardInfo makeBoardInfo() {
        return MessageFactory.createBoardInfo(board.getCurrentBoard());
    }

    public Worker getBlackPlayer() {
        return blackPlayer;
    }

    public Worker getWhitePlayer() {
        return whitePlayer;
    }

	public String getName() {
		return name;
	}

	public void broadcastStartMessage() {
		observerLock.readLock();
		for (Worker o : observerList)
		{
			o.getSlave().handleGameStart(this);
		}
		observerLock.readUnlock();
		blackPlayer.getSlave().handleGameStart(this);
		whitePlayer.getSlave().handleGameStart(this);
	}

	public void makePassMove() {
		final Message message = MessageFactory.createMakeMoveMessage(makeGameInfo(), getCurrentPlayer().makeClientInfo(), MessageProtocol.MOVE_PASS, new BoardLocation(0,0), Collections.<BoardLocation>emptyList());
		broadcastMessage(message);
		if(lastPassed)
		{
			doGameOver();
		}
		else {
			lastPassed = true;
			advanceTurns();
		}
	}
	
	public void broadcastMessage(Message message)
	{
		observerLock.readLock();
		for (Worker o : observerList)
		{
			o.getSlave().handleSendMessage(message);
		}
		observerLock.readUnlock();
		blackPlayer.getSlave().handleSendMessage(message);
		whitePlayer.getSlave().handleSendMessage(message);
	}
	
	public void doMakeMove(BoardLocation loc) {
		try {
			Vector<BoardLocation> captured = board.makeMove(loc, getActiveColor());
			Message message = MessageFactory.createMakeMoveMessage(makeGameInfo(), 
					getCurrentPlayer().makeClientInfo(), MessageProtocol.MOVE_STONE, loc, captured);
			broadcastMessage(message);
			lastPassed = false;
			advanceTurns();
		} catch (IllegalMoveException e) {
			//illegal move, we need to send game over error.
			doGameOverError(e);
		}
	}
	
	private void advanceTurns() {
		assert state != GameState.GAME_OVER: "Something tried to advance turns when game is inactive";
		if (state == GameState.BLACK_MOVE)
		{
			state = GameState.WHITE_MOVE;
		} else if (state == GameState.WHITE_MOVE)
		{
			state = GameState.BLACK_MOVE;
		}
		getCurrentPlayerSlave().handleNextMove(this);
	}
	
	private void doGameOver() {
		state = GameState.GAME_OVER;
		double blackScore = board.getScore(StoneColor.BLACK);
		double whiteScore = board.getScore(StoneColor.WHITE);
		Worker winner = blackScore > whiteScore ? blackPlayer : whitePlayer;
		final Message message = MessageFactory.createGameOverMessage(makeGameInfo(), blackScore, whiteScore, winner.makeClientInfo());
    	broadcastMessage(message);
		broadcastTerminate();
	}

	void doGameOverError(IllegalMoveException e) {
		state = GameState.GAME_OVER;
		double blackScore = state == GameState.BLACK_MOVE ? 0 : 1;
		double whiteScore = 1 - blackScore;
		Message err = MessageFactory.createGameOverErrorMessage(makeGameInfo(), blackScore, whiteScore, 
				getInactivePlayer().makeClientInfo(), e.getReasonByte(), getCurrentPlayer().makeClientInfo(), e.getMessage());
		broadcastMessage(err);
		broadcastTerminate();
	}

	void broadcastTerminate() {
		//terminates the game for the players.
		//nothing needs to be done for observers because they hold no state.
		
		((PlayerWorkerSlave)blackPlayer.getSlave()).terminateGame(this);
		((PlayerWorkerSlave)whitePlayer.getSlave()).terminateGame(this);
		observerLock.writeLock();
		Iterator<Worker> i = observerList.iterator();
		while (i.hasNext())
		{
			Worker obs = i.next();
			((ObserverLogic)obs.getLogic()).removeGame(this);
			i.remove();
		}
		observerLock.writeUnlock();
	}

	private PlayerWorkerSlave getCurrentPlayerSlave() {
		return (PlayerWorkerSlave) getCurrentPlayer().getSlave();
	}
	
	private Worker getCurrentPlayer() {
		if (state == GameState.BLACK_MOVE) { return blackPlayer;}
		if (state == GameState.WHITE_MOVE) { return whitePlayer;}
		//if game over, return null.
		return null;
	}

	private Worker getInactivePlayer() {
		if (state == GameState.BLACK_MOVE) { return whitePlayer;}
		if (state == GameState.WHITE_MOVE) { return blackPlayer;}
		//if game over, return null.
		return null;
	}

	private StoneColor getActiveColor() {
		assert state != GameState.GAME_OVER: "Something tried to get active stone color when game is inactive";
		if (state == GameState.BLACK_MOVE) { return StoneColor.BLACK;}
		if (state == GameState.WHITE_MOVE) { return StoneColor.WHITE;}
		return null;
	}
}
