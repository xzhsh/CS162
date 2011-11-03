package edu.berkeley.cs.cs162.Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@link ConnectionWorker} is a runnable that will be utilized by the {@link GameServer}
 * to offload the 3 way handshake after a client has connected.
 * 
 * These workers threads should be created in a pool. The {@link GameServer}
 * will deposit open sockets in a queue, and {@link ConnectionWorker} will 
 * dequeue them and attempt to accept the SYN_ID
 * 
 * When the SYN_ID is received, {@ ConnectionWorker} will store the connection if the id
 * is unique, or create a WorkerThread to handle the connection if there is already another
 * connection with the same id.
 * @author xshi
 *
 */
public class ConnectionWorker implements Runnable {
	private boolean done;
	private GameServer server;
	public ConnectionWorker(GameServer server)
	{
		this.server = server;
		done = false;
	}
	public void run()
	{
		while(!done) {
			handleConnection(server.getNextConnection());
		}
	}
	
	private void handleConnection(Socket connection) {
		try {
			DataInputStream isStream = new DataInputStream(connection.getInputStream());
			//the first integer should be the syn id.
			int SYN_ID = isStream.readInt();
			server.handleSYN(SYN_ID, connection);
		} catch (IOException e) {
			e.printStackTrace();
			try {
				connection.close();
			} catch (IOException e1) {
				// connection doesn't even exist any more. just terminate.
			}
		}
	}
}
