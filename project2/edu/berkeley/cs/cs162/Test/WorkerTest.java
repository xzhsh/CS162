package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import edu.berkeley.cs.cs162.Server.GameServer;
import edu.berkeley.cs.cs162.Writable.Message;

public class WorkerTest {
	@Mock GameServer mockServer;
	@Mock Message mockMessage;
	
	@Test
	public void test() {
		verify(mockServer).sendMessageToClient("TestClient", mockMessage);
		fail("Not yet implemented");
	}

}
