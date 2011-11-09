package edu.berkeley.cs.cs162.Server;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketGarbageCollector implements Runnable {

    private Map<Integer, SocketWithTimeStamp> socketMap;
    private ReaderWriterLock lock;
    private long timeoutInNs;
    private long sleepTime;
	@SuppressWarnings("unused")
	private PrintStream log;

    public SocketGarbageCollector(
            Map<Integer, SocketWithTimeStamp> waitingSocketMap,
            ReaderWriterLock waitingSocketMapLock, long timeoutInMs, long sleepTimeInMs, PrintStream log) {
        socketMap = waitingSocketMap;
        lock = waitingSocketMapLock;
        this.timeoutInNs = timeoutInMs * 1000000L;
        this.sleepTime = sleepTimeInMs;
        this.log = log;
    }

    public void run() {
        List<Integer> timedOutSockets = new ArrayList<Integer>();
        while (true) {
            timedOutSockets.clear();
            try {
                lock.readLock();
                for (Integer id : socketMap.keySet()) {
                    if (socketMap.get(id).timedOut(timeoutInNs)) {
                        timedOutSockets.add(id);
                    }
                }
                lock.readUnlock();
                lock.writeLock();
                List<Socket> closingSockets = new ArrayList<Socket>();
                for (Integer id : timedOutSockets) {
                    SocketWithTimeStamp s = socketMap.remove(id);
                    if (s != null) {
                        closingSockets.add(s.getConnection());
                    }
                }
                lock.writeUnlock();

                for (Socket s : closingSockets)
                {
                	try {
						s.close();
					} catch (IOException e) {
						//if already closed, just ignore it.
					}
                }
                //some small sleep value so that the garbage collector isn't taking up all the cpu time
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                assert false : "This thread should never be interrupted.";
            }
        }
    }

}
