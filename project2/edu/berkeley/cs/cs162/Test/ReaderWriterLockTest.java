package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;

import edu.berkeley.cs.cs162.Synchronization.Lock;
import edu.berkeley.cs.cs162.Synchronization.ReaderWriterLock;
import org.junit.Test;

import java.util.ArrayList;

public class ReaderWriterLockTest {

    @Test // Basic test to demonstrate that multiple readers can acquire the lock
    public void testMultipleReaders() throws InterruptedException {

        SharedResource<Integer> count = new SharedResource<Integer>(0);
        final Lock mutex = new Lock();
        final ReaderWriterLock lock = new ReaderWriterLock();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        class ReaderThread extends Thread {

            private SharedResource<Integer> sharedInteger;

            public ReaderThread(SharedResource<Integer> i){
                sharedInteger = i;
            }

            public void run() {
                lock.readLock();
                mutex.acquire();
                sharedInteger.setResource(sharedInteger.getResource() + 1);
                mutex.release();
            }
        }

        for(int i = 0; i < 100; i++){
            Thread reader = new ReaderThread(count);
            threads.add(reader);
            reader.start();
        }

        for(Thread thread : threads){
            thread.join();
        }

        // All readers were able to successfully acquire the lock
        assertEquals(100, count.getResource().intValue());
    }

    @Test // Basic test to demonstrate only one writer at a time may acquire the lock
    public void testNoMultipleWriters() throws InterruptedException {
        SharedResource<Integer> count = new SharedResource<Integer>(0);
        final ReaderWriterLock lock = new ReaderWriterLock();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        class WriterThread extends Thread {

            private SharedResource<Integer> sharedInteger;

            public WriterThread(SharedResource<Integer> i){
                sharedInteger = i;
            }

            public void run() {
                lock.writeLock();
                sharedInteger.setResource(sharedInteger.getResource() + 1);
            }
        }

        for(int i = 0; i < 10; i++){
            Thread writer = new WriterThread(count);
            threads.add(writer);
            writer.start();
        }

        for(Thread thread : threads){
            thread.join(100);
        }

        // Only one writer successfully acquired the lock.
        assertEquals(1, count.getResource().intValue());
    }

    @Test // Tests that readers cannot acquire a lock if a writer has it
    public void testSingleWriter() throws InterruptedException {

        final SharedResource<String> message = new SharedResource<String>("");
        final SharedResource<Integer> readerCount = new SharedResource<Integer>(0);
        final ReaderWriterLock lock = new ReaderWriterLock();
        final Lock mutex = new Lock();
        ArrayList<Thread> threads = new ArrayList<Thread>();

        int testreaders = 5;

        // Writes to the shared resources, waits for a bit, and then unlocks.
        class WriterThread extends Thread {

            public WriterThread(){
            }

            public void run() {
                lock.writeLock();
                message.setResource("Yo dawg I heard you like ReaderWriterLocks");

                // Go to sleep to let the readers try and acquire the lock
                try { Thread.sleep(1000L); }
                catch (InterruptedException e) { /* Resume execution... */ }

                lock.writeUnlock();
            }
        }

        class ReaderThread extends Thread {

            public ReaderThread(){
            }

            public void run() {
                lock.readLock();
                mutex.acquire();
                readerCount.setResource(readerCount.getResource() + 1);
                mutex.release();

                assertEquals("Yo dawg I heard you like ReaderWriterLocks", message.getResource());

                lock.readUnlock();
            }
        }

        Thread writer = new WriterThread();
        writer.start();

        for(int i = 0; i < testreaders; i++){
            Thread reader = new ReaderThread();
            threads.add(reader);
            reader.start();
        }

        for(Thread reader : threads)
            reader.join(10);

        // Make sure no readers have the lock.
        assertEquals(0, readerCount.getResource().intValue());

        for(Thread reader: threads)
            reader.join();

        // All readers should have completed.
        assertEquals(testreaders, readerCount.getResource().intValue());

    }
}
