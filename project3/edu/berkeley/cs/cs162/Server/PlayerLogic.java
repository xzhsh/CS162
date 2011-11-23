package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public abstract class PlayerLogic extends ClientLogic {
    
	private static final int HUMAN_PLAYER_TIMEOUT_IN_MS = 30000;
    private static final int MACHINE_PLAYER_TIMEOUT_IN_MS = 2000;

    public static class HumanPlayerLogic extends PlayerLogic {
        public HumanPlayerLogic(Worker worker) {
            super(worker, HUMAN_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createHumanPlayerClientInfo(getWorker().getClientName());
        }
    }

    public static class MachinePlayerLogic extends PlayerLogic {
        public MachinePlayerLogic(Worker worker) {
            super(worker, MACHINE_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createMachinePlayerClientInfo(getWorker().getClientName());
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
    private int playerTimeoutInMs;
    public PlayerLogic(Worker worker, int playerTimeoutInMs) {
        super(worker);
        state = PlayerState.CONNECTED;
        stateLock = new Lock();
        this.playerTimeoutInMs = playerTimeoutInMs;
    }
    @Override
	public Message handleWaitForGame() {
		stateLock.acquire();
		if (state == PlayerState.CONNECTED) {
		    state = PlayerState.WAITING;
		    stateLock.release();
		    System.out.println("Wait for game message received for player " + getWorker().getName());
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
		    System.out.println("Game started for player " + getWorker().getName());
    		state = PlayerState.PLAYING;
    		started = true;
    	} else {
    		System.out.println("Tried to start game for player " + getWorker().getClientName() + " who was " + state);
    	}
    	stateLock.release();
    	return started;
    }
    
    public void beginGame(Game game) {
		assert state == PlayerState.PLAYING : "Tried to start game when game was not active";
		((PlayerWorkerSlave)getWorkerSlave()).handleStartNewGame(game);
    }
    
    public void cleanup()
    {
    	disconnectState();
    }
    
	public abstract ClientInfo makeClientInfo();

	public int getTimeout() {
		return playerTimeoutInMs;
	}

	public void terminateGame() {
		stateLock.acquire();
    	if (state == PlayerState.PLAYING)
    	{
        	//assert state == PlayerState.PLAYING : "Terminated game when not playing";
    		state = PlayerState.CONNECTED;
    	}
    	stateLock.release();
	}

	public void disconnectState() {
		stateLock.acquire();
		state = PlayerState.DISCONNECTED;
		stateLock.release();
	}
	
	public WorkerSlave createSlaveThread(ClientConnection connection) {
		return new PlayerWorkerSlave(connection, getWorker(), getTimeout());
	}
}
