package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.ConnectionWorker;
import edu.berkeley.cs.cs162.Server.GameServer;
import static org.mockito.Mockito.*;

public class ConnectionWorkerTest {	
	
	@Test
	public void FunctionalityCheck() throws IOException, InterruptedException {
		/*GameServer serverMock = mock(GameServer.class);
		Socket socketMock = mock(Socket.class);
		//InputStream isMock = mock(InputStream.class);

		final int SYN_ID = 1234567890;
		class InputStreamStub extends InputStream {

			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return SYN_ID;
			}
		}
		when(socketMock.getInputStream()).thenReturn(new InputStreamStub());
		when(serverMock.getNextConnection()).thenReturn(socketMock);
		ConnectionWorker worker = new ConnectionWorker(serverMock);
		//worker.run();
		Thread cWorkerThread = new Thread(worker);
		cWorkerThread.start();
		cWorkerThread.join();
		verify(serverMock).handleSYN(SYN_ID, socketMock);*/
	}

}
