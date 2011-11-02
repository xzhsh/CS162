package edu.berkeley.cs.cs162.Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class TestClient extends BaseClient {
	public TestClient(String name)
	{
		super(name, MessageProtocol.TYPE_MACHINE);
	}
	
    public static void main(String[] args) {
        TestClient client = new TestClient(args[2]);
        try {
			client.connectTo(args[0], Integer.valueOf(args[1]));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	private void connectTo(String address, Integer portNumber) {
		// TODO Auto-generated method stub
		try {
			Socket c1 = new Socket(address, portNumber);
			Socket c2 = new Socket(address, portNumber);
			ServerConnection connection = new ServerConnection(c1, c2);
			System.out.println(connection.initiate3WayHandshake(new Random()));
			Message connectMessage = MessageFactory.createConnectMessage(MessageFactory.createMachinePlayerClientInfo(getName()));
			//connection.sendToServer(cInfo);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
