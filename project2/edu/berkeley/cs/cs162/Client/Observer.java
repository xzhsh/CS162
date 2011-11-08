package edu.berkeley.cs.cs162.Client;

abstract public class Observer extends BaseClient {

    protected int joinedGames;

    public Observer() {
        this("");
    }

    public Observer(String name) {
        this(name, (byte) -1);
    }

    public Observer(String name, byte type) {
    	super(name,type);
        joinedGames = 0;
    }
}
