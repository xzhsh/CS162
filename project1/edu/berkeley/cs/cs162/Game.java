package edu.berkeley.cs.cs162;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.GoBoard.IllegalMoveException;

/**
   A Game represents a single game of Go. There may be multiple games running at
   once, each with two players and any number of observers. The Game must keep
   track of the board state and check whether or not moves made by players are
   valid. If a player's move is not valid, the game should end immediately and
   the player who made the invalid move loses.

   The Game is responsible for informing all players and observers of all game
   events and deciding when a game is over. All players and observers should be
   kept track of by name. Games may not have direct access to Observer or Player
   objects.
 */
public class Game implements Runnable {

	GameServer server;
	String gameName;
	GoBoard goBoard;
	
	String blackPlayer;
	String whitePlayer;
	int moveTimeoutInMs;
	boolean previousPassed;

    boolean gameOver;
	
	//Lock that will be held during the execution of this game.
	Lock gameProgressLock;
	
	ArrayList<String> observers;
	ThreadSafeQueue<Runnable> mQueue;
	
	enum GameState {
		BLACKMOVE,
		WHITEMOVE,
	}
	
	GameState state;
	
	/**
	 * Initializes this game with the params passed.
	 * 
	 * 
	 * @param server
	 * @param gameName
	 * @param blackPlayer
	 * @param whitePlayer
	 * @param initialBoard
	 * @param moveTimeoutInMs
	 */
    public Game(GameServer server, String gameName, String blackPlayer, String whitePlayer, Board initialBoard, int moveTimeoutInMs) {
    	this.server = server;
    	this.gameName = gameName;
    	this.blackPlayer = blackPlayer;
    	this.whitePlayer = whitePlayer;
    	this.goBoard = new GoBoard(initialBoard);
    	this.moveTimeoutInMs = moveTimeoutInMs;
    	
    	gameProgressLock = new Lock();
    	
    	state = GameState.BLACKMOVE;
    	previousPassed = false;
    	
    	observers = new ArrayList<String>();
    	
    	mQueue = new ThreadSafeQueue<Runnable>(2);
    	observers.add(blackPlayer);
    	observers.add(whitePlayer);

        gameOver = false;
    }

    /**
       Add an observer by the given name to watch this game.
     */
    public void addObserver(String observerName) {
		observers.add(observerName);
    }

    /**
       Remove an observer by the given name from watching this game.
     */
    public void removeObserver(String observerName) {
		observers.remove(observerName);
    }

    /**
       Record a move made made by the given player.
     */
    public void makeMove(String playerName, Location loc) {
    	final String name = playerName;
    	final Location location = loc;

		sendMessage(new Runnable() {
			public void run() {
				if (doMoveForPlayer(name, location)) {
					switchTurns();
				}

                else {
					gameOver = true;
				}
			}
		});
    }

    /**
     * @param playerName
     * @return {@link StoneColor} for the player name or none if not a player
     */
    private StoneColor getColorForPlayerName(String playerName) {
		if (playerName.equals(blackPlayer)) {
			return StoneColor.BLACK;
        }

    	else if (playerName.equals(whitePlayer)) {
			return StoneColor.WHITE;
        }

    	return StoneColor.NONE;
	}

	/**
       Record that the given player has chosen to pass its turn.
     */
    public void makePassMove(String playerName) {
    	final String name = playerName;

		sendMessage(new Runnable() {
			public void run() {
				if (doPassForPlayer(name)) {
					switchTurns();
				}

                else {
					gameOver = true;
				}
			}
		});
    }

    /**
       @return The name of this game.
     */
    public String getName() {
    	return gameName;
    }

    /**
       @return The current board state.
     */
    public Board getBoard() {
    	return goBoard.board;
    }

    /**
       Wait until the game has finished.
     */
    public void waitForGameOver() {
    	//block until the gameprogresslock gets released.
	    gameProgressLock.acquire();
	    gameProgressLock.release();
    }
    
    private void switchTurns() {
		if (state == GameState.BLACKMOVE) {
			state = GameState.WHITEMOVE;
		}

		else if (state == GameState.WHITEMOVE) {
			state = GameState.BLACKMOVE;
		}

		else {
			assert false : "Programmer Error: Unsupportred message in Game.switchTurns";
		}
	}
    
	/**
     * Message read/execute loop.
     */
    public void run() {
    	gameProgressLock.acquire();
    	for (String targetObserverName : observers) {
			server.sendGameStart(targetObserverName, this, blackPlayer, whitePlayer, goBoard.board, moveTimeoutInMs);
		}

        // Loop that takes moves from the player
        while(!gameOver){
            // Notify observers a move needs to be made
            for (String targetObserverName : observers) {
			    server.sendMakeMove(targetObserverName, this, getActivePlayerName());
		    }

            // Wait for the move, with a timeout
            try {
			    Runnable message = mQueue.getWithTimeout(moveTimeoutInMs);
			    message.run();
		    }
            catch (TimeoutException e) {
                sendErrorAndGameover(true, "failed to make a move in time.");
                gameOver = true;
		    }
        }
        server.sendGameTearDown(this);
        gameProgressLock.release();
    }
    
     /**
     * Parses a message. If a message is a valid move or a pass, this should record it.
     * 
     * If it is an invalid message, log an error and start game teardown.
     * @return true if the game is ongoing, false if it should stop and display gameover.
     */
	private boolean doMoveForPlayer(String activePlayerName, Location moveLoc) {
		StoneColor activeColor = state.equals(GameState.BLACKMOVE) ? StoneColor.BLACK : StoneColor.WHITE;
		StoneColor waitingColor = state.equals(GameState.WHITEMOVE) ? StoneColor.WHITE : StoneColor.BLACK;
		
		if (!getColorForPlayerName(activePlayerName).equals(activeColor)) {
			//Wrong player name. Not current player.
			String waitingPlayerName = getWaitingPlayerName();
			if (getWaitingPlayerName().equals(waitingPlayerName)) {
				//Late message from previous player. Log an error and send game over.
				sendErrorAndGameover(false, "Moved out of turn");
				assert false : "Programmer error: A non-participating Player has sent a message to the Game";
			}

			return false;
		}
		
		Vector<Location> captured = null;

		try {
			captured = goBoard.makeMove(moveLoc, activeColor);
		}

        catch (IllegalMoveException e) {
			//If move is illegal, send a message
        	sendErrorAndGameover(true, e.getMessage());
			return false;
		}
		
		for (String obsName : observers) {
			//send the move to players.
			server.sendStonePlaced(obsName, this, activePlayerName, moveLoc, activeColor);
			for (Location loc : captured) {
				//send captured messages.
				server.sendStoneCaptured(obsName, this, activePlayerName, loc, waitingColor);
			}
		}

		previousPassed = false;

		return true;
	}

	private boolean doPassForPlayer(String activePlayerName) {
		StoneColor activeColor = state.equals(GameState.BLACKMOVE) ? StoneColor.BLACK : StoneColor.WHITE;
		
		if (!getColorForPlayerName(activePlayerName).equals(activeColor)) {
			//Wrong player name. Not current player.
			String waitingPlayerName = getWaitingPlayerName();

			if (getWaitingPlayerName().equals(waitingPlayerName)) {
				//Late message from previous player. Log an error and send game over.
				sendErrorAndGameover(false, "Moved out of turn");

				assert false : "Programmer error: A non-participating Player has sent a message to the Game";
			}

			return false;
		}
		
		//send a pass message to observers and return true.
		for (String obsName : observers) {
			server.sendPlayerPass(obsName, this, activePlayerName);
		}
		
		if (previousPassed) {
			//if the previous person passed, return false. Game ends.
			for (String obsName : observers) {
				server.sendGameOver(obsName, this, 
						Rules.countOwnedTerritory(goBoard.board, StoneColor.BLACK) + goBoard.board.getNumberOfColor(StoneColor.BLACK), 
						Rules.countOwnedTerritory(goBoard.board, StoneColor.WHITE) + goBoard.board.getNumberOfColor(StoneColor.WHITE));
			}
			
			return false;
		}

		else {
			//set the previousPassed flag
			previousPassed = true;
			return true;
		}
	}

    /**
     * Sends an error message and an additional game over message to the players.
     * 
     * @param activeLost true if the active player lost, false if the waiting player lost
     * @param failureMessage the message to send.
     */
    public void sendErrorAndGameover(boolean activeLost, String failureMessage) {
    	for (String obsName : observers) {
    		server.sendPlayerError(obsName, this, activeLost ? getActivePlayerName() : getWaitingPlayerName(), failureMessage);
    		boolean blackWinner = (state == GameState.BLACKMOVE) != activeLost;
    		server.sendGameOver(obsName, this, blackWinner ? 1 : 0, blackWinner ? 0 : 1);
		}
    }
    
	private String getActivePlayerName() {
		return state.equals(GameState.BLACKMOVE) ? blackPlayer: whitePlayer;
	}

	private String getWaitingPlayerName() {
		return !state.equals(GameState.BLACKMOVE) ? blackPlayer: whitePlayer;
	}

	public ArrayList<String> getObserversAndPlayers() {
		return observers;
	}

	public void sendMessage(Runnable message) {
		mQueue.add(message);
	}
}
