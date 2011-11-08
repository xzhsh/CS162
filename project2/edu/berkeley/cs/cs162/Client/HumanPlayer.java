package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.StoneColor;
import edu.berkeley.cs.cs162.Writable.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
    }

    public static void main(String[] args) {
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        HumanPlayer player = new HumanPlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if (player.connectTo(address, port)) {
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
            //...not sure this is actually necessary if handleMessage actually does handle all the messages -jay
            /*if (waitingForGames) {

            } else {

            }*/

            handleMessage(connection.readFromServer());
        }
    }

    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackPlayerName = m.getBlackClientInfo().getName();
        String whitePlayerName = m.getWhiteClientInfo().getName();

        this.gameName = gameName;
        board = m.getBoardInfo().getBoard();
        waitingForGames = false;

        if (blackPlayerName.equals(name)) {
            currentColor = StoneColor.BLACK;
            opponentColor = StoneColor.WHITE;
        } else if (whitePlayerName.equals(name)) {
            currentColor = StoneColor.WHITE;
            opponentColor = StoneColor.BLACK;
        } else {
            currentColor = StoneColor.NONE;
            opponentColor = StoneColor.NONE;
        }

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

        if (reason != MessageProtocol.GAME_OK) {
            String errorPlayerName = m.getErrorPlayer().getName();
            String errorMsg = m.getErrorMessage();
            System.out.println("Game " + gameName + " ended with an error by " + errorPlayerName + ": " + errorMsg + ". Black score " + blackPlayerScore + ", White score " + whitePlayerScore + ". WINNER: " + winner + "!");
        } else {
            System.out.println("Game " + gameName + " ended with Black score " + blackPlayerScore + ", White score " + whitePlayerScore + ". WINNER: " + winner + "!");
        }

        //destructors?
        waitingForGames = true;

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());

        //i think this is correct -jay
        connection.sendSyncToServer(MessageFactory.createWaitForGameMessage());
    }

    @Override
    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        //String gameName = m.getGameInfo().getName();
        String playerName = m.getPlayer().getName();
        byte type = m.getMoveType();
        BoardLocation loc = m.getLocation().makeBoardLocation();
        //WritableList stonesCaptured = m.getLocationList();

        if (playerName.equals(name)) {
            if (type == MessageProtocol.MOVE_PASS) {
                //what
            } else {
                try {
                    board.makeMove(loc, currentColor);
                } catch (GoBoard.IllegalMoveException e) {

                }
            }

        } else {
            if (type == MessageProtocol.MOVE_STONE) {
                //what
            } else {
                try {
                    board.makeMove(loc, opponentColor);
                } catch (GoBoard.IllegalMoveException e) {

                }
            }
        }

        //board.makeMove(loc.makeBoardLocation(),);

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    @Override
    protected void handleGetMove() throws IOException {

//    	ServerMessages.MakeMoveMessage moveMsg = (ServerMessages.MakeMoveMessage) m;
//    	GameInfo gInfo  = moveMsg.getGameInfo();

        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        //doesn't account for time?
        /*String humanInput = reader.readLine();

        if (humanInput.equals("pass")) {
            byte moveCode = MessageProtocol.MOVE_PASS;
            Location loc = MessageFactory.createLocationInfo(0, 0);
            Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, loc);
            connection.sendReplyToServer(getMoveResp);
        }
        //send message to worker thread that player has passed

        else {
            byte moveCode = MessageProtocol.MOVE_STONE;
            String[] outCoordinates = humanInput.split(" ");
            int xCoord = Integer.parseInt(outCoordinates[0]);
            int yCoord = Integer.parseInt(outCoordinates[1]);
            Location loc = MessageFactory.createLocationInfo(xCoord, yCoord);
            Message getMoveResp = MessageFactory.createGetMoveStatusOkMessage(moveCode, loc);

            connection.sendReplyToServer(getMoveResp);
            //send message to server about new move
        }*/
        //send a message to the server with byte moveType and Location loc

        byte moveType;
        Location loc;

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
