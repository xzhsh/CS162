package edu.berkeley.cs.cs162;

/**
 * Observer that prints messages it receives to stdout.
 * 
 * @author xshi, Jay
 */
public class PrintingObserver extends Observer {
	
	protected static final String OBSERVER_EXIT_MESSAGE = "SIG_OBSERVER_EXIT";

	private String nextMessage;

	public PrintingObserver() {
		super();
	}
	
    public void handleGameStart(String gameName, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) {
        final String g = gameName;
        final String bl = blackPlayerName;
        final String wh = whitePlayerName;
        final int b = board.getSize();
        final int t = moveTimeoutInMs;

        addMessage(new Runnable() {
            public void run() {
                setNextMessage(String.format("%s - %s: Started with Black Player %s and White Player %s.\n%s - \tBoard Size: %dx%d\n%s - Move Time Limit: %dms\n",
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

                setNextMessage(String.format("%s - %s: Game over. %s.\n%s - \tScore:\n%s - \t\tBlack Player: %f\n%s - \t\tWhite Player: %f\n",
                    name, g, results, name, name, bl, name, wh));
            }
        });
    }

    public void handleMakeMove(String gameName, String playerName) {
        final String g = gameName;
        final String p = playerName;

        addMessage(new Runnable() {
            public void run() {
                setNextMessage(String.format("%s - %s: %s's move\n",
    			name, g, p));
            }
        });
    }

    public void handleStonePlaced(String gameName, String playerName, Location loc, StoneColor color) {
        final String g = gameName;
        final String p = playerName;
        final Location l = loc;
        final StoneColor c = color;

        addMessage(new Runnable() {
            public void run() {
                setNextMessage(String.format("%s - %s: %s has placed a %s stone at (%d, %d).\n",
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
                setNextMessage(String.format("%s - %s: %s has captured a %s stone at (%d, %d).\n",
    			name, g, p, c, l.getX(), l.getY()));
            }
        });
    }

    public void handlePlayerPass(String gameName, String playerName) {
        final String g = gameName;
        final String p = playerName;

        addMessage(new Runnable() {
            public void run() {
                setNextMessage(String.format("%s - %s: %s has passed.\n",
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
                setNextMessage(String.format("%s - %s: %s has been disqualified due to illegal move: %s\n",
    			name, g, p, e));
            }
        });
    }

    /**
     * beginExit() is called to inform a running observer that it should terminate.
     * There should be NO further messages sent to this observer after beginExit is called.
     * 
     * This observer should print out the rest of the messages on the queue and terminate.
     */
    public void beginExit() {
    	addMessage(new Runnable() {
            public void run() {
                done = true;
            }
        });
    }
    
    /**
     * Dequeues string messages from the message queue and prints them to stdout
     */
    public void run() {
    	while (true) {
			Runnable message = getNextMessage();
			message.run();
			if(done) { break; }
			System.out.print(getNextMessageString());
    	}
    }

	protected String getNextMessageString() {
		return nextMessage;
	}

	public void setNextMessage(String nextMessage) {
		this.nextMessage = nextMessage;
	}
}
