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
	
	@Override
    public void handleGameOver(Game game, double blackScore, double whiteScore, Worker winner)
    {
    	//This may or may not work, the observer could have left already. 
    	//But it's fine to do nothing if it's not in a game anyways.
    	game.removeObserver(getMaster());
    	super.handleGameOver(game, blackScore, whiteScore, winner);
    }

    @Override
    public void handleGameOverError(Game game, double blackScore, double whiteScore, Worker winner, byte reason, Worker errorPlayer, String errorMessage)
    {
    	//This may or may not work, the observer could have left already. 
    	//But it's fine to do nothing if it's not in a game anyways.
    	game.removeObserver(getMaster());
    	super.handleGameOverError(game, blackScore, whiteScore, winner, reason, errorPlayer, errorMessage);
    }
}
