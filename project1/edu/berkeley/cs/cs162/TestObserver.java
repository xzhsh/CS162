package edu.berkeley.cs.cs162;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TestObserver extends Observer {
	
	private int movesTilFinish;
	private boolean correct;

	private static Map<String, TestObserver> observers = new HashMap<String, TestObserver>();
	private static Lock observerLock = new Lock();
    private ArrayList<String> messagesReceived;
    
    /**
     * Default constructor. This will simply construct a test observer that will always return false in isCorrect().
     */
    public TestObserver()
	{
		movesTilFinish = 0;
		correct = false;
        messagesReceived = new ArrayList<String>();
	}
    
	public TestObserver(int expectedMoves)
	{
		movesTilFinish = expectedMoves;
		correct = false;
        messagesReceived = new ArrayList<String>();
	}
	
	public void setName(String name)
	{
		super.setName(name);
		observerLock.acquire();
		observers.put(name, this);
		observerLock.release();
	}

    public ArrayList<String> getMessagesReceived(){
        return messagesReceived;
    }

	public boolean isCorrect()
	{
		return correct;
	}

	public void handleGameStart(String gameName, String blackPlayerName,
			String whitePlayerName, Board board, int moveTimeoutInMs) {
		messagesReceived.add("gs");
	}

	
	public void handleGameOver(String gameName, double blackPlayerScore,
		double whitePlayerScore) {
		messagesReceived.add("go");
        addMessage(new Runnable() {
			public void run()
			{
				correct = movesTilFinish == 0;
				done = true;
			}
		});
	}

	
	public void handleMakeMove(String gameName, String playerName) {

        messagesReceived.add("mm");
		addMessage(new Runnable() {
			public void run()
			{
				movesTilFinish--;
			}
		});
	}

	
	public void handleStonePlaced(String gameName, String playerName,
			Location loc, StoneColor color) {
		messagesReceived.add("sp");

	}

	
	public void handleStoneCaptured(String gameName, String playerName,
			Location loc, StoneColor color) {
		messagesReceived.add("sc");

	}

	
	public void handlePlayerPass(String gameName, String playerName) {
		messagesReceived.add("pp");

	}

	
	public void handlePlayerError(String gameName, String playerName,
			String errorDescription) {
		messagesReceived.add("pe");
	}

	public void beginExit() {
		addMessage(new Runnable() {
			public void run()
			{
				done = true;
			}
		});
	}
	
	public void run() {
		while (!done) {
            Runnable message = getNextMessage();
            message.run();
    	}
	}

	public int getMoves() {
		return movesTilFinish;
	}

	public static int getNumberOfInstantiatedObservers() {
		return observers.size();
	}
	
	public static boolean hasObserverWithName(String name) {
		return observers.containsKey(name);
	}

	public static void resetStatics() {
		observers = new HashMap<String, TestObserver>();
	}


}
