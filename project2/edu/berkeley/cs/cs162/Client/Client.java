package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;

import java.io.IOException;

/**
 * The Client interface is implemented by Players and Observers.
 * It defines a number of shared methods.
 */
public interface Client {
    /**
     * Sets the name of the Client
     *
     * @param name
     */
    public abstract void setName(String name);

    /**
     * Gets the name of the Client
     *
     * @return
     */
    public abstract String getName();

    /**
     * Sets the type of the Client (e.g. MessageProtocol.TYPE_HUMAN)
     *
     * @param type
     */
    public abstract void setType(byte type);

    /**
     * Gets the type of the Client (e.g. MessageProtocol.TYPE_MACHINE)
     *
     * @return type
     */
    public abstract byte getType();

    /**
     * Gets a ClientInfo representing the client
     *
     * @return clientInfo
     */
    public abstract ClientInfo getClientInfo();

    /**
     * Handles messages that the client receives
     */
    public abstract void handleMessage(Message m) throws IOException;
}
