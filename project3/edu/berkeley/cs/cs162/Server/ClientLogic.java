package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.ClientMessages;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public abstract class ClientLogic {
    private Worker worker;
    Lock observingLock;
    public ClientLogic(Worker worker) {
        this.worker = worker;
        observingLock = new Lock();
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
    
    public WorkerSlave getWorkerSlave() {
		return getWorker().getSlave();
	}

    public Message handleMessage(Message message) {
        switch (message.getMsgType()) {
            case MessageProtocol.OP_TYPE_LISTGAMES: {
                return handleListGames();
            }
            case MessageProtocol.OP_TYPE_JOIN: {
                return handleJoinGame(((ClientMessages.JoinMessage) message).getGameInfo());
            }
            case MessageProtocol.OP_TYPE_LEAVE: {
                return handleLeaveGame(((ClientMessages.LeaveMessage)message).getGameInfo());
            }
            case MessageProtocol.OP_TYPE_WAITFORGAME: {
            	return handleWaitForGame();
            }
            case MessageProtocol.OP_TYPE_DISCONNECT: {
                getWorker().closeAndCleanup();
                return null;
            }
        }
        throw new AssertionError("Unimplemented Method");
    }
    
    public Message handleWaitForGame() {
		return MessageFactory.createErrorRejectedMessage();
	}

	public Message handleLeaveGame(GameInfo gameInfo) {
		return MessageFactory.createErrorRejectedMessage();
	}

    public Message handleJoinGame(GameInfo gameInfo) {
		return MessageFactory.createErrorRejectedMessage();
	}

    public Message handleListGames() {
		return MessageFactory.createErrorRejectedMessage();
	}

    public abstract void cleanup();
	public abstract ClientInfo makeClientInfo();
	public abstract WorkerSlave createSlaveThread(ClientConnection connection);
}
