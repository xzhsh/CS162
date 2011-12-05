package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public class UnfinishedGame {
	private ClientInfo whitePlayerInfo;
	private ClientInfo blackPlayerInfo;
	private PlayerLogic blackPlayer;
	private PlayerLogic whitePlayer;
	private int gameID;
	private GoBoard board;
	private String name;

	enum ReconnectionStatus {
		NONE_CONNECTED,
		ONE_CONNECTED,
		TWO_CONNECTED
	}
	
	public UnfinishedGame(String name, GoBoard board, ClientInfo blackPlayer, ClientInfo whitePlayer, int gameID) {
		this.board = board;
		this.blackPlayerInfo = blackPlayer;
		this.whitePlayerInfo = whitePlayer;
		this.blackPlayer = null;
		this.whitePlayer = null;
		this.gameID = gameID;
		this.name = name;
	}
	
	public Game reconnectGame() {
		if (blackPlayer == null || whitePlayer == null) {
			return null;
		}
		return new Game(name, whitePlayer, whitePlayer, board, gameID);
	}
	
	public boolean matchesPlayer(PlayerLogic player) {
		if (blackPlayer == null && blackPlayerInfo.equals(player.makeClientInfo())) {
			blackPlayer = player;
			return true;
		}
		if (whitePlayer == null && whitePlayerInfo.equals(player.makeClientInfo())) {
			whitePlayer = player;
			return true;
		}
		return false;
	}

	public int getGameID() {
		return gameID;
	}

	public StoneColor getColorForInfo(ClientInfo info) {
		if (info.equals(blackPlayerInfo))
			return StoneColor.BLACK;
		else if (info.equals(whitePlayerInfo))
			return StoneColor.WHITE;
		else 
			return null;
	}

	public GameInfo makeGameInfo() {
		return MessageFactory.createGameInfo(name);
	}
	public BoardInfo makeBoardInfo() {
		return MessageFactory.createBoardInfo(board.getCurrentBoard());
	}

	public ClientInfo getWhiteInfo() {
		return whitePlayerInfo;
	}
	public ClientInfo getBlackInfo() {
		return blackPlayerInfo;
	}

	public void wakeOtherPlayer(PlayerLogic playerLogic) {
		PlayerLogic other = null;
		if (playerLogic == blackPlayer) {
			other = whitePlayer;
		} else if (playerLogic == whitePlayer)
		{
			other = blackPlayer;
		} else {
			assert false : "Programmer error, player logic not in the unfinished game";
		}
		other.wakeReconnection();
	}
}
