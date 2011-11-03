package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.Message;

public class PlayerLogic extends ClientLogic {

	public PlayerLogic(long playerTimeoutInMs) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Message handleMessage(Message message) {
		// TODO Auto-generated method stub
		if (message != null)
			System.out.println("Message Received with opcode: " + message.getMsgType());
		return null;
		//throw new AssertionError("Unimplemented Method");
	}

}
