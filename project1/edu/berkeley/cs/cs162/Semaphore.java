package edu.berkeley.cs.cs162;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

public class Semaphore {
	/**
	 * "indefinite sleep" time
	 * 
	 * Since this number is finite, it implies that Locks may randomly 
	 * unlock themselves after 292 million years.
	 */
	final static long MAX_SLEEP = Long.MAX_VALUE;
	
	/**
	 * A spin lock that guards the p and v methods
	 */
    SpinLock guard;
    
    /**
     * A queue of sleepingThreads
     */
    Queue<Thread> sleepingThreads;
    
    /**
     * Integer count for this semaphore.
     */
    int count;
    
    /**
     * Initializes this Semaphore
     */
    public Semaphore(int initialCount) {
		count = initialCount;
    	guard = new SpinLock();
    	sleepingThreads = new ArrayDeque<Thread>();
    }
    
    public void p() {
    	guard.acquire();
    	if (count == 0) {
    		sleepingThreads.add(Thread.currentThread());
    		guard.release();

            try {
    			Thread.sleep(MAX_SLEEP);
    		}

    		catch (InterruptedException e) {
    			//resume execution when interrupted.
    		}
    	}

    	else {
    		count--;
    		guard.release();
    	}
    }

    public void p(int timeoutInMs) throws TimeoutException {
    	guard.acquireWithTimeout(timeoutInMs);
    	long startTimeInMs = System.currentTimeMillis();

        if (count == 0) {
    		sleepingThreads.add(Thread.currentThread());
    		guard.release();

    		try {
    			//this block technically should be atomic, but... no primitives we can use :(
    			long elapsedTimeInMs = System.currentTimeMillis() - startTimeInMs;
    			long timeLeftInMs = timeoutInMs - elapsedTimeInMs;
    			if (timeLeftInMs > 0)
    				Thread.sleep(timeLeftInMs);
    		}

    		catch (InterruptedException e) {
    			//resume execution when interrupted.
    			return;
    		}

    		//if sleep finishes, remove the thread from sleeping threads and throw a timeout exception
    		guard.acquire();
    		sleepingThreads.remove(Thread.currentThread());
    		guard.release();
    		throw new TimeoutException("Lock Acquire timed out");
    	}

    	else {
    		count--;
    		guard.release();
    	}
    }
    
    public void v() {
    	guard.acquire();

    	if (sleepingThreads.isEmpty()) {
    		count++;
    		guard.release();
    	}

    	else {
    		Thread next = sleepingThreads.remove();
    		guard.release();
    		next.interrupt();
    	}
    }
}

