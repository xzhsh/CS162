package edu.berkeley.cs.cs162.Server;

import java.net.Socket;

class SocketWithTimeStamp {
    private long timeStamp;
    private Socket connection;

    SocketWithTimeStamp(Socket connection) {
        this.connection = connection;
        timeStamp = System.nanoTime();
    }

    public Socket getConnection() {
        return connection;
    }

    /**
     * @param timeoutInNs
     * @return true if this connection has already timed out.
     */
    public boolean timedOut(long timeoutInNs) {
    	long elapsed = System.nanoTime() - timeStamp;
        return (elapsed) > timeoutInNs;
    }
}