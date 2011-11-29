package edu.berkeley.cs.cs162.Synchronization;

import java.util.concurrent.TimeoutException;

/**
 * Interface ILock
 * 
 * An interface for Mutex locks
 * @author xshi
 */
public interface LockInterface {
	public void acquire();
	public void acquireWithTimeout(int timeoutInMs) throws TimeoutException;
	public void release();
}
