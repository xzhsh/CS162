package edu.berkeley.cs.cs162;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
   The GameServer manages all the running games and players. Any messages passed
   between players and games must go through the GameServer via the provided
   interface.
 */
public class GameServer implements Runnable {
	/**
	 * Lock for the name string to game hashmap.
	 */
	private Lock gamesLock;
	
	/**
	 * Lock for the observer name string to observer hashmap.
	 */
	private Lock observersLock;
	
	/**
	 * Lock for the openThreads counter. 
	 * 
	 * This is incremented in the launcher thread, 
	 * and decremented in the GameServer thread
	 */
	private Lock openThreadsLock;
	
	/**
	 * represents all the current game threads going.
	 */
	private Map<Game, Thread> gameThreads;
	
	private Map<String, Game> games;
	private Map<String, Observer> observers;
	
	private int openThreads;

	private ThreadSafeQueue<Runnable> mQueue;
	private boolean done;

	private Lock gameThreadsLock;

	private Lock addingGameLock;
	
	
	/**
	 * Initializes the Locks and Maps for this server
	 */
	public GameServer ()
	{
		//no need for synchronization here.
		gameThreads = new HashMap<Game, Thread>();
		games = new HashMap<String, Game>();
		gamesLock = new Lock();
		observers = new HashMap<String, Observer>();
		observersLock = new Lock();
		mQueue = new ThreadSafeQueue<Runnable>(10);
		done = false;
		openThreads = 0;
		gameThreadsLock = new Lock();
		openThreadsLock = new Lock();
		addingGameLock = new Lock();
	}
	/**
       Creates a game with the specified game name, players,
       initial board configuration, and observers and stores it on the message queue.
       
       The game server thread is responsible for launching games.
     */
    public void createGame(String gameName, Player blackPlayer, Player whitePlayer, Board initialBoard, int moveTimeoutInMs, Vector<Observer> initialObservers) {
		Game newGame = new Game(this, gameName, blackPlayer.getName(), whitePlayer.getName(), initialBoard, moveTimeoutInMs);

		for (Observer o : initialObservers) {
			newGame.addObserver(o.getName());
			observersLock.acquire();
			observers.put(o.getName(), o);
			observersLock.release();
		}

		observersLock.acquire();
		observers.put(blackPlayer.getName(), blackPlayer);
		observers.put(whitePlayer.getName(), whitePlayer);
		observersLock.release();
		
		blackPlayer.setGameName(gameName);
		whitePlayer.setGameName(gameName);
		blackPlayer.setPlayerColor(StoneColor.BLACK);
		whitePlayer.setPlayerColor(StoneColor.WHITE);
		
		blackPlayer.setServer(this);
		whitePlayer.setServer(this);

		blackPlayer.initializeBoard(initialBoard);
		whitePlayer.initializeBoard(initialBoard);
		
		gamesLock.acquire();
		games.put(gameName, newGame);
		gamesLock.release();

		final Game g = newGame;

		mQueue.add(new Runnable() {
			public void run() {
				Thread gameThread = new Thread(g);
    			
    			gameThreadsLock.acquire();
    			gameThreads.put(g, gameThread);
    			gameThreadsLock.release();
    			
    			for (String obsName : g.getObserversAndPlayers()) {
    				getObserver(obsName).incrementViewing();
    			}

    			gameThread.start();
			}
		});
		
		openThreadsLock.acquire();
		openThreads++;
		openThreadsLock.release();
    }
    
    /**
       @return The Game instance with the specified name.
     */
    private Game getGame(String gameName) {
    	Game game;
    	gamesLock.acquire();
    	game = games.get(gameName);
    	gamesLock.release();
    	return game;
    }

    /**
       @return The Observer instance with the specified name.
     */
    private Observer getObserver(String observerName) {
        Observer obs;
        observersLock.acquire();
        obs = observers.get(observerName);
        observersLock.release();
        return obs;
    }


    /**
       Methods to send information about game events to observers. Games may only notify
       their observers of game events by calling these methods.
     */
    
    public void sendGameStart(String targetObserverName, Game game, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) {
	    getObserver(targetObserverName).handleGameStart(game.getName(), blackPlayerName, whitePlayerName, board, moveTimeoutInMs);
    }

    public void sendGameOver(String targetObserverName, Game game, double blackPlayerScore, double whitePlayerScore) {
	    getObserver(targetObserverName).handleGameOver(game.getName(), blackPlayerScore, whitePlayerScore);
    }

    public void sendMakeMove(String targetObserverName, Game game, String playerName) {
	    getObserver(targetObserverName).handleMakeMove(game.getName(), playerName);
    }

    public void sendStonePlaced(String targetObserverName, Game game, String playerName, Location loc, StoneColor color) {
	    getObserver(targetObserverName).handleStonePlaced(game.getName(), playerName, loc, color);
    }

    /**
     * @param targetObserverName
     * @param game
     * @param playerName player who captured the stone
     * @param loc the location of the stone
     * @param color the color of the stone
     */
    public void sendStoneCaptured(String targetObserverName, Game game, String playerName, Location loc, StoneColor color) {
    	getObserver(targetObserverName).handleStoneCaptured(game.getName(), playerName, loc, color);
    }

    public void sendPlayerPass(String targetObserverName, Game game, String playerName) {
    	getObserver(targetObserverName).handlePlayerPass(game.getName(), playerName);
    }

    public void sendPlayerError(String targetObserverName, Game game, String playerName, String errorDescription) {
    	getObserver(targetObserverName).handlePlayerError(game.getName(), playerName, errorDescription);
    }

    /**
       Methods for players to send move information to games. Players may only notify
       games of moves they want to make by calling these methods.
     */
    
    public void sendMoveToGame(Player player, String gameName, Location loc) {
    	getGame(gameName).makeMove(player.getName(), loc);
    }

    public void sendPassMoveToGame(Player player, String gameName) {
    	getGame(gameName).makePassMove(player.getName());
    }

    /**
     * Blocks until all running games have finished.
     */
    public void waitForGames() {
    	doneAddingGames();
    	addingGameLock.acquire();
    	addingGameLock.release();
    	for (Thread t : gameThreads.values()) {
    		try {
				t.join();
			}
            catch (InterruptedException e) {
				assert false : "Game Threads should not be interrupted";
			}
    	}
    }

    /**
     * Signals to the GameServer that all games have been added.
     * This should be called by the same thread as 
     */
    private void doneAddingGames() {
		mQueue.add(new Runnable() {
			public void run() {
				done = true;
				addingGameLock.release();
			}
		});
    }
    
    /**
     * Runs the game server.
     * 
     * This thread will start and stop games as they play out.
     * 
     * Once the wait for games method is called, the server thread will terminate when all current games request a stop.
     */
    public void run() {
    	addingGameLock.acquire();
    	//loop until both the done adding flag has been set, and there are 0 open threads.
    	while (!done || openThreads != 0) {
    		Runnable message = mQueue.get();
    		message.run();
    	}
    }
    
    /**
     * Sends a message to the game server thath the game is done.
     * @param game The {@link Game} that is finished.
     */
	public void sendGameTearDown(Game game) {
		final Game g = game;
		
		mQueue.add(new Runnable() {
			public void run() {
				for (String obsName : g.getObserversAndPlayers()) {
    				Observer obs = getObserver(obsName);
    				obs.decrementViewing();
    				if (obs.stillObserving() == 0) {
    					//if the observer is not still observing, send teardown message
    					obs.beginExit();
    				}
    			}

    			openThreadsLock.acquire();
    			openThreads--;
    			openThreadsLock.release();
			}
		});
	}
	
	public void redirectLoggingObserver(String observerName,
			OutputStream output) {
		Observer obs = getObserver(observerName);
		if (obs instanceof LoggingObserver)
		{
			((LoggingObserver)obs).setOutputStream(output);
		}
		else 
		{
			assert false : "Cannot set output stream because specified name is not a LoggingObserver";
		}
	}

}
