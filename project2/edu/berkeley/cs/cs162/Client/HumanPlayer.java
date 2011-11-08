package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.StoneColor;
import edu.berkeley.cs.cs162.Writable.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
    }

    public static void main(String[] args) {

        HumanPlayer player;
        String address;
        Integer port;

        try{
            player = new HumanPlayer(args[2]);
            address = args[0];
            port = Integer.valueOf(args[1]);
        }
        catch (Exception e){
            System.out.println("Enter arguments in the following format: <host> <port> <playername>");
            return;
        }

        if (player.connectTo(address, port)) {
            System.out.println("HumanPlayer " + player.getName() + " is connected to the server!");

            try {
                player.runExecutionLoop();
            } catch (IOException e) {
                System.out.println("An error occurred... HumanPlayer " + player.getName() + " terminating.");
            }
        }
    }

    @Override
    protected void handleGetMove() throws IOException {

//    	ServerMessages.MakeMoveMessage moveMsg = (ServerMessages.MakeMoveMessage) m;
//    	GameInfo gInfo  = moveMsg.getGameInfo();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //doesn't account for time?
        /*String humanInput = reader.readLine();

        if (humanInput.equals("pass")) {
            byte moveCode = MessageProtocol.MOVE_PASS;
            Location loc = MessageFactory.createLocationInfo(0, 0);
            Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, loc);
            connection.sendReplyToServer(getMoveResp);
        }
        //send message to worker thread that player has passed

        else {
            byte moveCode = MessageProtocol.MOVE_STONE;
            String[] outCoordinates = humanInput.split(" ");
            int xCoord = Integer.parseInt(outCoordinates[0]);
            int yCoord = Integer.parseInt(outCoordinates[1]);
            Location loc = MessageFactory.createLocationInfo(xCoord, yCoord);
            Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, loc);

            connection.sendReplyToServer(getMoveResp);
            //send message to server about new move
        }*/
        //send a message to the server with byte moveType and Location loc

        byte moveType;
        Location loc;

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime >= 20000) {
            if (reader.ready()) {
                break;
            }
        }

        String input = reader.readLine();

        if (input.equals("pass")) {
            moveType = MessageProtocol.MOVE_PASS;
            loc = MessageFactory.createLocationInfo(0, 0);
        } else {
            moveType = MessageProtocol.MOVE_STONE;

            String[] coordinates = input.split(" ");
            loc = MessageFactory.createLocationInfo(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
        }

        Message m = MessageFactory.createGetMoveStatusOkMessage(moveType, loc);

        connection.sendReplyToServer(m);
    }
}
