package edu.berkeley.cs.cs162.Synchronization;
/**
 * Stub for the reader writer lock until kunal implements it.
 * 
 * This stub will function as intended, however it is just implementing a 
 * normal lock. This means readers cannot concurrently access the resource.
 * 
 * 
 * @author xshi
 *
 */
public class ReaderWriterLockStub extends ReaderWriterLock{
	private Lock lock;
    public ReaderWriterLockStub() {
        lock = new Lock();
    }
    
    /**
     * Locks the readers lock
     */
    public void readLock() {
        lock.acquire();
    }
    
    /**
     * Unlocks the readers lock
     */
    public void readUnlock() {
    	lock.release();
    }
    
    /**
     * Locks the writers lock
     */
    public void writeLock() {
        lock.acquire();
    }
    
    /**
     * Unlocks the writers lock
     */
    public void writeUnlock() {
        lock.release();
    }
}
