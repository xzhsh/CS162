package edu.berkeley.cs.cs162.Server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Random;

import edu.berkeley.cs.cs162.Synchronization.ThreadSafeQueue;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.ClientMessages;
import edu.berkeley.cs.cs162.Writable.CompositeMessage;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
@SuppressWarnings("unused")
public class Worker extends Thread {
	private WorkerSlave slave;
	private Socket returnConnection;
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
	public void run()
	{	
		try {
			ClientInfo cInfo = initializeWorker();
			if (cInfo == null)
			{
				System.out.println("Could not get info for client connection.");
				server.decrementConnectionCount();
				connection.close();
				return;
			} else 
			{
				System.out.println("Client connected! " + cInfo);
			}
			name = cInfo.getName();
			server.addWorker(name, this);
			clientLogic = ClientLogic.getClientLogicForClientType(this, cInfo.getPlayerType());
			
			while (!done)
			{
				//just read messages from input and let the client logic handle stuff.
				Message returnMessage = clientLogic.handleMessage(connection.readFromClient());
				if (returnMessage != null)
				{
					//if it is a synchronous message, write the return message to output.
					connection.sendToClient(returnMessage);
				}
			}
			
			assert returnConnection != null;
			returnConnection.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block

			System.out.println("Connection closed unexpectedly.");
			server.decrementConnectionCount();
			try {
				connection.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	private ClientInfo initializeWorker() throws IOException {
		if (!connection.receive3WayHandshake(server.getRNG()))
		{
			//if the 3way handshake fails, just decrement the connection count, close the connections and terminate
			//there should be no resources that the worker has otherwise allocated at this point.
			return null;
		}
		returnConnection = connection.getC2S();
		slave = new WorkerSlave(connection.getS2C());
		slave.start();
		
		ClientMessages.ConnectMessage returnMessage = new ClientMessages.ConnectMessage();
		connection.readFromClient(returnMessage);
		
		if(returnMessage.getMsgType() != MessageProtocol.OP_TYPE_CONNECT)
		{
			//unexpected message, close and terminate.
			return null;
		}
		
		
		connection.sendToClient(MessageFactory.createStatusOkMessage());
		//TODO Extract clientinfo from connectMessage
		return returnMessage.getClientInfo();
	}

	public Message handleSendMessageToClient(Message message) {
		if (message.isSynchronous()) {
			return slave.handleSendMessageSync(message);
		}
		else  
		{
			slave.handleSendMessageAsync(message);
			return null;
		}
	}
}