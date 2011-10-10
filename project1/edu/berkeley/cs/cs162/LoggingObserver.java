package edu.berkeley.cs.cs162;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import edu.berkeley.cs.cs162.GoBoard.IllegalMoveException;

/**
 * Logs the game to a outputstream.
 * 
 * The logging observer will also print out the board state after receiving messages that signify a board change.
 * 
 * @author xshi
 */
public class LoggingObserver extends PrintingObserver {
	BufferedWriter writer;
	HashMap<String, GoBoard> boardReferences;

	private boolean printBoard;

	/**
	 * Constructor for Launcher to recognize.
	 * @param name name of this observer
	 */
	public LoggingObserver() {
		super();
		printBoard = true;
		writer = new BufferedWriter(new OutputStreamWriter(System.out));
		boardReferences = new HashMap<String, GoBoard>();
	}
	
	/**
	 * Prints the board if it's enabled.
	 */
	public void sendPrintBoardMessage(String gameName)
	{
		final String gname = gameName;
		if (printBoard)
		{
			Runnable message = new Runnable() {
				public void run() {
					setNextMessage(boardReferences.get(gname).toString());
				}
			};
			addMessage(message);
		}
	}
	
	/**
	 * Sets whether this observer should track the board and print it.
	 * 
	 * Note this should be set before any games it's observer start, or weird stuff can happen.
	 * 
	 * Do not change if any games are in progress.
	 * @param print
	 */
	public void setPrintBoard(boolean print)
	{
		printBoard = print;
	}

	/**
	 * Redirects the output stream of this observer to something else.
	 * @param out
	 */
	public void setOutputStream(OutputStream out) {
		writer = new BufferedWriter(new OutputStreamWriter(out));
	}
	
    public void handleGameStart(String gameName, String blackPlayerName, String whitePlayerName, Board board, int moveTimeoutInMs) {
    	boardReferences.put(gameName, new GoBoard(board));
    	sendPrintBoardMessage(gameName);
    	super.handleGameStart(gameName, blackPlayerName, whitePlayerName, board, moveTimeoutInMs);
	}

    public void handleGameOver(String gameName, double blackPlayerScore, double whitePlayerScore) {
    	sendPrintBoardMessage(gameName);
    	super.handleGameOver(gameName, blackPlayerScore, whitePlayerScore);
	}

    public void handleMakeMove(String gameName, String playerName){
    	sendPrintBoardMessage(gameName);
    	super.handleMakeMove(gameName, playerName);
    }

    public void handlePlayerPass(String gameName, String playerName) {
    	sendPrintBoardMessage(gameName);
    	super.handlePlayerPass(gameName, playerName);
    }
    
    public void handleStonePlaced(String gameName, String playerName, Location loc, StoneColor color) {
    	if (printBoard)
    		{
    			try {
					boardReferences.get(gameName).makeMove(loc, color);
				}
		
		        catch (IllegalMoveException e) {
					assert false: "Programmer error: all errors should be caught by this point";
				}
    		}
    	super.handleStonePlaced(gameName, playerName, loc, color);
    }

    public void handlePlayerError(String gameName, String playerName, String errorDescription) {
    	sendPrintBoardMessage(gameName);
    	final String g = gameName;
        final String p = playerName;
        final String e = errorDescription;
        
        addMessage(new Runnable() {
            public void run() {
                setNextMessage(String.format("%s - %s: %s has been disqualified due to illegal move: %s\n",
    			name, g, p, e));
            }
        });
    	//super.handlePlayerError(gameName, playerName, errorDescription);
    }
    
    /**
     * {@inheritDoc}
     */
	public void run() {
    	while (true) {
    		Runnable message = getNextMessage();
			message.run();
			if (done) { break; }
    		try {
				writer.write(getNextMessageString());
				writer.flush();
			}
            catch (IOException e) {
				e.printStackTrace();
				break;
			}
    	}
    	try {
			writer.flush();
		}
        catch (IOException e) {
			e.printStackTrace();
		}
    }	
}
