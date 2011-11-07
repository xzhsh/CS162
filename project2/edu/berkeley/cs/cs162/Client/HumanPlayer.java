package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
    }

    public static void main(String[] args){
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        HumanPlayer player = new HumanPlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if(player.connectTo(address, port)){
            System.out.println("HumanPlayer " + player.getName() + " is connected to the server!");
        }
    }

    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackPlayerName = m.getBlackClientInfo().getName();
        String whitePlayerName = m.getWhiteClientInfo().getName();


    }

    @Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        double blackPlayerScore = m.getBlackScore();
        double whitePlayerScore = m.getWhiteScore();
        String winner = m.getWinner().getName();
        byte reason = m.getReason();
        String errorPlayerName = m.getErrorPlayer().getName();
        String errorMsg = m.getErrorMessage();

    }

    @Override
    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String player = m.getPlayer().getName();
        byte type = m.getMoveType();
        Location loc = m.getLocation();
        WritableList stonesCaptured = m.getLocationList();

    }

    @Override
    protected void handleGetMove() throws IOException {

//    	ServerMessages.MakeMoveMessage moveMsg = (ServerMessages.MakeMoveMessage) m;
//    	GameInfo gInfo  = moveMsg.getGameInfo();
    	
    	
    	final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	String humanInput = reader.readLine();
    	if (humanInput.equals("Pass")) {
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
    	}
        //send a message to the server with byte moveType and Location loc
    
    }
}
