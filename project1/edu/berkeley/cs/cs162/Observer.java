package edu.berkeley.cs.cs162;

/**
   An Observer is a thread that can observe games. Observers may watch any
   number of games they wish and will be notified of all game events, but can
   choose to respond to these events in whatever way they want.
*/
public abstract class Observer implements Runnable {

    String name;
    
    /**
     * Count of how many games this observer is currently viewing
     */
    private int viewingCount;

    private ThreadSafeQueue<Runnable> mQueue;
    protected boolean done;

    public Observer()
    {
    	viewingCount = 0;
    	mQueue = new ThreadSafeQueue<Runnable>(500);
        done = false;
        name = null;
    }
    
    protected ThreadSafeQueue<Runnable> getMessageQueue() {
    	return mQueue;
    }

    protected Runnable getNextMessage() {
        return mQueue.get();
    }

    protected void addMessage(Runnable message) {
        mQueue.add(message);
    }
    
    /**
     * Increments the number of games this observer is viewing by one.
     * 
     * The game server is responsible for incrementing this whenever an observer is added to a game.
     */
    public void incrementViewing() {
    	viewingCount++;
    }
    /**
     * Decrements the number of games this observer is viewing by one
     * 
     * The game server is responsible for decrementing this whenever an observer is removed from a game.
     */
    public void decrementViewing() {
    	viewingCount--;
    	assert viewingCount >= 0;
    }
    /**
     * @return true if not viewing anything
     */
    public int stillObserving() {
    	return viewingCount;
    }
    
    /* Sets this observer's name. */
    public void setName(String name) { this.name = name; }
    
    /* Returns the name of this observer. */
    public String getName() { return name; }

    /* Handler methods for all game events */
    public abstract void handleGameStart(String gameName, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs);
    public abstract void handleGameOver(String gameName, double blackPlayerScore, double whitePlayerScore);
    public abstract void handleMakeMove(String gameName, String playerName);    
    public abstract void handleStonePlaced(String gameName, String playerName, Location loc, StoneColor color);
    public abstract void handleStoneCaptured(String gameName, String playerName, Location loc, StoneColor color);
    public abstract void handlePlayerPass(String gameName, String playerName);
    public abstract void handlePlayerError(String gameName, String playerName, String errorDescription);

    /* beginExit() is called to inform a running observer that it should terminate */
    public abstract void beginExit();

    public abstract void run();
}


