package edu.berkeley.cs.cs162;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ParseHelper {
	
	enum CommandType {
		PLAYER,
		OBSERVER,
		GAME,
		INVALID
	}
	
	public static CommandType parseType(String command) {
		if (command.startsWith("Player")) {
			return CommandType.PLAYER;
		}

		if (command.startsWith("Observer")) {
			return CommandType.OBSERVER;
		}

		if (command.startsWith("Game")) {
			return CommandType.GAME;
		}

		return CommandType.INVALID;
	}
	
	public static void parseObserver(Map<String, Observer> observers, String[] tokens, int lineNumber) {
		Object observer = parseClassType(tokens, Observer.class, lineNumber);

        if (observer != null) {
			if (observer instanceof LoggingObserver) {
				//special logic handling logging observer. (Yeah I know :[...)
				File logFile = new File(tokens[2] + ".txt");
				try {
					((LoggingObserver)observer).setOutputStream(new FileOutputStream(logFile));
				}

                catch (FileNotFoundException e) {
					System.err.println("Cannot open file for logging");
				}
			}

			observers.put(tokens[2],(Observer)observer);
		}

		else {
			System.err.println("Observer was Null!");
		}
	}
	
	public static void parsePlayer(Map<String, Player> players, String[] tokens, int lineNumber) {
		Object player = parseClassType(tokens, Player.class, lineNumber);

		if (player != null) {
			players.put(tokens[2],(Player)player);
		}

		else {
			System.err.println("Player was Null!");
		}
	}

	private static Object parseClassType(String[] tokens, Class<?> classType, int lineNumber) {
		if (tokens.length != 3) {
			System.err.printf("Warning: not a valid command. Skipping line %d (Wrong number of arguments)\n", lineNumber);
			return null;
		}

		String className = "edu.berkeley.cs.cs162." + tokens[1];
		String playerName = tokens[2];

        try {
			//Grab the class for the class name.
			Class<?> playerClass = Class.forName(className);
			//check if it's a valid PlayerClass.
			if (!classType.isAssignableFrom(playerClass)) {
				//if not, print error message and continue.
				System.err.printf("Warning: %s is not a valid class. Skipping line %d (Unassignable Class)\n", className, lineNumber);
				return null;
			}

			Observer newObserver = (Observer) playerClass.getConstructors()[0].newInstance();
			newObserver.setName(playerName);
			return newObserver;
		}

        catch (IllegalArgumentException e) {
			assert false : "All players should implement a constructor that only 0 argument";
		}

        catch (SecurityException e) {
			assert false : "A security exception shouldn't happen...";
		}

        catch (InstantiationException e) {
			System.err.printf("Warning: %s is not a valid class. Skipping line %d (Instantiation Error)\n", className, lineNumber);
			return null;
		}

        catch (IllegalAccessException e) {
			assert false : "All players should implement a constructor that only take a single name";
		}

        catch (InvocationTargetException e) {
			assert false : "All players should implement a constructor that only take a single name";
		}

        catch (ClassNotFoundException e) {
			System.err.printf("Warning: %s is not a valid class. Skipping line %d (Class not found)\n", className, lineNumber);
			return null;
		}

		assert false : "Should never get here";
		return null;
	}

	/**
	 * Validates and parses the parameters for a Game
	 * @param nameToObserver
	 * @param nameToPlayers
	 * @param tokens
	 * @return true if valid
	 */
	public static void parseGame(HashMap<String, Observer> nameToObserver, HashMap<String, Player> nameToPlayers,
                                 String[] tokens, GameServer server, int lineNumber) {
		//check black player
		Vector<Observer> observers = new Vector<Observer>();

		if (!nameToPlayers.containsKey(tokens[2])) {
			System.err.printf("Black Player %s has not been initialized yet! Skipping line %d\n", 
					tokens[2], lineNumber);
			return;
		}

		if (!nameToPlayers.containsKey(tokens[3])) {
			System.err.printf("White Player %s has not been initialized yet! Skipping line %d\n", 
					tokens[3], lineNumber);
			return;
		}
		
		for (int i = 6; i < tokens.length; i++) {
			if (!nameToObserver.containsKey(tokens[i])) {
			    //print an error message if a game tries to add an observer that hasn't been initialized yet.
				System.err.printf("Observer %s has not been initialized yet! Skipping line %d\n", 
						tokens[i], lineNumber);
				return;
			}

			observers.add(nameToObserver.get(tokens[i]));
		}

		server.createGame(tokens[1], nameToPlayers.get(tokens[2]), nameToPlayers.get(tokens[3]), 
				new Board(Integer.valueOf(tokens[4])), Integer.valueOf(tokens[5]), observers);
	}
}
