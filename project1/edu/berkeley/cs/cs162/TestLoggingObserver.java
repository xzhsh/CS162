package edu.berkeley.cs.cs162;

/**
 * Same as a logging observer, but is used solely for testing.
 * The only difference in this Observer is that it doesn't keep track of board or print it out board by default.
 * 
 * @author xshi
 *
 */
public class TestLoggingObserver extends LoggingObserver {

	public TestLoggingObserver()
	{
		super();
		super.setPrintBoard(false);
	}
}
