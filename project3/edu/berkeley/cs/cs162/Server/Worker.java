package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Server.AuthenticationManager.ServerAuthenticationException;
import edu.berkeley.cs.cs162.Writable.*;
import edu.berkeley.cs.cs162.Writable.ClientMessages.RegisterMessage;

import java.io.IOException;
import java.net.SocketTimeoutException;

public class Worker extends Thread {
    private boolean done;
    private GameServer server;
    private String clientName;
    private ClientConnection connection;
    private ClientLogic clientLogic;
	private int clientID;
    
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
            clientLogic = ClientLogic.getClientLogicForClientType(getServer(), getClientName(), cInfo.getPlayerType(), connection);
            clientLogic.setID(clientID);
            server.addWorker(clientName, this);
            server.getLog().println("Client connected! " + cInfo);
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
            closeAndCleanup();
        }
        catch (IOException e) {
            server.getLog().println("Connection closed unexpectedly." + e.getMessage());
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
			while (true) {
				returnMessage = connection.readFromClient();
				if (returnMessage.getMsgType() == MessageProtocol.OP_TYPE_CONNECT) 
				{
					//connect
					ClientMessages.ConnectMessage connectMsg = (ClientMessages.ConnectMessage) returnMessage;
					try {
						clientID = getServer().getAuthenticationManager().authenticateClient(
							connectMsg.getClientInfo(), connectMsg.getPasswordHash());
						//authenticated, connected.
						connection.sendReplyToClient(MessageFactory.createStatusOkMessage());
				        return connectMsg.getClientInfo();
					}
					catch (ServerAuthenticationException e) {
						//authentication failed. Can retry.
						connection.sendReplyToClient(MessageFactory.createErrorBadAuthMessage());
					}
				}
				else if (returnMessage.getMsgType() == MessageProtocol.OP_TYPE_REGISTER)
				{
					//register
					ClientMessages.RegisterMessage regMsg = (RegisterMessage) returnMessage;
					if (getServer().getAuthenticationManager().registerClient(
							regMsg.getClientInfo(), regMsg.getPasswordHash()))
					{
						// success
						connection.sendReplyToClient(MessageFactory.createStatusOkMessage());
					}
					else 
					{
						// failed.
						connection.sendReplyToClient(MessageFactory.createErrorRejectedMessage());
					}
				}
				else if (returnMessage.getMsgType() == MessageProtocol.OP_TYPE_DISCONNECT)
				{
					//disconnect
					return null;
				}
				else {
		            //unexpected message, close and terminate.
		        	connection.sendReplyToClient(MessageFactory.createErrorUnconnectedMessage());
		        	return null;
		        }
			}
		} catch (IOException e) {
			getServer().getLog().println("Error getting client info:\n" + e);
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

        getServer().getLog().println("Worker cleaned up, " + getServer().getNumberOfActiveGames() + " Games active");
    }

    public GameServer getServer() {
        return server;
    }

    /**
     * Cleans up all open resources of this worker.
     */
    public void closeAndCleanup() {
        // TODO remove game and wait list.
    	done = true;
    	getLogic().cleanup();
        cleanup();
    }

    public ClientLogic getLogic() {
        return clientLogic;
    }

    public ClientInfo makeClientInfo() {
        return clientLogic.makeClientInfo();
    }
	
	public String getClientName()
	{
		return clientName;
	}
}