package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public abstract class PlayerLogic extends ClientLogic {


	private static final long HUMAN_PLAYER_TIMEOUT_IN_MS = 30000L;
	private static final long MACHINE_PLAYER_TIMEOUT_IN_MS = 2000;
	public static class HumanPlayerLogic extends PlayerLogic {
		
		public HumanPlayerLogic(Worker worker) {
			super(worker, HUMAN_PLAYER_TIMEOUT_IN_MS);
		}

		public ClientInfo makeClientInfo() {
			return MessageFactory.createHumanPlayerClientInfo(getWorker().getName());
		}
	}
	public static class MachinePlayerLogic extends PlayerLogic {
		public MachinePlayerLogic(Worker worker) {
			super(worker, MACHINE_PLAYER_TIMEOUT_IN_MS);
		}

		public ClientInfo makeClientInfo() {
			return MessageFactory.createMachinePlayerClientInfo(getWorker().getName());
		}
	}
	

	
	/**
	 * flag that says whether this worker is waiting for a game.
	 */
	private boolean waiting;
	
	public PlayerLogic(Worker worker, long playerTimeoutInMs) {
		super(worker);
		waiting = false;
	}

	@Override
	public Message handleMessage(Message message) {
		switch(message.getMsgType())
		{
		case MessageProtocol.OP_TYPE_DISCONNECT:
			getWorker().closeAndCleanup();
		return null;
		case MessageProtocol.OP_TYPE_WAITFORGAME:
			waiting = true;
			if (getWorker().handleRegisterAsWaiting()) 
			{
				return MessageFactory.createStatusOkMessage();
			} else
			{
				waiting = false;
				return MessageFactory.createErrorUnconnectedMessage();	
			}
		default:
			throw new AssertionError("Unimplemented message type");
		}
	}

	public boolean isWaiting() {
		return waiting;
	}

	@Override
	public void startGame(Game game) {
		waiting = false;
	}
	
	public abstract ClientInfo makeClientInfo();
}
