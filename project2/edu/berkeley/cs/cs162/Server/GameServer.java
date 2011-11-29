package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

public class GameServer {
<<<<<<< HEAD
    public static final int GLOBAL_TIMEOUT_IN_MS = 3000;
    private static final int WAITING_CONNECTION_BUFFER_SIZE = 200;
=======
    public static final int GLOBAL_TIMEOUT_IN_MS = 300000;
    private static final int WAITING_CONNECTION_BUFFER_SIZE = 100;
>>>>>>> b70978eefef586adb4b41ceaa2cc1fb5f06c2212
    /**
     * RNG for this game server.
     */
    private Random rng;
    /**
     * Map of the current sockets waiting for a pairing before spawning a worker thread.
     */
    private Map<Integer, SocketWithTimeStamp> waitingSocketMap;
    private ReaderWriterLock waitingSocketMapLock;
    /**
     * connectionQueue for incoming socket connection
     */
    private ThreadSafeQueue<Socket> connectionQueue;
    /**
     * Queue for players waiting for games.
     */
    private ThreadSafeQueue<PlayerLogic> waitingPlayerQueue;
    /**
     * Mapping between the name of a client and the worker handling the client's connection
     */
    private Map<String, Worker> nameToWorkerMap;
    private ReaderWriterLock nameToWorkerMapLock;

    /**
     * How many maximum connected clients there can be.
     */
    private int clientLimit;

    /**
     * The number of clients currently connected.
     */
    private int clientsConnected;
    private ReaderWriterLock clientsConnectedLock;

    /**
     * Set of active games.
     */
    private Map<String, Game> activeGames;
    private ReaderWriterLock activeGamesLock;

    private PrintStream logStream;
	private boolean ready;

    /**
     * Constructor for gameServer
     *
     * @param clientLimit             how many max clients can be connected
     * @param handshakeThreadPoolSize How many {@link ConnectionWorker} threads there will be.
     */
    public GameServer(int clientLimit, int handshakeThreadPoolSize, PrintStream logStream) {
        this.clientLimit = clientLimit;
        this.logStream = logStream;
        rng = new Random();
        waitingSocketMap = new HashMap<Integer, SocketWithTimeStamp>();
        
        connectionQueue = new ThreadSafeQueue<Socket>(WAITING_CONNECTION_BUFFER_SIZE);
        waitingPlayerQueue = new ThreadSafeQueue<PlayerLogic>(clientLimit);
        activeGames = new HashMap<String, Game>();
        nameToWorkerMap = new HashMap<String, Worker>();
        waitingSocketMapLock = new ReaderWriterLock();
        nameToWorkerMapLock = new ReaderWriterLock();
        clientsConnectedLock = new ReaderWriterLock();
        activeGamesLock = new ReaderWriterLock();
        clientsConnected = 0;
        
        for (int i = 0; i < handshakeThreadPoolSize; i++) {
            //NOTE These worker threads will never be cleaned up.
            //However, since this is supposed to either run forever or is terminated by the process
            //it shouldn't really matter.
            ConnectionWorker hsWorker = new ConnectionWorker(this);
            Thread hsThread = new Thread(hsWorker);
            hsThread.start();
        }
        //NOTE the garbage collector thread will never be cleaned up either.
        //However the same logic holds for HandshakeWorker threads.
        SocketGarbageCollector collector = new SocketGarbageCollector(waitingSocketMap, waitingSocketMapLock, GLOBAL_TIMEOUT_IN_MS, 10, getLog());
        Thread collectorThread = new Thread(collector);
        collectorThread.start();
        MatchMakingWorker matchMaker = new MatchMakingWorker(this);
        (new Thread(matchMaker)).start();
    }

    /**
     * Starts waiting on a port to accept connections. This method should never return.
     *
     * @param portNumber
     */
    public void waitForConnectionsOnPort(int portNumber, InetAddress localIP) {
        try {
<<<<<<< HEAD
            ServerSocket server = new ServerSocket(portNumber, 50, localIP);
            ready = true;
            while (true) {
                Socket incomingConnection = server.accept();
                incomingConnection.setSoTimeout(GLOBAL_TIMEOUT_IN_MS);
                getLog().println("Found conneciton");
                connectionQueue.add(incomingConnection);
                getLog().println("Added conneciton");
=======
            ServerSocket server = new ServerSocket(portNumber, 100, localIP);
            server.setSoTimeout(GLOBAL_TIMEOUT_IN_MS);
            ready = true;
            while (true) {
                Socket incomingConnection = server.accept();
                getLog().println("Found connection");
                incomingConnection.setSoTimeout(GLOBAL_TIMEOUT_IN_MS);
                connectionQueue.add(incomingConnection);
                getLog().println("Added connection");
>>>>>>> b70978eefef586adb4b41ceaa2cc1fb5f06c2212
            }
        } catch (IOException e) {
            e.printStackTrace(getLog());
        }
    }

    /**
     * Sends a message to the named client through a worker.
     *
     * @param name
     * @param message
     */
    public void sendMessageToClient(String name, Message message) {
        Worker worker;
        nameToWorkerMapLock.readLock();
        worker = nameToWorkerMap.get(name);
        nameToWorkerMapLock.readUnlock();
        worker.handleSendMessageToClient(message);
    }

    /**
     * @return The random number generator used by this server.
     */
    public Random getRNG() {
        return rng;
    }

    /**
     * @return the next connection in queue to be handled.
     */
    public Socket getNextConnection() {
        return connectionQueue.get();
    }

    /**
     * handles the connection when the specified SYN_ID is received.
     *
     * @param SYN_ID
     * @param connection
     */
    public void handleSYN(int SYN_ID, Socket connection) {
        SocketWithTimeStamp otherConnection = null;
        logStream.println("Connection initiated with SYN_ID: " + SYN_ID);
        //Read whether the syn id exists already
        waitingSocketMapLock.writeLock();
        if (waitingSocketMap.containsKey(SYN_ID)) {
            otherConnection = waitingSocketMap.get(SYN_ID);
        }
        ////////////////////////////////////////
        if (otherConnection != null) {
            logStream.println("Pair found!");
            waitingSocketMap.remove(SYN_ID);
            initializeWorkerForConnection(connection, otherConnection.getConnection(), SYN_ID);
        } else {
            waitingSocketMap.put(SYN_ID, new SocketWithTimeStamp(connection));
        }
        waitingSocketMapLock.writeUnlock();
    }

    private void initializeWorkerForConnection(Socket connection1,
                                               Socket connection2, int SYN_ID) {
        if (tryIncrementConnectionCount()) 
        {
	        logStream.println("Initialized a worker with SYN_ID = " + SYN_ID);
	        Worker worker = new Worker(this, new ClientConnection(connection1, connection2, SYN_ID, getLog()));
	        worker.start();
        } else {
        	logStream.println("Maximum number of connections reached. SYN_ID = " + SYN_ID + " rejected.");
        	
        }
    }

    /**
     * Cleans up the resources held by this worker.
     *
     * @param name
     */
    protected void removeWorker(String name) {
        nameToWorkerMapLock.writeLock();
        nameToWorkerMap.remove(name);
        logStream.println("Client<" + name + "> disconnected");
        nameToWorkerMapLock.writeUnlock();
    }

    protected void decrementConnectionCount() {
        clientsConnectedLock.writeLock();
        clientsConnected--;
        assert clientsConnected >=0 : "cannot have negative client connected";
        logStream.println("Client count: " + clientsConnected);
        clientsConnectedLock.writeUnlock();
    }

    protected boolean tryIncrementConnectionCount() {
        boolean accepted = false;
    	clientsConnectedLock.writeLock();
        if (clientsConnected < clientLimit) {
        	clientsConnected++;
        	logStream.println("Client count: " + clientsConnected);
        	accepted = true;
        }
        clientsConnectedLock.writeUnlock();
        return accepted;
    }

    public PrintStream getLog() {
        return logStream;
    }

    /**
     * Adds a worker to the current map. This will fail if there the maximum number of connections
     * has been reached.
     *
     * @param name
     * @param worker
     * @return true if this succeeds, false if it fails.
     */
    public void addWorker(String name, Worker worker) {
        nameToWorkerMapLock.writeLock();
        nameToWorkerMap.put(name, worker);
        logStream.println("Client<" + name + "> connected");
        nameToWorkerMapLock.writeUnlock();
    }

    public static void main(String args[]) {
        if (args.length != 2) 
        {
        	System.out.println("Not enough arguments!\n\tjava GameServer <ip address> <port number>");
        	return;
        }
    	GameServer server = new GameServer(100, 5, System.out);
        
        try {
        	InetAddress serverAddr = InetAddress.getByName(args[0]) ;
        	if (serverAddr == null) {
        		System.out.println("Invalid ip address given: " + args[0]);
        		return;
        	}
            server.waitForConnectionsOnPort(Integer.valueOf(args[1]), serverAddr);
        } catch (NumberFormatException e) {
        	System.out.println("Invalid port number: " + args[1]);
        } catch (UnknownHostException e) {
        	System.out.println("Host not found: " + args[0]);
        }
    }

    public void addPlayerToWaitQueue(PlayerLogic player) {
        waitingPlayerQueue.add(player);
    }

    public PlayerLogic getNextWaitingPlayer() {
        return waitingPlayerQueue.get();
    }

    /**
     * Gets a copy of the currently active games. This is mainly for observers.
     *
     * @return a copy of the list of active games
     */
    public List<Game> getGameList() {
        activeGamesLock.readLock();
        List<Game> temp = new ArrayList<Game>(activeGames.values());
        activeGamesLock.readUnlock();
        return temp;
    }

    public Game getGame(String name) {
        activeGamesLock.readLock();
        Game g = activeGames.get(name);
        activeGamesLock.readUnlock();
        return g;
    }
    
	public void removeGame(Game game) {
		activeGamesLock.writeLock();
		if (activeGames.containsKey(game.getName()))
		{
			activeGames.remove(game.getName());
		}
		activeGamesLock.writeUnlock();
	}
	
	public void addGame(Game game) {
		activeGamesLock.writeLock();
		activeGames.put(game.getName(), game);
		activeGamesLock.writeUnlock();
	}

	public int getNumberOfActiveGames() {
		return activeGames.size();
	}

	public boolean isReady() {
		return ready;
	}
}