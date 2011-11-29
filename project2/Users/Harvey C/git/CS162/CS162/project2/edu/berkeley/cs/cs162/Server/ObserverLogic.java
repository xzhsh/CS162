package edu.berkeley.cs.cs162.Server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.WritableList;

/**
 * Logic for handling messages from observer.
 * @author Excess
 *
 */
public class ObserverLogic extends ClientLogic {

    /**
     * List of currently observed game. 
     */
    Set<Game> currentlyObserving;
    
    public ObserverLogic(Worker worker, WorkerSlave slave) {
        super(worker, slave);
        currentlyObserving = new HashSet<Game>();
    }

    /**
     * @return a list of gameinfos that are currently active. 
     */
    public Message handleListGames() {
		List<GameInfo> gInfos = new ArrayList<GameInfo>();
		for (Game g : getWorker().getServer().getGameList()) {
		    gInfos.add(g.makeGameInfo());
		}
		WritableList gameInfoList = MessageFactory.createWritableListFromCollection(GameInfo.class, gInfos);
		return MessageFactory.createListGamesStatusOkMessage(gameInfoList);
	}

    /**
     * Attempts to join a game with observer. This also stores the game in the corresponding workerslave.
     */
	public Message handleJoinGame(GameInfo gameInfo) {
		Game game = getWorker().getServer().getGame(gameInfo.getName());
		if (game == null) {
		    return MessageFactory.createErrorInvalidGameMessage();
		}
		if (game.addObserver(getWorker()))
		{
			return MessageFactory.createJoinStatusOkMessage(game.makeBoardInfo(), game.getBlackPlayer().makeClientInfo(), game.getWhitePlayer().makeClientInfo());
		}
		else {
			return MessageFactory.createErrorInvalidGameMessage();
		}
	}

	public Message handleLeaveGame(GameInfo gameInfo) {
		Game desiredGame = getWorker().getServer().getGame(gameInfo.getName());
		if (desiredGame == null)
		{
		    return MessageFactory.createErrorInvalidGameMessage();
		}
		if (desiredGame.removeObserver(getWorker()))
		{
			return MessageFactory.createStatusOkMessage();
		}
		else 
		{
			return MessageFactory.createErrorInvalidGameMessage();
		}
	}

	/**
	 * Cleans up this observer by removing itself from all currently observing Games.
	 * 
	 * Careful for deadlocks here! it is both acquiring observing lock and the readerwriterlock inside game!
	 * This method cannot be called at the same time as addGame/removeGame!
	 * 
	 * Right now, add and remove game will only be called after acquiring the lock inside game as a result of calling 
	 * game.addObserver()/game.removeObserver()
	 */
    public void cleanup() {
    	observingLock.acquire();
    	for (Game game : currentlyObserving)
    	{
    		game.removeObserver(getWorker());
    	}
    	observingLock.release();
    }

    public ClientInfo makeClientInfo() {
        return MessageFactory.createObserverClientInfo(getWorker().getName());
    }

	public void addGame(Game game) {
		observingLock.acquire();
		currentlyObserving.add(game);
		observingLock.release();
	}
	public void removeGame(Game game) {
		observingLock.acquire();
		currentlyObserving.remove(game);
		observingLock.release();
	}
}
