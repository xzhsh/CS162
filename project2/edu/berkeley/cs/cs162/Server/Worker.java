package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class Worker extends Thread {
    private WorkerSlave slave;
    private boolean done;
    private GameServer server;
    private String clientName;
    private ClientConnection connection;
    private ClientLogic clientLogic;
    
    public Worker(GameServer server, ClientConnection connection) {
        this.server = server;
        done = false;
        clientName = null;
        this.connection = connection;
    }

    /**
     * Run loop for the worker
     */
    public void run() {
        try {
            ClientInfo cInfo = initializeWorker();
            if (cInfo == null) {
                server.getLog().println("Could not get info for client connection.");
                server.decrementConnectionCount();
                connection.close();
                return;
            }

            clientName = cInfo.getName();

            //grab the client logic fo this type of worker.
            clientLogic = ClientLogic.getClientLogicForClientType(this, cInfo.getPlayerType());

            slave = clientLogic.createSlaveThread(connection);
            slave.start();
            server.addWorker(clientName, this);
            server.getLog().println("Client connected! " + cInfo);

            server.addWorker(clientName, this);
            while (!done) {
                //just read messages from input and let the client logic handle stuff.
                Message returnMessage = clientLogic.handleMessage(connection.readFromClient());
                if (returnMessage != null) {
                    //if it is a synchronous message, write the return message to output.
                    connection.sendReplyToClient(returnMessage);
                }
            }
            connection.close();
        }
        catch (SocketTimeoutException e)
        {
            server.getLog().println("Connection timed out.");
            e.printStackTrace();
            closeAndCleanup();
        }
        catch (IOException e) {
            server.getLog().println("Connection closed unexpectedly.");
            e.printStackTrace();
            closeAndCleanup();
        }
    }

    private ClientInfo initializeWorker() {
        if (!connection.receive3WayHandshake(server.getRNG())) {
            //if the 3way handshake fails, just decrement the connection count, close the connections and terminate
            //there should be no resources that the worker has otherwise allocated at this point.
            return null;
        }

        Message returnMessage;
		try {
			returnMessage = connection.readFromClient();
	        if (returnMessage.getMsgType() != MessageProtocol.OP_TYPE_CONNECT) {
	            //unexpected message, close and terminate.
	        	connection.sendReplyToClient(MessageFactory.createErrorUnconnectedMessage());
	            return null;
	        }
	        connection.sendReplyToClient(MessageFactory.createStatusOkMessage());
	
	        return ((ClientMessages.ConnectMessage) returnMessage).getClientInfo();
		} catch (IOException e) {
			System.out.println("Error getting client info:\n" + e);
			return null;
		}
    }

    /**
     * Cleaning up this worker's server resources.
     * <p/>
     * This will remove the worker from the game server and close the connection.
     * <p/>
     * However, this will not clean up the game it is playing or the other player's state.
     * Call closeAndCleanup() for that.
     */
    private void cleanup() {
        server.decrementConnectionCount();
        server.removeWorker(clientName);
        connection.close();

        System.out.println("Worker cleaned up, " + getServer().getNumberOfActiveGames() + " Games active");
    }

    public void handleSendMessageToClient(Message message) {
        slave.handleSendMessage(message);
    }

    public GameServer getServer() {
        return server;
    }

    /**
     * Cleans up all open resources of this worker.
     */
    public void closeAndCleanup() {
        // TODO remove game and wait list.
    	slave.handleTerminate();
    	getLogic().cleanup();
        cleanup();
    }

    public ClientLogic getLogic() {
        return clientLogic;
    }

    public ClientInfo makeClientInfo() {
        // TODO Auto-generated method stub
        return clientLogic.makeClientInfo();
    }

	public WorkerSlave getSlave() {
		// TODO Auto-generated method stub
		return slave;
	}
	
	public String getClientName()
	{
		return clientName;
	}
}