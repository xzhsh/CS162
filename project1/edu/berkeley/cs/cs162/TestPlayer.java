package edu.berkeley.cs.cs162;

import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

/**
 * TestPlayer
 * 
 * Implements a player that will pick moves in a predetermined way.
 * @author xshi
 */
public class TestPlayer extends MachinePlayer {
	
	private static Map<String, TestPlayer> players = new HashMap<String, TestPlayer>();
	
	protected class Move {
		Location moveLoc;
		Move()
		{
			this(null);
		}
		Move(Location loc)
		{
			moveLoc = loc;
		}
		
		boolean isPass()
		{
			return moveLoc == null;
		}
		boolean isMove()
		{
			return !isPass();
		}
		
		Location getMoveLoc()
		{
			return moveLoc;
		}
	}
	
	Queue<Move> moves;
	
	Lock moveLock;
	public TestPlayer()
	{
		moves = new ArrayDeque<Move>();
		moveLock = new Lock();
	}
	
	
	public void setName(String name)
	{
		super.setName(name);
		players.put(name, this);
	}
	/**
	 * Adds a move to the queue to be executed when its this player's turn
	 * @param move
	 */
	public void addMove(Location move)
	{
		moveLock.acquire();
		moves.add(new Move(move));
		moveLock.release();
	}
	/**
	 * Adds a pass to the queue to be executed when its this player's turn
	 */
	public void addPass()
	{
		moveLock.acquire();
		moves.add(new Move());
		moveLock.release();
	}
	public Queue<Move> getMoves() {
		return moves;
	
	}
	/**
     * If the game name and the playerName match, we have to make a move.
     */
    public void handleMakeMove(String gameName, String playerName) {
        if (playerName.equals(name) && this.gameName.equals(gameName)) {
            final String gName = gameName;
            final Player thisPlayer = this;

            addMessage(new Runnable() {
                public void run() {
                	Move move;
                	moveLock.acquire();
                	if (moves.isEmpty())
                	{
                		assert false : "No more moves!";
                		move = new Move();
                	}
                	else
                	{
                		move = moves.remove();
                	}
                	moveLock.release();
                	if (move.isMove())
                	{
                		server.sendMoveToGame(thisPlayer, gName, move.getMoveLoc());
                	}
                	else
                	{
                		server.sendPassMoveToGame(thisPlayer, gName);
                	}
                }
            });
    	}
    }

	public static int getNumberOfInstantiatedPlayers() {
		return players.size();
	}
	
	public static boolean hasPlayerWithName(String name) {
		return players.containsKey(name);
	}
	public static void resetStatics() {
		players = new HashMap<String, TestPlayer>();
	}
}
