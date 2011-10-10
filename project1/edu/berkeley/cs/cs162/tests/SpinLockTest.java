package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.berkeley.cs.cs162.SpinLock;

/**
 * @author xshi
 *
 */
public class SpinLockTest {
	
	/**
	 * Simple test case that will create a lock, acquire it, and release it.
	 * 
	 * This test will timeout and fail in 5s if it does not complete.
	 */
	@Test(timeout=5000)
	public void SanityCheckTest() {
		SpinLock lock = new SpinLock();
		lock.acquire();
		lock.release();		
	}
	
	/**
	 * Tests Lock functionality with threads.
	 * @throws InterruptedException 
	 */
	@Test(timeout=10000)
	public void ThreadLockTest() throws InterruptedException
	{
		final SpinLock lock = new SpinLock();
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
		//Since the system is locked, the value of the buffer should be 2000
		assertTrue(buffer.getResource().intValue() == 2000);
	}
}