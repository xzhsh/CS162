package edu.berkeley.cs.cs162.Client;

import java.io.IOException;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

abstract public class Observer extends BaseClient {

    protected int joinedGames;

    public Observer(String name, String password) {
        super(name, password, MessageProtocol.TYPE_OBSERVER);
    }
    
    protected void runExecutionLoop() throws IOException {
        while (joinedGames > 0) {
            handleMessage(getConnection().readFromServer());
        }
    }
}
