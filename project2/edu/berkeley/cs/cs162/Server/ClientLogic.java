package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public abstract class ClientLogic {


    private Worker worker;

    public ClientLogic(Worker worker) {
        this.worker = worker;
    }

    public static ClientLogic getClientLogicForClientType(Worker worker, byte playerType) {
        switch (playerType) {
            case MessageProtocol.TYPE_HUMAN:
                return new PlayerLogic.HumanPlayerLogic(worker);
            case MessageProtocol.TYPE_MACHINE:
                return new PlayerLogic.MachinePlayerLogic(worker);
            case MessageProtocol.TYPE_OBSERVER:
                return new ObserverLogic(worker);
        }
        throw new AssertionError("Unknown Client Type");
    }

    public Worker getWorker() {
        return worker;
    }

    public abstract Message handleMessage(Message readMessageFromInput);

    public abstract void startGame(Game game);

    public abstract ClientInfo makeClientInfo();
}
