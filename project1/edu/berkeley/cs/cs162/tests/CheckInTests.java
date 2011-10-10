package edu.berkeley.cs.cs162.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ BoardTest.class, GameTest.class, LauncherTest.class,
		LocationTest.class, LockTest.class, ObserverTest.class,
		SemaphoreTest.class, SpinLockTest.class, StressTest.class,
		ThreadSafeQueueTest.class })
public class CheckInTests {
}