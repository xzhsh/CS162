package edu.berkeley.cs.cs162.Server;

import java.util.ArrayList;
import java.util.List;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.ClientMessages;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ReturnMessages;
import edu.berkeley.cs.cs162.Writable.WritableList;

public class ObserverLogic extends ClientLogic {
	private List<Game> currentlyObserving;
	/**
	 * We will use an ordinary lock here because we only will be reading this 
	 * when modifying the currentlyObserving list anyways.
	 */
	private Lock gameListLock;
	public ObserverLogic(Worker worker) {
		super(worker);
		currentlyObserving = new ArrayList<Game>();
		gameListLock = new Lock();
	}

	@Override
	public Message handleMessage(Message message) {
		switch (message.getMsgType())
		{
			case MessageProtocol.OP_TYPE_LISTGAMES:
			{
				List<GameInfo> gInfos = new ArrayList<GameInfo>();
				for (Game g :getWorker().getServer().getGameList())
				{
					gInfos.add(g.makeGameInfo());
				}
				WritableList gameInfoList = MessageFactory.createWritableListFromCollection(GameInfo.class, gInfos);
				return MessageFactory.createStatusOkMessage(gameInfoList);
			}
			case MessageProtocol.OP_TYPE_JOIN:
			{
				GameInfo gameInfo = ((ClientMessages.JoinMessage)message).getGameInfo();
				Game game = getWorker().getServer().getGame(gameInfo.getName());
				if (game == null) {return ReturnMessages.createErrorInvalidGameMessage();}
				startGame(game);
				return MessageFactory.createStatusOkMessage(game.makeBoardInfo(), game.getBlackPlayer().makeClientInfo(), game.getWhitePlayer().makeClientInfo());
			}
			case MessageProtocol.OP_TYPE_LEAVE:
			{
				GameInfo gameInfo = ((ClientMessages.JoinMessage)message).getGameInfo();
				Game desiredGame = getWorker().getServer().getGame(gameInfo.getName());
				if (desiredGame == null) {return ReturnMessages.createErrorInvalidGameMessage();}
				startGame(desiredGame);
			}
		}
		throw new AssertionError("Unimplemented Method");
	}
	
	/**
	 * This will probably only be called by the ObserverLogic after a message.
	 */
	public void startGame(Game game) {
		gameListLock.acquire();
		game.addObserver(getWorker());
		currentlyObserving.add(game);
		gameListLock.release();
	}
	
	/**
	 * This will probably only be called by the ObserverLogic after a message.
	 */
	public void leaveGame(Game game) {
		gameListLock.acquire();
		game.removeObserver(getWorker());
		currentlyObserving.add(game);
		gameListLock.release();
	}
	
	public void cleanup()
	{
		gameListLock.acquire();
		for (Game g : currentlyObserving)
		{
			g.removeObserver(getWorker());
		}
		gameListLock.release();
	}
	
	public ClientInfo makeClientInfo()
	{
		return MessageFactory.createObserverClientInfo(getWorker().getName());
	}
}
