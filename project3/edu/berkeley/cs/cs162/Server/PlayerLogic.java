package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

public abstract class PlayerLogic extends ClientLogic {
    
	private static final int HUMAN_PLAYER_TIMEOUT_IN_MS = 30000;
    private static final int MACHINE_PLAYER_TIMEOUT_IN_MS = 2000;

    public static class HumanPlayerLogic extends PlayerLogic {
        public HumanPlayerLogic(GameServer server, ClientConnection con, String name) {
            super(server, con, name, HUMAN_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createHumanPlayerClientInfo(getName());
        }
    }

    public static class MachinePlayerLogic extends PlayerLogic {
        public MachinePlayerLogic(GameServer server, ClientConnection con, String name) {
            super(server, con, name, MACHINE_PLAYER_TIMEOUT_IN_MS);
        }

        public ClientInfo makeClientInfo() {
            return MessageFactory.createMachinePlayerClientInfo(getName());
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
	private Game currentlyPlayingGame;
	private PlayerWorkerSlave slave;
    
	public PlayerLogic(GameServer server, ClientConnection connection, String name, int playerTimeoutInMs) {
        super(server, name);
        state = PlayerState.CONNECTED;
        stateLock = new Lock();
        this.playerTimeoutInMs = playerTimeoutInMs;
        currentlyPlayingGame = null;
        slave = new PlayerWorkerSlave(connection, this, getTimeout());
        slave.start();
    }
    
    @Override
	public Message handleWaitForGame() {
		stateLock.acquire();
		if (state == PlayerState.CONNECTED) {
			state = PlayerState.WAITING;
			stateLock.release();
			Game unfinishedGame = getServer().checkForUnfinishedGame(this);
			if (unfinishedGame != null) {
				//TODO reconnect logic!
				throw new AssertionError("Unimplemented method");
			} else {
				getServer().addPlayerToWaitQueue(this);
				return MessageFactory.createStatusOkMessage();
			}
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
		    System.out.println("Game started for player " + getName());
    		state = PlayerState.PLAYING;
    		started = true;
    	} else {
    		System.out.println("Tried to start game for player " + getName() + " who was " + state);
    	}
    	stateLock.release();
    	return started;
    }
    
    public void beginGame(Game game) {
		assert state == PlayerState.PLAYING : "Tried to start game when game was not active";
		slave.handleStartNewGame(game);
    }
    
    public void cleanup()
    {
    	disconnectState();
    	slave.handleTerminate();
    }
    
	public abstract ClientInfo makeClientInfo();

	public int getTimeout() {
		return playerTimeoutInMs;
	}

	public void terminateGame() {
		stateLock.acquire();
    	if (state == PlayerState.PLAYING && currentlyPlayingGame != null)
    	{
        	//assert state == PlayerState.PLAYING : "Terminated game when not playing";
    		state = PlayerState.CONNECTED;
    		currentlyPlayingGame = null;
    	}
    	stateLock.release();
	}

	public void disconnectState() {
		stateLock.acquire();
		state = PlayerState.DISCONNECTED;
		stateLock.release();
	}
	public void setGame(Game game) {
		this.currentlyPlayingGame = game;
	}
	public void handleNextMove(Game game) {
		getSlave().handleNextMove(game);
	}
	private PlayerWorkerSlave getSlave() {
		return slave;
	}
	public void handleSendMessage(Message message) {
		getSlave().handleSendMessage(message);
	}
}
