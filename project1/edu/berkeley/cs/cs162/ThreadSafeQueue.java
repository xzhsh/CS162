package edu.berkeley.cs.cs162;

import java.util.ArrayDeque;
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
	Lock guard;
	Semaphore slotsEmpty;
	Semaphore slotsFull;
	
    public ThreadSafeQueue(int maxNumElements) {
    	queue = new ArrayDeque<E>(maxNumElements);
    	maxSize = 0;
    	slotsEmpty = new Semaphore(maxNumElements);
    	slotsFull = new Semaphore(0);
    	guard = new Lock();
    }
    
    public void add(E element) {
    	slotsEmpty.p();
    	guard.acquire();
    	queue.add(element);
    	guard.release();
    	slotsFull.v();
    }

    public E get() {
    	slotsFull.p();
    	guard.acquire();
    	E e = queue.remove();
    	guard.release();
    	slotsEmpty.v();

    	return e;
    }

    public E getWithTimeout(int timeoutInMs) throws TimeoutException {
    	long time = System.currentTimeMillis();
    	slotsFull.p(timeoutInMs);
    	guard.acquireWithTimeout((int)(timeoutInMs - (System.currentTimeMillis() - time)));
    	E e = queue.remove();
    	guard.release();
    	slotsEmpty.v();
    	return e;
    }
}
