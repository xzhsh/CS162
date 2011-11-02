package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class MachinePlayer extends Player {

    public MachinePlayer(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
        // TODO init beep boop
    }

    public ClientInfo getClientInfo() {
        return MessageFactory.createMachinePlayerClientInfo(name);
    }

    public static void main(String[] args) {
        // TODO: Write me!
    }
}
