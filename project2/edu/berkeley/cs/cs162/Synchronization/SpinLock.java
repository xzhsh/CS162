package edu.berkeley.cs.cs162.Synchronization;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of a simple spin lock in java using an {@link AtomicBoolean}
 * @author xshi
 */
public class SpinLock implements ILock {
	
    private AtomicBoolean lock;
    
    /**
     * Constructor. Creates a new SpinLock
     */
    public SpinLock() {
    	lock = new AtomicBoolean(false);
    }
    
    /**
     * Tries to acquires this lock.
     * 
     * This method will spin wait (loop infinitely) until the lock is freed
     */
    public void acquire() {
    	while (lock.getAndSet(true)) {
            Thread.yield();
        }
    }
    
    /**
     * Tries to acquires this lock within the timeout in milliseconds.
     * 
     * This method will spin wait (loop infinitely) until the lock is freed
     */
    public void acquireWithTimeout(int timeoutInMs) throws TimeoutException{
    	long startTime = System.currentTimeMillis();

        while (lock.getAndSet(true)) {
            Thread.yield();
    		long elapsedTime = System.currentTimeMillis() - startTime;

            if (elapsedTime > timeoutInMs) {
    			throw new TimeoutException("SpinLock acquire timed out");
    		}
    	}
    }

    /**
     * Releases this lock. This method should only be called if the thread currently has acquired the lock.
     */
    public void release() {
    	lock.set(false);
    }
}

