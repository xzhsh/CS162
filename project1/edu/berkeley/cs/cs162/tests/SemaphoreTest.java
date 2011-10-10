package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.berkeley.cs.cs162.Semaphore;

/**
 * Test cases for Semaphore.
 * <p>
 * Most of the functionality is tested by running LockTest. 
 * These cases only test functionality that is exclusive to Semaphores
 * 
 * @author xshi
 *
 */
public class SemaphoreTest {

	@Test(timeout=5000)
	public void SimpleSemaphoreTest() throws InterruptedException {

		final SharedResource<Integer> resource = new SharedResource<Integer>(0);
		final Semaphore s = new Semaphore(4);
		
		class TestThread extends Thread{
			public void run()
			{
				s.p();
				s.p();
				s.p();
				resource.setResource(1);
				s.p();
				resource.setResource(2);
			}
		}

		s.p();
		assertEquals(resource.getResource(), Integer.valueOf(0));
		Thread t = new TestThread();
		t.start();
		Thread.sleep(1000);
		assertEquals(resource.getResource(), Integer.valueOf(1));
		Thread.sleep(1000);
		assertEquals(resource.getResource(), Integer.valueOf(1));
		s.v();
		t.join();
		assertEquals(resource.getResource(), Integer.valueOf(2));
	}
}
