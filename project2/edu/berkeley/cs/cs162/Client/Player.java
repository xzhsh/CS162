package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.StoneColor;

abstract public class Player extends BaseClient {

    GoBoard board;
    boolean waitingForGames;
    StoneColor currentColor;
    StoneColor opponentColor;
    String gameName;

    public Player(String name, byte type) {
        super(name, type);
        board = new GoBoard(10);
        waitingForGames = true;
        gameName = "";
    }

    public Player(String name) {
        this(name, (byte) -1);
    }

    public Player() {
        this("");
    }

    public void setBoard(GoBoard b) {
        board = b;
    }

    public GoBoard getBoard() {
        return board;
    }

    public boolean getWaitingForGames() {
        return waitingForGames;
    }

    public void setWaitingForGames(boolean b) {
        waitingForGames = b;
    }

    public void setGameName(String s) {
        gameName = s;
    }

    public String getGameName() {
        return gameName;
    }
}
