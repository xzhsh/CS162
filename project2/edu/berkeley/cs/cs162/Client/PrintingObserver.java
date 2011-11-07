package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;

public class PrintingObserver extends Observer {

    public PrintingObserver(String name) {
        super(name, MessageProtocol.TYPE_OBSERVER);
    }

    public static void main(String[] args) {
        PrintingObserver observer = new PrintingObserver(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if (observer.connectTo(address, port)) {
            System.out.println("Printing observer connected, yo.");
            try {
                observer.joinGames();
                observer.runExecutionLoop();
            } catch (IOException e) {
                System.out.println("An error occurred... PrintingObserver " + observer.getName() + " terminating.");
            }
        }
    }

    private void joinGames() throws IOException {
        Message listResponse = connection.sendSyncToServer(MessageFactory.createListGamesMessage());
        if (listResponse.isOK()) {
            WritableList gameList = ((ResponseMessages.ListGamesStatusOkMessage) listResponse).getGameList();
            for (Writable game : gameList) {
                GameInfo g = (GameInfo) game;
                Message joinResponse = connection.sendSyncToServer(MessageFactory.createJoinMessage(g));
                if (joinResponse.isOK()) {
                    joinedGames.add(g);
                }
            }
        }
    }

    private void runExecutionLoop() throws IOException {
        while (true) {
            handleMessage(connection.readFromServer());
        }
    }

    public void handleMessage(Message m) throws IOException {
        switch (m.getMsgType()) {
            case MessageProtocol.OP_TYPE_GAMESTART:
            	System.out.println("Game start received");
                handleGameStart((ServerMessages.GameStartMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GAMEOVER:
            	System.out.println("Game over received");
                handleGameOver((ServerMessages.GameOverMessage) m);
                break;
            case MessageProtocol.OP_TYPE_MAKEMOVE:
            	System.out.println("Make move received");
                handleMakeMove((ServerMessages.MakeMoveMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GETMOVE:
                // This should never be called...
            	System.out.println("Get move received");
                break;
            default:
            	System.out.println("Weird shit received" + m.getMsgType());
                break;
        }
    }

    private void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackName = m.getBlackClientInfo().getName();
        String whiteName = m.getWhiteClientInfo().getName();

        System.out.println("Game " + gameName + " starting with Black player " + blackName + " and White player " + whiteName + ".");
        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    private void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        double blackScore = m.getBlackScore();
        double whiteScore = m.getWhiteScore();
        String winner = m.getWinner().getName();
        byte reason = m.getReason();

        if (reason != MessageProtocol.GAME_OK) {
            String playerName = m.getErrorPlayer().getName();
            String errorMsg = m.getErrorMessage();
            System.out.println("Game " + gameName + " ended with an error by " + playerName + ": " + errorMsg + ". Black score " + blackScore + ", White score " + whiteScore + ". WINNER: " + winner + "!");
        } else {
            System.out.println("Game " + gameName + " ended with Black score " + blackScore + ", White score " + whiteScore + ". WINNER: " + winner + "!");
        }

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());

    }

    private void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        String game = m.getGameInfo().getName();
        String player = m.getPlayer().getName();
        byte type = m.getMoveType();
        Location loc = m.getLocation();
        WritableList stonesCaptured = m.getLocationList();

        if (type == MessageProtocol.MOVE_STONE) {
            System.out.println("In game " + game + ", " + player + " placed a stone at " + loc + ". " + stonesCaptured.size() + " stones were captured.");
        } else {
            System.out.println("In game " + game + ", " + player + " passed.");
        }

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }
}
