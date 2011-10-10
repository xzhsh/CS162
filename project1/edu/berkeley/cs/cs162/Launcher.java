package edu.berkeley.cs.cs162;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
   The Launcher contains the main() method. Launcher should be started by
   calling 'java Launcher <filename>', where filename is the name of the
   configuration file containing Game, Player, and Observer configurations.

   The Launcher will create a single new GameServer, load configuration settings
   from the specified file, and wait for the configured games to run and finish.
 */
public class Launcher {
    /**
       Loads information from the configuration file and creates all the
       specified Players, Observers, and Boards. For each game, ask the server
       to create and start running that game using GameServer.createGame(). Note
       that it should be possible to have some games running and finishing while
       others have not been created yet.
	   <p>
       The configuration file should have the following format:
       <PRE><code>
       Player &ltPlayerClassName&gt &ltPlayerName&gt
       ...
       Observer &ltObserverClassName&gt &ltObserverName&gt
       ...
       Game &ltGameName&gt &ltBlackPlayerName&gt &ltWhitePlayerName&gt &ltBoardSize&gt &ltTimeoutInMs&gt &ltObservers&gt</code></PRE>
	   <p>
       You may also assume that all Player and Observer lines come before all
       Game lines in the file. Threads should be created and started for all
       players and observers specified in the configuration file, and provided
       back to the caller in the observerThreads map.
     */
    public static void loadFromConfigurationFile(String filename, GameServer server, HashMap<Observer, Thread> observerThreads) {
    	File file = new File(filename);
    	try {
			loadFromConfigurationFile(new BufferedReader(new FileReader(file)), server, observerThreads);
		} catch (FileNotFoundException e) {
			System.err.printf("File not found: %s\n", e.getMessage());
		}
    }
    
    /**
     * Loads from a BufferedReader
     * 
     * This lets us generate virtual config files for testing.
     * 
     * @param reader
     * @param server
     * @param observerThreads
     */
	public static void loadFromConfigurationFile(BufferedReader reader, GameServer server, HashMap<Observer, Thread> observerThreads) {
    	HashMap<String, Player> nameToPlayers = new HashMap<String, Player>();
    	HashMap<String, Observer> nameToObserver = new HashMap<String, Observer>();

        try {
    		//line number for error reporting
    		int lineNumber = 0;
    		
    		//Buffered reader to read in the config file.
			while (reader.ready()) {
				String command = reader.readLine();
				
				lineNumber++;//line number starts at 1
				if (command == null) { break; }

				//skip empty commands
				if (command.isEmpty()) { continue; }

				String tokens[] = command.split(" ");
				switch(ParseHelper.parseType(tokens[0])) {
					case PLAYER:
						ParseHelper.parsePlayer(nameToPlayers, tokens, lineNumber);
						break;
					case OBSERVER:
						ParseHelper.parseObserver(nameToObserver, tokens, lineNumber);
						break;
					case GAME:
						ParseHelper.parseGame(nameToObserver, nameToPlayers, tokens, server, lineNumber);
						break;
					case INVALID:
						System.err.printf("Warning: %s is not a valid command. Skipping line %d\n", tokens[0], 
								lineNumber);
				}
			}
		}

        catch (IOException e) {
			System.err.println("Could not open configuration file: " + e.getMessage());
			return;
		}
    	
    	for (Observer o : nameToObserver.values()) {
    		Thread observerThread = new Thread(o);
    		observerThread.start();
    		observerThreads.put(o, observerThread);
    	}

    	for (Player p : nameToPlayers.values()) {
    		Thread playerThread = new Thread(p);
    		playerThread.start();
    		observerThreads.put(p, playerThread);
    	}
    }
    
    public static void signalAllToExit(HashMap<Observer, Thread> observerThreads) {
    	for (Observer o : observerThreads.keySet()) {
    		o.beginExit();
    	}
    }
    
    public static void exitAndJoinAllThreads(Thread serverThread, HashMap<Observer, Thread> observerThreads) {
    	try {
	    	for(Thread t : observerThreads.values()) {
	    		t.join();
	    	}

	    	serverThread.join();
    	}

    	catch (InterruptedException e) {
    		assert false : "Launcher Thread should not be interrupted";
    	}
    }
    
    public static void main(String[] args) {
		if (args.length != 1) {
		    System.err.println("Wrong number of args: should be 'java Launcher <filename>'");
		    return;
		}
		
		GameServer server = new GameServer();
		Thread serverThread = new Thread(server);
		HashMap<Observer, Thread> observerThreads = new HashMap<Observer, Thread>();
		serverThread.start();
		loadFromConfigurationFile(args[0], server, observerThreads);
		server.waitForGames();
		exitAndJoinAllThreads(serverThread, observerThreads);
    }
}
