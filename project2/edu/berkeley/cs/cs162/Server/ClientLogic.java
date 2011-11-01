package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public abstract class ClientLogic {

	private static final long HUMAN_PLAYER_TIMEOUT_IN_MS = 30000L;
	private static final long MACHINE_PLAYER_TIMEOUT_IN_MS = 2000;

	public static ClientLogic getClientLogicForClientType(Worker worker, byte playerType) {
		switch(playerType)
		{
		case MessageProtocol.TYPE_HUMAN:
			return new PlayerLogic(HUMAN_PLAYER_TIMEOUT_IN_MS);
		case MessageProtocol.TYPE_MACHINE:
			return new PlayerLogic(MACHINE_PLAYER_TIMEOUT_IN_MS);
		case MessageProtocol.TYPE_OBSERVER:
			return new ObserverLogic();
		}
		throw new AssertionError("Unknown Client Type");
	}

	public abstract Message handleMessage(Message readMessageFromInput);
}
