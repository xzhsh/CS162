package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
    }

    public static void main(String[] args){
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        HumanPlayer player = new HumanPlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if (player.connectTo(address, port)){
            System.out.println("HumanPlayer " + player.getName() + " is connected to the server!");

            try {
                player.runExecutionLoop();
            } catch (IOException e) {
                System.out.println("An error occurred... HumanPlayer " + player.getName() + " terminating.");
            }
        }
    }

    private void runExecutionLoop() throws IOException {
        while (true) {
            if (waitingForGames) {

            } else {

            }

            handleMessage(connection.readFromServer());
        }
    }

    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackPlayerName = m.getBlackClientInfo().getName();
        String whitePlayerName = m.getWhiteClientInfo().getName();

        board = m.getBoardInfo().getBoard();
        waitingForGames = false;

        System.out.println("Game " + gameName + " starting with Black player " + blackPlayerName + " and White player " + whitePlayerName + ".");

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        double blackPlayerScore = m.getBlackScore();
        double whitePlayerScore = m.getWhiteScore();
        String winner = m.getWinner().getName();
        byte reason = m.getReason();
        String errorPlayerName = m.getErrorPlayer().getName();
        String errorMsg = m.getErrorMessage();

        waitingForGames = true;

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String player = m.getPlayer().getName();
        byte type = m.getMoveType();
        Location loc = m.getLocation();
        WritableList stonesCaptured = m.getLocationList();



        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleGetMove() throws IOException {
        //send a message to the server with byte moveType and Location loc

        byte moveType;
        Location loc;

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime >= 20000) {
            if (reader.ready()) {
                break;
            }
        }

        String input = reader.readLine();

        if (input.equals("pass")) {
            moveType = MessageProtocol.MOVE_PASS;
            loc = MessageFactory.createLocationInfo(0, 0);
        } else {
            moveType = MessageProtocol.MOVE_STONE;

            String[] coordinates = input.split(" ");
            loc = MessageFactory.createLocationInfo(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
        }

        Message m = MessageFactory.createGetMoveStatusOkMessage(moveType, loc);

        connection.sendReplyToServer(m);
    }
}
