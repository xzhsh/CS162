package edu.berkeley.cs.cs162.Client;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.ServerMessages;

abstract public class BaseClient implements Client {

    String name;
    byte type;
    ClientInfo clientInfo;
    private ServerConnection connection;
    static Random rng = new Random();
    public BaseClient(String name, byte type) {
        this.name = name;
        this.type = type;
        clientInfo = MessageFactory.createClientInfo(this.name, this.type);
    }

    public BaseClient(String name) {
        this(name, (byte) -1);
    }

    public BaseClient() {
        this("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getType() {
        return this.type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public boolean connectTo(String address, Integer port){
        try
        {
            // Create the C2S and S2C sockets
            Socket c1 = new Socket(address, port);
            Socket c2 = new Socket(address, port);

            // Attempt to connect to the GameServer via 3-way Handshake
            connection = new ServerConnection(c1, c2);
            if (!getConnection().initiate3WayHandshake(rng))
            {
            	return false;
            }
            Message connectMessage = MessageFactory.createConnectMessage(clientInfo);
            Message serverResponse = getConnection().sendSyncToServer(connectMessage);

            return (serverResponse.isOK());
        }
        catch(IOException e) {
        	e.printStackTrace();
        	System.out.println(e.getLocalizedMessage() + " " + getName() + " @ address: " + address + " port :" + port);
        	
        	return false;
    	}
    }

    protected void handleMessage(Message m) throws IOException {
        switch (m.getMsgType()) {
            case MessageProtocol.OP_TYPE_GAMESTART:
                handleGameStart((ServerMessages.GameStartMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GAMEOVER:
                handleGameOver((ServerMessages.GameOverMessage) m);
                break;
            case MessageProtocol.OP_TYPE_MAKEMOVE:
                handleMakeMove((ServerMessages.MakeMoveMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GETMOVE:
                handleGetMove();
                break;
            default:
                assert false: "No method defined for message type.";
        }
    }

    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    protected void handleGetMove() throws IOException {
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    protected void disconnect() throws IOException {
        getConnection().sendDisconnectToServer();
    }

	public ServerConnection getConnection() {
		return connection;
	}
}
