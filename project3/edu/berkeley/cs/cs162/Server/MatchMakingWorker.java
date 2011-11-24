package edu.berkeley.cs.cs162.Server;

public class MatchMakingWorker implements Runnable {
    private GameServer server;

    public MatchMakingWorker(GameServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
        	PlayerLogic player1;
        	PlayerLogic player2;
        	while (true) {
        		player1 = server.getNextWaitingPlayer();
        		if(player1.startGame()) {
        			break;
        		} else 
        		{
        			System.out.println(player1.getName() + " is still playing");
        		}
        	}
        	while (true) {
        		player2 = server.getNextWaitingPlayer();
        		if(player2.startGame()) {
        			break;
        		} else {
        			System.out.println(player2.getName() + " is still playing");
        		}
        	}
            Game game = new Game(player1.getName() + "VS" + player2.getName(), player1, player2, 10);
            player1.beginGame(game);
            server.addGame(game);
        }
    }

}
