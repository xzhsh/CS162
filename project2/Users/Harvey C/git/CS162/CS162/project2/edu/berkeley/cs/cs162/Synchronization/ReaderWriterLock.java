package edu.berkeley.cs.cs162.Synchronization;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Synchronization class that provides classic
 * reader-writer locking behavior. This lock should
 * allow multiple readers, and prioritize waiting
 * writers over waiting readers.
 * 
 * 
 */
public class ReaderWriterLock {

    Lock guard;
    int value;
    Queue<Thread> waitingReaders;
    Queue<Thread> waitingWriters;
    
    public ReaderWriterLock() {
        guard = new Lock();
        value = 0;
        waitingReaders = new ArrayDeque<Thread>();
        waitingWriters = new ArrayDeque<Thread>();
    }
    
    /**
     * Locks the readers lock
     */
    public void readLock() {
        guard.acquire();
        if((value >= 0) && waitingWriters.isEmpty()){
            value++;
            guard.release();
        }
        else {
            waitingReaders.add(Thread.currentThread());
            guard.release();
            try {Thread.sleep(Semaphore.MAX_SLEEP);}
            catch(InterruptedException i){ /*Continue execution...*/}
        }
    }
    
    /**
     * Unlocks the readers lock
     */
    public void readUnlock() {
        guard.acquire();
        if((value == 1) && !waitingWriters.isEmpty()){
            value = -1; // Directly pass the lock to the waiting Writer
            Thread next = waitingWriters.remove();
            guard.release();
            next.interrupt();
        }
        else{
            value--;
            assert value >= 0;
            guard.release();
        }
        
    }
    
    /**
     * Locks the writers lock
     */
    public void writeLock() {
        guard.acquire();
        if(value == 0){
            value--;
            guard.release();
        }
        else{
            waitingWriters.add(Thread.currentThread());
            guard.release();
            try { Thread.sleep(Semaphore.MAX_SLEEP); }
            catch(InterruptedException i) { /* Continue Execution... */ }
        }
    }
    
    /**
     * Unlocks the writers lock
     */
    public void writeUnlock() {
        guard.acquire();
        if(!waitingWriters.isEmpty()){
            Thread next = waitingWriters.remove();
            guard.release();
            next.interrupt();
        }
        else if(!waitingReaders.isEmpty()){
            value = waitingReaders.size(); // Directly pass the lock to all waiting readers
            for(Thread reader : waitingReaders){
                reader.interrupt();
            }
            waitingReaders.clear();
            guard.release();
        }
        else {
            value++;
            assert value == 0;
            guard.release();
        }
        
    }
}
