package edu.berkeley.cs.cs162;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import edu.berkeley.cs.cs162.GoBoard.IllegalMoveException;

public class HumanPlayer extends Player {

    private int timeout;

    public HumanPlayer() {
    	super();
    }

    public void handleGameStart(String gameName, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) {

        final String g = gameName;
        final String bl = blackPlayerName;
        final String wh = whitePlayerName;
        final int b = board.getSize();
        final int t = moveTimeoutInMs;

        timeout = moveTimeoutInMs;

        addMessage(new Runnable() {
            public void run() {
                System.out.printf(String.format("%s - %s: Started with Black Player %s and White Player %s.\n%s - \tBoard Size: %dx%d\n%s - Move Time Limit: %dms\n",
                        name, g, bl, wh, name, b, b, name, t));
            }
        });
    }

    public void handleGameOver(String gameName, double blackPlayerScore, double whitePlayerScore) {
        final String g = gameName;
        final double bl = blackPlayerScore;
        final double wh = whitePlayerScore;

        addMessage(new Runnable() {
            public void run() {
                final String results;

                if (bl > wh) {
    		        results = "Black Player won";
    	        }

    	        else if (wh > bl) {
    		        results = "White Player won";
    	        }

                else {
    		        results = "Tie";
    	        }

                System.out.printf(String.format("%s - %s: Game over. %s.\n%s - \tScore:\n%s - \t\tBlack Player: %f\n%s - \t\tWhite Player: %f\n",
                    name, g, results, name, name, bl, name, wh));
            }
        });
    }

    public void handleMakeMove(String gameName, String playerName) {
        if((playerName.equals(name)) && this.gameName.equals(gameName)){
            final String g = gameName;
            final Player thisPlayer = this;

            final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            addMessage(new Runnable() {
                public void run() {
                    String input;
                    try{
                        System.out.print(goBoard.toString());
                        System.out.println("Please enter coordinates in the format X Y, or 'pass' to pass: ");

                        long startTime = System.currentTimeMillis();
                        while(true){
                            if(System.currentTimeMillis() - startTime >= timeout){
                                return;
                            }
                            else if(reader.ready()){
                                break;
                            }
                            else{
                                Thread.yield();
                            }
                        }

                        input = reader.readLine();
                        if(!input.equals("pass")){
                            String[] coordinates = input.split(" ");
                            server.sendMoveToGame(thisPlayer, g, new Location(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])));
                        }
                        else{
                            server.sendPassMoveToGame(thisPlayer, g);
                        }
                    }
                    catch(Exception e){
                        System.out.println("There was an error in your move. Sending a PASS.");
                        server.sendPassMoveToGame(thisPlayer, g);
                    }
                }
            });
        }

    }

    public void handleStonePlaced(String gameName, String playerName, Location loc, StoneColor color) {
        final String g = gameName;
        final String p = playerName;
        final Location l = loc;
        final StoneColor c = color;

        addMessage(new Runnable() {
            public void run() {
            	try {
					goBoard.makeMove(l, c);
				} catch (IllegalMoveException e) {
					assert false : "Messages should have been validated by the server";
				}
                System.out.printf(String.format("%s - %s: %s has placed a %s stone at (%d, %d).\n",
                        name, g, p, c, l.getX(), l.getY()));
            }
        });
    }

    public void handleStoneCaptured(String gameName, String playerName, Location loc, StoneColor color) {
        final String g = gameName;
        final String p = playerName;
        final Location l = loc;
        final StoneColor c = color;

    	addMessage(new Runnable() {
            public void run() {
                System.out.printf(String.format("%s - %s: %s has captured a %s stone at (%d, %d).\n",
    			name, g, p, c, l.getX(), l.getY()));
            }
        });
    }

    public void handlePlayerPass(String gameName, String playerName) {
        final String g = gameName;
        final String p = playerName;

        addMessage(new Runnable() {
            public void run() {
                System.out.printf(String.format("%s - %s: %s has passed.\n",
    			name, g, p));
            }
        });
    }

    public void handlePlayerError(String gameName, String playerName, String errorDescription) {
        final String g = gameName;
        final String p = playerName;
        final String e = errorDescription;

        addMessage(new Runnable() {
            public void run() {
                System.out.printf(String.format("%s - %s: %s has been disqualified due to illegal move: %s\n",
    			name, g, p, e));
            }
        });
    }

    /* exit() is called to inform a running observer that it should terminate */
    public void beginExit() {
    	addMessage(new Runnable() {
            public void run() {
                done = true;
            }
        });
    }

    public void run() {
        while(!done){
            Runnable message = getNextMessage();
            message.run();
        }
    }
}
