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
        		}
        	}
        	while (true) {
        		player2 = server.getNextWaitingPlayer();
        		if(player2.startGame()) {
        			break;
        		}
        	}
            Game game = new Game(player1.getWorker().getName() + "VS" + player2.getWorker().getName(), player1.getWorker(), player2.getWorker(), 10);
            player1.beginGame(game);
            player2.beginGame(game);
        }
    }

}
