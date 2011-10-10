package edu.berkeley.cs.cs162;

import java.util.concurrent.TimeoutException;

/**
 * Lock class that enforces mutural exclusion with minimal busy waiting
 * 
 * Note that this is implemented with as a Binary Semaphore
 * 
 * @author xshi
 */

public class Lock extends Semaphore implements ILock{
	/**
	 * Constructs this lock.
	 */
	public Lock() {
		super(1);
	}

	/**
	 * Tries to acquire this lock, blocking indefinitely until it is free.
	 */
	public void acquire() {
		p();
	}

	/**
	 * Tries to acquire this lock, blocking until either a TimeoutException is thrown
	 * or the lock is released.
	 */
	public void acquireWithTimeout(int timeoutInMs) throws TimeoutException {
		p(timeoutInMs);
	}

	/**
	 * Releases this lock so other threads may use it. Note that this should ONLY be called 
	 * by the thread that acquired this lock!
	 */
	public void release() {
		v();
	}
}

