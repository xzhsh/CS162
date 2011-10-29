package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.Message;

public abstract class ClientLogic {

	public static ClientLogic getClientLogicForClientType(Worker worker, byte playerType) {
		// TODO Auto-generated method stub
		return null;
	}

	public abstract Message handleMessage(Message readMessageFromInput);
}
