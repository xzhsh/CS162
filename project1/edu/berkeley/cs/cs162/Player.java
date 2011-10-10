package edu.berkeley.cs.cs162;

/**
   A Player is a special Observer that can also play in games. Each player must maintain
   its own private board state, and cannot directly access the Game's board or any other
   Player's board.

   Players will be initialized with a server and a unique name. Interaction with the Game
   object in any way must be done through the server.
 */
public abstract class Player extends Observer {

    protected GameServer server;
    protected String gameName;
    protected GoBoard goBoard;
    private StoneColor playerColor;

    // Constructor
    public Player()
    {
    	super();
    }
    
    // Sets the this player's server.
    public void setServer(GameServer server) { this.server = server; }

    // Returns the player's board.
    public Board getBoard(){ return goBoard.board; }

    // Initializes the player's board by copying another board.
    public void initializeBoard(Board initialBoard){ this.goBoard = new GoBoard(initialBoard);}

    /**
     * Sets the name of this player's game.
     * @param gameName - The name of the game.
     */
	public void setGameName(String gameName) { this.gameName = gameName; }
	
	// Sets the board reference. This is for testing purposes only!
	public void setBoard(GoBoard board){ this.goBoard = board.copy(); }

    /**
     * Returns this player's color.
     */
	public StoneColor getPlayerColor() { return playerColor; }

	/**
	 * Sets the player color. This should only be called once when launcher is setting up a game.
	 * @param playerColor - The color to set to.
	 */
	public void setPlayerColor(StoneColor playerColor) {
		this.playerColor = playerColor;
	}
}
