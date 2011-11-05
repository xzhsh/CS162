package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;

@SuppressWarnings("unused")
public class Worker extends Thread {
    private WorkerSlave slave;
    private boolean done;
    private GameServer server;
    private String name;
    private ClientConnection connection;
    private ClientLogic clientLogic;

    public Worker(GameServer server, ClientConnection connection) {
        this.server = server;
        done = false;
        name = null;
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
            } else {
                server.addWorker(name, this);
                server.getLog().println("Client connected! " + cInfo);

                //TODO ...is this in the right place? i get the feeling i'm going to break something...
                if (cInfo.getPlayerType() == MessageProtocol.TYPE_HUMAN || cInfo.getPlayerType() == MessageProtocol.TYPE_MACHINE) {
                    server.addPlayerWorkerToWaitQueue(this);
                }
            }
            name = cInfo.getName();
            server.addWorker(name, this);

            //grab the client logic fo this type of worker.
            clientLogic = ClientLogic.getClientLogicForClientType(this, cInfo.getPlayerType());

            while (!done) {
                //just read messages from input and let the client logic handle stuff.
                Message returnMessage = clientLogic.handleMessage(connection.readFromClient());
                if (returnMessage != null) {
                    //if it is a synchronous message, write the return message to output.
                    connection.sendToClient(returnMessage);
                }
            }
            connection.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            server.getLog().println("Connection closed unexpectedly.");
            cleanup();
        }
    }

    private ClientInfo initializeWorker() throws IOException {
        if (!connection.receive3WayHandshake(server.getRNG())) {
            //if the 3way handshake fails, just decrement the connection count, close the connections and terminate
            //there should be no resources that the worker has otherwise allocated at this point.
            return null;
        }
        slave = new WorkerSlave(connection);
        slave.start();

        Message returnMessage = connection.readFromClient();

        if (returnMessage.getMsgType() != MessageProtocol.OP_TYPE_CONNECT) {
            //unexpected message, close and terminate.
            return null;
        }
        connection.sendReplyToClient(MessageFactory.createStatusOkMessage());
        //TODO Extract clientinfo from connectMessage
        return ((ClientMessages.ConnectMessage) returnMessage).getClientInfo();
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
        server.removeWorker(name);
        try {
            connection.close();
        } catch (IOException e1) {
            // Connection is already closed, just continue execution
        }
    }

    public Message handleSendMessageToClient(Message message) {
        if (message.isSynchronous()) {
            return slave.handleSendMessageSync(message);
        } else {
            slave.handleSendMessageAsync(message);
            return null;
        }
    }

    public GameServer getServer() {
        return server;
    }

    public boolean handleRegisterAsWaiting() {
        server.addPlayerWorkerToWaitQueue(this);
        return connection.isValid();
    }

    /**
     * Cleans up all open resources of this worker.
     */
    public void closeAndCleanup() {
        // TODO remove game and wait list.
        cleanup();
    }

    public ClientLogic getLogic() {
        // TODO Auto-generated method stub
        return clientLogic;
    }

    public void startGame(Game game) {
        getLogic().startGame(game);
    }

    public ClientInfo makeClientInfo() {
        // TODO Auto-generated method stub
        return clientLogic.makeClientInfo();
    }
}