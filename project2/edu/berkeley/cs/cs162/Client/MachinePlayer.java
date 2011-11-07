package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MachinePlayer extends Player {
	
    public MachinePlayer(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
    }

    public static void main(String[] args){
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        MachinePlayer player = new MachinePlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if(player.connectTo(address, port)){
            System.out.println("MachinePlayer " + player.getName() + " is connected to the server!");
            try {
                player.runExecutionLoop();
            } catch (IOException e) {
                System.out.println("An error occurred... MachinePlayer " + player.getName() + " terminating.");
            }
        }
    }

    private void runExecutionLoop() throws IOException {
        while (true) {
            if (/*waitingForGames for running purposes*/false) {

            } else {

            }

            handleMessage(connection.readFromServer());
        }
    }
    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackPlayerName = m.getBlackClientInfo().getName();
        String whitePlayerName = m.getWhiteClientInfo().getName();

        board = m.getBoardInfo().getBoard();


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
    
    private Location decideMove() {
    	Random rng = new Random();
    	int size = board.getCurrentBoard().getSize();
    	BoardLocation loc = new BoardLocation(rng.nextInt(size), rng.nextInt(size));
    	int chanceOfPass = 0;
//    	Vector<Location> invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);
   //need to get board state
    	boolean valid = false;
    	while (!valid) {
    		
    		
    		//adds .5% of pass per try
    		chanceOfPass+= 5;
    		if (chanceOfPass >= 10000 || rng.nextInt(10000-chanceOfPass) == 0) {
    			return null;
    		}
    		loc = new BoardLocation(rng.nextInt(size), rng.nextInt(size));
//    		invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);
    		try {
    			board.testMove(loc, currentColor);
        		valid = true;
    		} catch (GoBoard.IllegalMoveException e) {}
    	}

		return MessageFactory.createLocationInfo(loc.getX(), loc.getY());
    }
    @Override
    protected void handleGetMove() throws IOException {
        long startTime = System.currentTimeMillis();
        int moveTime = 2000;
        while (true) {
            if(System.currentTimeMillis() - startTime >= moveTime){
                return;
            }
           
        
    	Location loc = decideMove();
    	if (loc == null) {
    		byte moveCode = MessageProtocol.MOVE_PASS;
    		Location nullLoc = MessageFactory.createLocationInfo(0, 0);
      		Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, nullLoc);
    		connection.sendReplyToServer(getMoveResp);
    	} else {
    		byte moveCode = MessageProtocol.MOVE_STONE;
    		Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, loc); 		
    		connection.sendReplyToServer(getMoveResp);
    	}
        //send a message to the server with byte moveType and Location loc
        }
    }
        

}
