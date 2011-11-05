package edu.berkeley.cs.cs162.Server;

public class MatchMakingWorker implements Runnable {
    private GameServer server;

    public MatchMakingWorker(GameServer server) {
        this.server = server;
    }

    @Override
    public void run() {
        while (true) {
            Worker player1 = server.getNextWaitingPlayer();
            Worker player2 = server.getNextWaitingPlayer();
            Game game = new Game(player1.getName() + "VS" + player2.getName(), player1, player2, 10);
            player1.startGame(game);
            player2.startGame(game);
        }
    }

}
