package edu.berkeley.cs.cs162.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import edu.berkeley.cs.cs162.ThreadSafeQueue;

import org.junit.Test;

public class ThreadSafeQueueTest {
	@Test(timeout = 10000)
	public void HeuristicThreadTest() throws InterruptedException {
		final ThreadSafeQueue<Integer> queue = new ThreadSafeQueue<Integer>(10);
		class IncrementorThread extends Thread {
			int value;
			public IncrementorThread(int value)
			{
				this.value = value;
			}
			public void run()
			{
				for (int i = 0; i < 100; i++)
					queue.add(value);
			}
		}
		
		class AggregatorThread extends Thread {
			int value;
			public AggregatorThread()
			{
				this.value = 0;
			}
			public void run()
			{
				while(true)
				{
					int newVal = queue.get();
					if (newVal > 0)
						value += newVal;
					else
						break;
				}
			}
			public int getValue()
			{
				return value;
			}
		}
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AggregatorThread aggregator = new AggregatorThread();
		aggregator.start();
		assertEquals(aggregator.getValue(), 0);
		
		for (int i = 0; i < 50; i++)
		{
			threads.add(new IncrementorThread(i+1));
		}
		
		for (Thread t : threads)
		{
			t.start();
		}
		for (Thread t : threads)
		{
			t.join();
		}
		queue.add(0);
		aggregator.join();
		
		assertEquals(aggregator.getValue(), 127500);
	}
	
	@Test(timeout = 20000)
	public void SlowAggregatorThreadTest() throws InterruptedException {
		final ThreadSafeQueue<Integer> queue = new ThreadSafeQueue<Integer>(10);
		class IncrementorThread extends Thread {
			int value;
			public IncrementorThread(int value)
			{
				this.value = value;
			}
			public void run()
			{
				for (int i = 0; i < 100; i++)
					queue.add(value);
			}
		}
		
		class AggregatorThread extends Thread {
			int value;
			public AggregatorThread()
			{
				this.value = 0;
			}
			public void run()
			{
				while(true)
				{
					int newVal = queue.get();
					if (newVal > 0)
						value += newVal;
					else
						break;
					try {
						Thread.sleep(1);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			public int getValue()
			{
				return value;
			}
		}
		
		ArrayList<Thread> threads = new ArrayList<Thread>();
		AggregatorThread aggregator = new AggregatorThread();
		aggregator.start();
		assertEquals(aggregator.getValue(), 0);
		
		for (int i = 0; i < 50; i++)
		{
			threads.add(new IncrementorThread(i+1));
		}
		
		for (Thread t : threads)
		{
			t.start();
		}
		for (Thread t : threads)
		{
			t.join();
		}
		queue.add(0);
		aggregator.join();
		
		assertEquals(aggregator.getValue(), 127500);
	}
	
	@Test(timeout = 10000)
	public void FIFOCheck() throws InterruptedException {
		final ThreadSafeQueue<String> queue = new ThreadSafeQueue<String>(10);
		class ProducerThread extends Thread {
			public ProducerThread()
			{
			}
			public void run()
			{
				for (int i = 0; i < 10; i++)
				{
					queue.add(String.valueOf(i));
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						assert false : "Should not be interrupted";
						assertTrue(false);
					}
				}
				queue.add("");
			}
		}
		
		class ConsumerThread extends Thread {
			StringBuffer sb;
			public ConsumerThread()
			{
				sb = new StringBuffer();
			}
			public void run()
			{
				while(true)
				{
					String newVal = queue.get();
					if (!newVal.isEmpty())
						sb.append(newVal);
					else
						break;
				}
			}
			public String getValue()
			{
				return sb.toString();
			}
		}
		ConsumerThread consumer = new ConsumerThread();
		ProducerThread producer = new ProducerThread();
		
		consumer.start();
		assertEquals(consumer.getValue(), "");
		producer.start();

		producer.join();
		consumer.join();
		
		assertEquals(consumer.getValue(), "0123456789");
	}
	
	@Test(timeout = 10000)
	public void BlockingAddCheck() throws InterruptedException {
		final ThreadSafeQueue<String> queue = new ThreadSafeQueue<String>(5);
		final SharedResource<Integer> currentNumber = new SharedResource<Integer>(-1);
		class Add6Thread extends Thread {
			public Add6Thread()
			{
			}
			public void run()
			{
				for (int i = 0; i < 6; i++)
				{	
					queue.add(String.valueOf(i));
					currentNumber.setResource(i);
				}
			}
		}
		
		Add6Thread add6 = new Add6Thread();

		assertEquals(-1, currentNumber.getResource().intValue());
		add6.start();
		Thread.sleep(2000);
		assertEquals(4, currentNumber.getResource().intValue());
		queue.get();
		add6.join();
		assertEquals(5, currentNumber.getResource().intValue());
	}
	
	@Test(timeout = 10000)
	public void GetWithTimeOutCheck() throws InterruptedException {
		final ThreadSafeQueue<String> queue = new ThreadSafeQueue<String>(5);
		final SharedResource<Integer> currentNumber = new SharedResource<Integer>(-1);
		class TimeoutThread extends Thread {
			int timeoutInMs;
			public TimeoutThread(int timeoutInMs)
			{
				this.timeoutInMs = timeoutInMs;
			}
			public void run()
			{
				try {
					queue.getWithTimeout(timeoutInMs);
				} catch (TimeoutException e) {
					currentNumber.setResource(timeoutInMs);
				}
			}
		}
		
		TimeoutThread add6 = new TimeoutThread(1000);

		assertEquals(-1, currentNumber.getResource().intValue());
		add6.start();
		Thread.sleep(500);
		assertEquals(-1, currentNumber.getResource().intValue());
		Thread.sleep(1000);
		assertEquals(1000, currentNumber.getResource().intValue());
	}
}
