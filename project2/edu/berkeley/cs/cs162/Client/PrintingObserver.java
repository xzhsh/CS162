package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class PrintingObserver extends Observer {

    public PrintingObserver(String name) {
        super(name, MessageProtocol.TYPE_OBSERVER);
    }

    public static void main(String[] args) {

        PrintingObserver observer;
        String address;
        Integer port;

        try {
            observer = new PrintingObserver(args[2]);
            address = args[0];
            port = Integer.valueOf(args[1]);
        }
        catch (Exception e) {
            System.out.println("Enter arguments in the following format: <host> <port> <observername>");
            return;
        }

        if (observer.connectTo(address, port)) {

            System.out.println("PrintingObserver connected to remote server.");
            while(true){
                try {

                    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.println("Push any key to join games, or Q to quit.");
                    String input = reader.readLine();

                    if(input.equals("Q") || input.equals("q")){
                        System.out.println("Disconnecting.");
                        observer.disconnect();
                        return;
                    }

                    System.out.println("Joining games...");
                    while(!observer.joinGames()){
                        System.out.println("Could not connect to any games. Push any key to try again, or Q to quit.");
                        input = reader.readLine();
                        if(input.equals("Q") || input.equals("q")){
                            System.out.println("Disconnecting.");
                            observer.disconnect();
                            return;
                        }
                    }
                    observer.runExecutionLoop();
                } catch (IOException e) {
                    System.out.println("An error occurred... PrintingObserver " + observer.getName() + " terminating.");
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean joinGames() throws IOException {
        Message listResponse = getConnection().sendSyncToServer(MessageFactory.createListGamesMessage());
        if (listResponse.isOK()) {
            WritableList gameList = ((ResponseMessages.ListGamesStatusOkMessage) listResponse).getGameList();
            
            for (Writable game : gameList) {
                GameInfo g = (GameInfo) game;
            	System.out.println("Found game" + g.getName());
                Message joinResponse = getConnection().sendSyncToServer(MessageFactory.createJoinMessage(g));
                if (joinResponse.isOK()) {
                    System.out.println("Joining game " + g.getName());
                    joinedGames++;
                }
            }
        }

        return joinedGames > 0;
    }

    protected void runExecutionLoop() throws IOException {
        super.runExecutionLoop();
        System.out.println("All joined games have ended.");
    }
    
    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackName = m.getBlackClientInfo().getName();
        String whiteName = m.getWhiteClientInfo().getName();

        System.out.println("Game " + gameName + " starting with Black player " + blackName + " and White player " + whiteName + ".");
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
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
        joinedGames--;
        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
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

        getConnection().sendReplyToServer(MessageFactory.createStatusOkMessage());
    }
}
