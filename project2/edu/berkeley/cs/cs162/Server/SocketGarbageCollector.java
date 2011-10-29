package edu.berkeley.cs.cs162.Server;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;

public class SocketGarbageCollector implements Runnable {

	private Map<Integer, SocketWithTimeStamp> socketMap;
	private ReaderWriterLock lock;
	private long timeoutInNs;
	private long sleepTime;
	public SocketGarbageCollector(
			Map<Integer, SocketWithTimeStamp> waitingSocketMap,
			ReaderWriterLock waitingSocketMapLock, long timeoutInMs, long sleepTimeInMs) {
		socketMap = waitingSocketMap;
		lock = waitingSocketMapLock;
		this.timeoutInNs = timeoutInMs * 1000000L;
		this.sleepTime = sleepTimeInMs;
	}

	public void run() {

		List<Integer> timedOutSockets = new ArrayList<Integer>();
		while(true)
		{
			timedOutSockets.clear();
			try {
				lock.readLock();
				for (Integer id : socketMap.keySet())
				{
					if(socketMap.get(id).timedOut(timeoutInNs))
					{
						timedOutSockets.add(id);
					}
				}
				lock.readUnlock();
				lock.writeLock();
				for (Integer id : timedOutSockets)
				{
					socketMap.remove(id);
				}
				lock.writeUnlock();
				
				//some small sleep value so that the garbage collector isn't taking up all the cpu time
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				assert false : "This thread should never be interrupted.";
			}
		}
	}

}
