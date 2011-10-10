package edu.berkeley.cs.cs162;

import java.util.Random;
import java.util.Vector;

import edu.berkeley.cs.cs162.GoBoard.IllegalMoveException;

/**
 * MachinePlayer
 * 
 * Implements a player that will pick a random move.
 * @author xshi
 */
public class MachinePlayer extends Player {

	private Random rng;
	
	public MachinePlayer() {
		super();
		rng = new Random();
	}
	
    public void handleGameStart(String gameName, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) {
    	
    }

    public void handleGameOver(String gameName, double blackPlayerScore, double whitePlayerScore) {

    }

    /**
     * If the game name and the playerName match, we have to make a move.
     */
    public void handleMakeMove(String gameName, String playerName) {
        if (playerName.equals(name) && this.gameName.equals(gameName)) {
        	
        	final int randomTicket = rng.nextInt();
            final String gName = gameName;
            final Player thisPlayer = this;
            
            addMessage(new Runnable() {
                public void run() {
                     //random chance based on the number of spaces left to pass
                    if (rng.nextInt(goBoard.board.getNumberOfColor(StoneColor.NONE)) == 0) {
                        server.sendPassMoveToGame(thisPlayer, gName);
                    }

                    else {
                        //randomize a move
                        Location loc = decideMove();

                        //if we decided to pass, send a pass message
                        if (loc == null) {
                            server.sendPassMoveToGame(thisPlayer, gName);
                        	System.out.println(name + " decided to pass. ticket #" + randomTicket);
                        }

                        else {
                            //otherwise, test the move and send the move to game
                            try {
                                goBoard.testMove(loc, getPlayerColor());
                                server.sendMoveToGame(thisPlayer, gName, loc);
                            }

                            catch (IllegalMoveException e) {
                                //if it's an illegal move, pass instead.
                                server.sendPassMoveToGame(thisPlayer, gName);
                            }
                        }
                    }
                }
            });
    	}
    }
    
    public void handleStonePlaced(String gameName, String playerName, Location loc, StoneColor color) {
        final Location l = loc;
        final StoneColor c = color;

        addMessage(new Runnable() {
            public void run() {
                try {
                    goBoard.makeMove(l, c);
		        }
                catch (IllegalMoveException e) {
                    //There shouldn't be any illegal moves when the server tells us it's okay
                    assert false : "Programmer Error: Illegal move caught in Player";
		        }
            }
        });
    }
    
    public void handleStoneCaptured(String gameName, String playerName, Location loc, StoneColor color) {
    	
    }

    public void handlePlayerPass(String gameName, String playerName) {
    	
    }

    public void handlePlayerError(String gameName, String playerName, String errorDescription) {
    	
    }

    /**
     * exit() is called to inform a running observer that it should terminate 
     * THIS SHOULD ONLY BE CALLED ONCE BY GAME SERVER
     */
    public void beginExit() {
        addMessage(new Runnable() {
            public void run() {
                done = true;
            }
        });
    }
    
    /**
     * @return a random location that doesn't have a piece, or null if it wants to pass.
     */
    private Location decideMove() {
    	Location loc = new Location(rng.nextInt(goBoard.board.getSize()), rng.nextInt(goBoard.board.getSize()));
    	int chanceOfPass = 0;
    	Vector<Location> invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);
    	
    	while (invalidatedLocations.contains(loc) || goBoard.board.getAtLocation(loc) != StoneColor.NONE) {
    		//adds .5% of pass per try
    		chanceOfPass+= 5;
    		if (chanceOfPass >= 10000 || rng.nextInt(10000-chanceOfPass) == 0) {
    			return null;
    		}
    		loc = new Location(rng.nextInt(goBoard.board.getSize()), rng.nextInt(goBoard.board.getSize()));
    		invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);
    	}

		return loc;
    }

    /**
     * Run the thread.
     * 
     * While active, the machine player will block for messages from the game, and make decisions
     * based on the message.
     */
    public void run() {
    	while (!done) {
            Runnable message = getNextMessage();
            message.run();
    	}
    }
}
