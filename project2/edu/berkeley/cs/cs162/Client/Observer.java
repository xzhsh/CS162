package edu.berkeley.cs.cs162.Client;

import java.io.IOException;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

abstract public class Observer extends BaseClient {

    protected int joinedGames;

    public Observer() {
        this("");
    }

    public Observer(String name) {
        this(name, MessageProtocol.TYPE_OBSERVER);
    }

    public Observer(String name, byte type) {
    	super(name,type);
        joinedGames = 0;
    }
    
    protected void runExecutionLoop() throws IOException {
        while (joinedGames > 0) {
            handleMessage(getConnection().readFromServer());
        }
    }
}
