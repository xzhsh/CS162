package edu.berkeley.cs.cs162.Server;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.WritableList;

public class ObserverLogic extends ClientLogic {
    public ObserverLogic(Worker worker, WorkerSlave slave) {
        super(worker, slave);
    }

    public Message handleListGames() {
		List<GameInfo> gInfos = new ArrayList<GameInfo>();
		for (Game g : getWorker().getServer().getGameList()) {
		    gInfos.add(g.makeGameInfo());
		}
		WritableList gameInfoList = MessageFactory.createWritableListFromCollection(GameInfo.class, gInfos);
		return MessageFactory.createListGamesStatusOkMessage(gameInfoList);
	}

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

    public void cleanup() {
    	
    }

    public ClientInfo makeClientInfo() {
        return MessageFactory.createObserverClientInfo(getWorker().getName());
    }
}
