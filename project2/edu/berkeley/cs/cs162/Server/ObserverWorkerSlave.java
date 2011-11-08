package edu.berkeley.cs.cs162.Server;

/**
 * Worker slave in charge of observers.
 * @author xshi
 *
 */
public class ObserverWorkerSlave extends WorkerSlave {
	public ObserverWorkerSlave(ClientConnection connection, Worker master) {
		super(connection, master);
	}
}
