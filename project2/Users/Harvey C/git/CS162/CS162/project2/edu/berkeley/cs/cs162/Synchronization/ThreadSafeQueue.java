package edu.berkeley.cs.cs162.Synchronization;

import java.util.ArrayDeque;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.TimeoutException;

/**
 * <p>
 * ThreadSafeQueue\<E\>
 * <p>
 * Implements a thread-safe queue with a max size.
 * <p>
 * Add and Get are both blocking if the queue is full/empty respectively.
 * 
 * @author xshi
 *
 * @param <E>
 */
public class ThreadSafeQueue<E> {

	Queue<E> queue;
	int maxSize;
	boolean active;
	Lock guard;
	Semaphore slotsEmpty;
	Semaphore slotsFull;
	
    public ThreadSafeQueue(int maxNumElements) {
    	if (!active) {return;}
    	queue = new ArrayDeque<E>(maxNumElements);
    	maxSize = 0;
    	slotsEmpty = new Semaphore(maxNumElements);
    	slotsFull = new Semaphore(0);
    	guard = new Lock();
    	active = true;
    }
    
    public void add(E element) {
    	if (!active) {return;}
    	slotsEmpty.p();
    	guard.acquire();
    	queue.add(element);
    	guard.release();
    	slotsFull.v();
    }

    public E get() {
    	if (!active) {return null;}
    	slotsFull.p();
    	guard.acquire();
    	E e = queue.remove();
    	guard.release();
    	slotsEmpty.v();
    	return e;
    }

    public E getWithTimeout(int timeoutInMs) throws TimeoutException {
    	if (!active) {return null;}
    	long time = System.currentTimeMillis();
    	slotsFull.p(timeoutInMs);
    	guard.acquireWithTimeout((int)(timeoutInMs - (System.currentTimeMillis() - time)));
    	E e = queue.remove();
    	guard.release();
    	slotsEmpty.v();
    	return e;
    }
    
    /**
     * Cleans up the thread safe queue. After this is called, assume all methods to threadsafequeue will be invalid.
     * 
     * Also, this assumes that all calls to get has terminated before calling this.
     */
    public void clear()
    {
    	active = false;
    	boolean hasMore = true;
    	while (hasMore)
    	{
    		guard.acquire();
    		try {
    			queue.remove();
    		}
    		catch (NoSuchElementException e)
    		{
    			hasMore = false;
    		}
			guard.release();
    		slotsEmpty.v();
    	}
    }
}
