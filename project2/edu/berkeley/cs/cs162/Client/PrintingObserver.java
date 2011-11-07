package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class PrintingObserver extends Observer {

    public PrintingObserver(String name) {
        super(name, MessageProtocol.TYPE_OBSERVER);
    }

    private void connectTo(String address, Integer port) {
        try 
        {
            // Create the C2S and S2C sockets
            Socket c1 = new Socket(address, port);
            Socket c2 = new Socket(address, port);

            // Attempt to connect to the GameServer via 3-way Handshake
            connection = new ServerConnection(c1, c2);
            System.out.println(connection.initiate3WayHandshake(new Random()));
            Message connectMessage = MessageFactory.createConnectMessage(clientInfo);

            Message serverResponse = connection.sendSyncToServer(connectMessage);

            // If successfully connected to the GameServer...
            if (serverResponse.getMsgType() == MessageProtocol.OP_STATUS_OK) {
                System.out.println("Status OK, connected");

                // First, we need a list of all the games
                Message gameListMessage = connection.sendSyncToServer(MessageFactory.createListGamesMessage());
                if (serverResponse.getMsgType() == MessageProtocol.OP_STATUS_OK){
                    WritableList gameList = ((ResponseMessages.ListGamesStatusOkMessage) gameListMessage).getGameList();

                    // Join ALL the games!
                    for(Writable game : gameList){
                        Message joinResponse = connection.sendSyncToServer(MessageFactory.createJoinMessage((GameInfo) game));
                    }
                }

            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }
}
