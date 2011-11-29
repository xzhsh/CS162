package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.ClientInfo;

public class UnfinishedGame {
	private ClientInfo whitePlayerInfo;
	private ClientInfo blackPlayerInfo;
	private PlayerLogic blackPlayer;
	private PlayerLogic whitePlayer;
	
	private GoBoard board;

	enum ReconnectionStatus {
		NONE_CONNECTED,
		ONE_CONNECTED,
		TWO_CONNECTED
	}
	
	public UnfinishedGame(GoBoard board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
		this.board = board;
		this.blackPlayerInfo = blackPlayer;
		this.whitePlayerInfo = whitePlayer;
		this.blackPlayer = null;
		this.whitePlayer = null;
	}
	
	public Game reconnectGame() {
		if (blackPlayer == null || whitePlayer == null) {
			return null;
		}
		String name = blackPlayerInfo.getName() + "vs" + whitePlayerInfo.getName();
		return new Game(name, whitePlayer, whitePlayer, board);
	}
	
	public boolean matchesPlayer(PlayerLogic player) {
		if (blackPlayer == null && blackPlayerInfo == player.makeClientInfo()) {
			blackPlayer = player;
			return true;
		}
		if (whitePlayer == null && whitePlayerInfo == player.makeClientInfo()) {
			whitePlayer = player;
			return true;
		}
		return false;
	}
}
