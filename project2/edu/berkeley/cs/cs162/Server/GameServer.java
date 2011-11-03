package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLockStub;
import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.Message;

public class GameServer {
	private static final int GLOBAL_TIMEOUT_IN_MS = 3000;
	private static final int WAITING_CONNECTION_BUFFER_SIZE = 10;
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
	 * Constructor for gameServer
	 * @param clientLimit how many max clients can be connected
	 * @param handshakeThreadPoolSize How many {@link ConnectionWorker} threads there will be.
	 */
	
	public GameServer(int clientLimit, int handshakeThreadPoolSize) 
	{
		this.clientLimit = clientLimit;
		rng = new Random();
		waitingSocketMap = new HashMap<Integer, SocketWithTimeStamp>();

		connectionQueue = new ThreadSafeQueue<Socket>(WAITING_CONNECTION_BUFFER_SIZE);
		nameToWorkerMap = new HashMap<String, Worker>();
		//TODO change these to ReaderWriterLock when it's implemented.
		waitingSocketMapLock = new ReaderWriterLockStub();
		nameToWorkerMapLock = new ReaderWriterLockStub();
		clientsConnectedLock = new ReaderWriterLockStub();
		
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
		SocketGarbageCollector collector = new SocketGarbageCollector(waitingSocketMap, waitingSocketMapLock, GLOBAL_TIMEOUT_IN_MS, 10);
		Thread collectorThread = new Thread(collector);
		collectorThread.start();
	}
	
	/**
	 * Starts waiting on a port to accept connections. This method should never return.
	 * 
	 * @param portNumber
	 */
	public void waitForConnectionsOnPort(int portNumber, InetAddress localIP) {
		try {
			ServerSocket server = new ServerSocket(portNumber, 50, localIP);
			
			while (true) {
				Socket incomingConnection = server.accept();
				incomingConnection.setSoTimeout(GLOBAL_TIMEOUT_IN_MS);
				connectionQueue.add(incomingConnection);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * @return true if no more clients can connect to this server.
	 */
	public boolean maxNumberOfClientsReached()
	{
		boolean limitReached;
		clientsConnectedLock.readLock();
		limitReached = clientsConnected < clientLimit;
		clientsConnectedLock.readUnlock();
		return limitReached;
	}
	
	/**
	 * Sends a message to the named client through a worker.
	 * 
	 * @param name
	 * @param message
	 * @return a return message if the message is synchronous.
	 */
	public Message sendMessageToClient(String name, Message message) {
		Worker worker;
		nameToWorkerMapLock.readLock();
		worker = nameToWorkerMap.get(name);
		nameToWorkerMapLock.readUnlock();
		return worker.handleSendMessageToClient(message);
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
	 * @param sYN_ID
	 * @param connection
	 */
	public void handleSYN(int SYN_ID, Socket connection) {
		SocketWithTimeStamp otherConnection = null;
		System.out.println("Connection initiated with SYN_ID: " + SYN_ID);
		//Read whether the syn id exists already
		waitingSocketMapLock.readLock();
		if(waitingSocketMap.containsKey(SYN_ID))
		{
			otherConnection = waitingSocketMap.get(SYN_ID);
		}
		waitingSocketMapLock.readUnlock();
		////////////////////////////////////////
		
		if(otherConnection != null)
		{
			System.out.println("Pair found!");
			waitingSocketMapLock.writeLock();
			waitingSocketMap.remove(SYN_ID);
			waitingSocketMapLock.writeUnlock();
			initializeWorkerForConnection(connection, otherConnection.getConnection(), SYN_ID);
		}
		else
		{

			waitingSocketMapLock.writeLock();
			waitingSocketMap.put(SYN_ID, new SocketWithTimeStamp(connection));
			waitingSocketMapLock.writeUnlock();
		}
	}
	
	private void initializeWorkerForConnection(Socket connection1,
			Socket connection2, int SYN_ID) {
		incrementConnectionCount();
		System.out.println("Initialized a worker with syn id = " + SYN_ID);
		Worker worker = new Worker(this, new ClientConnection(connection1, connection2, SYN_ID));
		worker.start();
	}

	/**
	 * Cleans up the resources held by this worker.
	 * @param worker
	 */
	protected void removeWorker(String name) {
		//TODO implement stuff for game.
		nameToWorkerMapLock.writeLock();
		nameToWorkerMap.remove(name);
		nameToWorkerMapLock.writeUnlock();
	}

	protected void decrementConnectionCount() {
		clientsConnectedLock.writeLock();
		clientsConnected--;
		clientsConnectedLock.writeUnlock();
	}

	protected void incrementConnectionCount() {
		clientsConnectedLock.writeLock();
		clientsConnected++;
		clientsConnectedLock.writeUnlock();
	}

	/**
	 * Adds a worker to the current map. This will fail if there the maximum number of connections 
	 * has been reached.
	 * @param name
	 * @param worker
	 * @return true if this succeeds, false if it fails.
	 */
	public void addWorker(String name, Worker worker) {
		nameToWorkerMapLock.writeLock();
		nameToWorkerMap.put(name, worker);
		nameToWorkerMapLock.writeUnlock();
	}
	
	public static void main(String args[])
	{
		GameServer server = new GameServer(100, 5);
		try {
			server.waitForConnectionsOnPort(Integer.valueOf(args[1]), InetAddress.getByName(args[0]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}