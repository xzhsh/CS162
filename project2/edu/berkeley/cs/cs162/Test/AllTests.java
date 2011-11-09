package edu.berkeley.cs.cs162.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ ClientConnectionTest.class, ConnectionWorkerTest.class,
		DataTypeIOTest.class, HalfOpenConnectionTest.class, MachinePlayerTest.class,
		PrintingObserverTest.class, ReaderWriterLockTest.class, ServerStressTest.class,
		WritablesTest.class })
public class AllTests {

}
