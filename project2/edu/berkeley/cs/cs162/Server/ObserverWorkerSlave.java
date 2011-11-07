package edu.berkeley.cs.cs162.Server;

/**
 * Worker slave in charge of observers. Has some special logic for ending games.
 * @author xshi
 *
 */
public class ObserverWorkerSlave extends WorkerSlave {

	public ObserverWorkerSlave(ClientConnection connection, Worker master) {
		super(connection, master);
	}
	
    /**
     * Tells this worker that the game has begun.
     * 
     * The worker should save the game if needed.
     * 
     * @param game
     */
    public void handleGameStart(Game game)
    {
    	super.handleGameStart(game);
    }
    
    /**
     * Tells this worker that the game has finished.
     * 
     * The worker should clean up after itself when this message is received.
     * 
     * @param game
     * @param blackScore
     * @param whiteScore
     * @param winner
     */
    public void handleGameOver(Game game, double blackScore, double whiteScore, Worker winner)
    {
    	//This may or may not work, the observer could have left already. 
    	//But it's fine to do nothing if it's not in a game anyways.
    	game.removeObserver(getMaster());
    	super.handleGameOver(game, blackScore, whiteScore, winner);
    }
    
    /**
     * Tells this worker that an error has occurred.
     * 
     * The worker should clean up after itself when this message is received.
     * 
     * @param game
     */
    public void handleGameOverError(Game game, double blackScore, double whiteScore, Worker winner, byte reason, Worker errorPlayer, String errorMessage)
    {
    	//This may or may not work, the observer could have left already. 
    	//But it's fine to do nothing if it's not in a game anyways.
    	game.removeObserver(getMaster());
    	super.handleGameOverError(game, blackScore, whiteScore, winner, reason, errorPlayer, errorMessage);
    }
}
