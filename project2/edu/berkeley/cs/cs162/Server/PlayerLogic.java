package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public abstract class PlayerLogic extends ClientLogic {
    private static final long HUMAN_PLAYER_TIMEOUT_IN_MS = 30000L;
    private static final long MACHINE_PLAYER_TIMEOUT_IN_MS = 2000;

    public static class HumanPlayerLogic extends PlayerLogic {

        public HumanPlayerLogic(Worker worker, WorkerSlave slave) {
            super(worker, slave, HUMAN_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createHumanPlayerClientInfo(getWorker().getName());
        }
    }

    public static class MachinePlayerLogic extends PlayerLogic {
        public MachinePlayerLogic(Worker worker, WorkerSlave slave) {
            super(worker, slave, MACHINE_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createMachinePlayerClientInfo(getWorker().getName());
        }
    }

    /**
     * flag that says whether this worker is waiting for a game.
     */
    enum PlayerState {
    	CONNECTED,
    	WAITING,
    	PLAYING,
    	DISCONNECTED
    }
    
    private PlayerState state;
    private Lock stateLock;
    private long playerTimeoutInMs;
    public PlayerLogic(Worker worker, WorkerSlave slave, long playerTimeoutInMs) {
        super(worker, slave);
        state = PlayerState.CONNECTED;
        stateLock = new Lock();
        this.playerTimeoutInMs = playerTimeoutInMs;
    }

	public Message handleWaitForGame() {
		stateLock.acquire();
		if (state == PlayerState.CONNECTED) {
		    state = PlayerState.WAITING;
		    stateLock.release();
		    getWorker().getServer().addPlayerToWaitQueue(this);
		    return MessageFactory.createStatusOkMessage();
		}
		else
		{
			stateLock.release();
			return MessageFactory.createErrorRejectedMessage();
		}
	}

	/**
	 * Switches the client state from waiting to playing.
	 * @return
	 */
    public boolean startGame()
    {
    	boolean started = false;
    	stateLock.acquire();
    	if (state == PlayerState.WAITING)
    	{
    		state = PlayerState.PLAYING;
    		started = true;
    	}
    	stateLock.release();
    	return started;
    }
    
    public void beginGame(Game game) {
		assert state == PlayerState.PLAYING : "Tried to start game when game was not active";
		getWorkerSlave().handleGameStart(game);
    }
    
    public void cleanup()
    {
    	state = PlayerState.DISCONNECTED;
    }
    
	public abstract ClientInfo makeClientInfo();

	public long getTimeout() {
		// TODO Auto-generated method stub
		return playerTimeoutInMs;
	}
}
