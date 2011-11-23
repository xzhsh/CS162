package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class TestClient extends BaseClient {
    public TestClient(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
    }

    public static void main(String[] args) {
        TestClient client = new TestClient(args[2]);
        try {
            if(client.connectTo(args[0], Integer.valueOf(args[1]))){
                System.out.println("Succesfully connected... now time to do shit.");
            }
        } catch (NumberFormatException e) {
        	System.out.println("Invalid port: " + args[1]);
        }
    }
}
