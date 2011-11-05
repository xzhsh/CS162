package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
        // TODO other init stuff
    }


    public ClientInfo getClientInfo() {
        return MessageFactory.createHumanPlayerClientInfo(name);
    }

    public static void main(String[] args) {
        // TODO: Write me!
    }
}
