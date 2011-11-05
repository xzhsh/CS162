package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public class Game {
	private GoBoard board;
	private String name;
	private Worker blackPlayer;
	private Worker whitePlayer;
	public Game(String name, Worker blackPlayer, Worker whitePlayer, int size) {
		board = new GoBoard(size);
	}
	
	public GameInfo makeGameInfo() {
		return new GameInfo(name);
	}

	/**
	 * Adds an observer to this game. can be done asynchronously by multiple threads.
	 * @param worker
	 */
	public void addObserver(Worker worker) {
		// TODO Auto-generated method stub
		
	}

	public void removeObserver(Worker worker) {
		// TODO Auto-generated method stub
		
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
}
