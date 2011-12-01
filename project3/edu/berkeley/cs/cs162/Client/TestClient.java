package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.MessageProtocol;

public class TestClient extends BaseClient {
    public TestClient(String name, String password) {
        super(name, password, MessageProtocol.TYPE_MACHINE);
    }

    public static void main(String[] args) {
    	//TODO SAME PASSWORD SHIT
        TestClient client = new TestClient(args[2], args[2] + "jaysucks");
        try {
            if(client.connectTo(args[0], Integer.valueOf(args[1]))){
                System.out.println("Succesfully connected... now time to do shit.");
            }
        } catch (NumberFormatException e) {
        	System.out.println("Invalid port: " + args[1]);
        }
    }
}
