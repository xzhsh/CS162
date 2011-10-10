package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import edu.berkeley.cs.cs162.Lock;

/**
 * @author xshi
 *
 */
public class LockTest {
	
	
	/**
	 * Simple test case that will create a lock, acquire it, and release it.
	 * 
	 * This test will timeout and fail in 5s if it does not complete.
	 */
	@Test(timeout=5000)
	public void SanityCheckTest() {
		Lock lock = new Lock();
		lock.acquire();
		lock.release();
	}
	
	/**
	 * @throws InterruptedException 
	 * 
	 */
	@Test(timeout=15000)
	public void TimeoutTest() throws InterruptedException {
		final Lock lock = new Lock();
		SharedResource<Boolean> acquiredFlag = new SharedResource<Boolean>(false);
		class TestThread extends Thread
		{
			private SharedResource<Boolean> buffer;
			long delay;
			public TestThread(SharedResource<Boolean> buffer, long delay)
			{
				this.buffer = buffer;
				this.delay = delay;
			}
			
			public void run()
			{
				try {
					lock.acquireWithTimeout((int)delay);
					buffer.setResource(false);
					lock.release();
				} catch (TimeoutException e) {
					buffer.setResource(true);
				}
			}
		}
		System.out.println("Thread " + Thread.currentThread().getId() + " is acquiring lock");
		long startTime = System.currentTimeMillis();
		lock.acquire();
		long elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Thread " + Thread.currentThread().getId() + " has acquired the lock in " + elapsedTime + "ms");
		
		Thread test1 = (new TestThread(acquiredFlag,5000));
		test1.start();
		System.out.println("Thread " + Thread.currentThread().getId() + " is sleeping for 4000ms");
		startTime = System.currentTimeMillis();
		Thread.sleep(4000);
		elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Thread " + Thread.currentThread().getId() + " has slept for " + elapsedTime + "ms");
		assertFalse(acquiredFlag.getResource());
		System.out.println("Flag = " + acquiredFlag.getResource());
		
		System.out.println("Thread " + Thread.currentThread().getId() + " is sleeping for 1000ms");
		startTime = System.currentTimeMillis();
		Thread.sleep(1000);
		elapsedTime = System.currentTimeMillis() - startTime;
		System.out.println("Thread " + Thread.currentThread().getId() + " has slept for " + elapsedTime + "ms");
		assertTrue(acquiredFlag.getResource());
		System.out.println("Flag = " + acquiredFlag.getResource());
		test1.join();
		Thread test2 = (new TestThread(acquiredFlag,1000));
		test2.start();
		lock.release();
		test2.join(1000);
		assertFalse(acquiredFlag.getResource());
	}
	
	
	/**
	 * Tests Lock functionality with threads.
	 * @throws InterruptedException 
	 */
	@Test(timeout=10000)
	public void HeuristicThreadLockTest() throws InterruptedException
	{
		final Lock lock = new Lock();
		/**
		 * A shared resource. Basically a mutable wrapping around an value
		 * @author xshi
		 *
		 * @param <E>
		 */
		
		
		/**
		 * A thread that will increment a shared integer by 1000
		 * @author xshi
		 */
		class ThreadA extends Thread
		{
			private SharedResource<Integer> buffer;
			
			public ThreadA(SharedResource<Integer> buffer)
			{
				this.buffer = buffer;
			}
			
			public void run()
			{
				for(int i = 0; i < 1000; i++)
				{
					lock.acquire();
					buffer.setResource(buffer.getResource() + 1);
					lock.release();
				}
			}
		}
		
		SharedResource<Integer> buffer = new SharedResource<Integer>(0);
		Thread a = new ThreadA(buffer);
		Thread b = new ThreadA(buffer);
		a.start();
		b.start();
		a.join();
		b.join();
		System.out.println(buffer.getResource());
		//Since the system is locked, the value of the buffer should be 2000
		assertEquals(buffer.getResource().intValue(),2000);
	}
	
	/**
	 * Tests Lock functionality with many threads.
	 * @throws InterruptedException 
	 */
	@Test(timeout=10000)
	public void ManyThreadLockTest() throws InterruptedException
	{
		final Lock lock = new Lock();
		/**
		 * A shared resource. Basically a mutable wrapping around an value
		 * @author xshi
		 *
		 * @param <E>
		 */
		
		
		/**
		 * A thread that will increment a shared integer by 1000
		 * @author xshi
		 */
		class ThreadA extends Thread
		{
			private SharedResource<Integer> buffer;
			
			public ThreadA(SharedResource<Integer> buffer)
			{
				this.buffer = buffer;
			}
			
			public void run()
			{
				for(int i = 0; i < 100; i++)
				{
					lock.acquire();
					buffer.setResource(buffer.getResource() + 1);
					lock.release();
				}
			}
		}
		
		SharedResource<Integer> buffer = new SharedResource<Integer>(0);
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 100; i++)
		{
			Thread thread = new ThreadA(buffer);
			threads.add(thread);
			thread.start();
		}
		for (Thread t : threads)
		{
			t.join();
		}
		
		System.out.println(buffer.getResource());
		//Since the system is locked, the value of the buffer should be 1000000
		assertEquals(buffer.getResource().intValue(), 10000);
	}
}