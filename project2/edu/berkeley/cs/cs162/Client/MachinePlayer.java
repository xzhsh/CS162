package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.GoBoard;
import edu.berkeley.cs.cs162.Server.StoneColor;
import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.util.Random;

public class MachinePlayer extends Player {

    public MachinePlayer(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
    }

    public static void main(String[] args) {
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        MachinePlayer player = new MachinePlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if (player.connectTo(address, port)) {
            System.out.println("MachinePlayer " + player.getName() + " is connected to the server!");
            try {
                player.runExecutionLoop();
            } catch (IOException e) {
                System.out.println("An error occurred... MachinePlayer " + player.getName() + " terminating.");
            }
        }
    }

    private void runExecutionLoop() throws IOException {
        while (true) {
            //...not sure this is actually necessary if handleMessage actually does handle all the messages -jay
            if (waitingForGames && !sentWFGMessage) {
                Message reply = connection.sendSyncToServer(MessageFactory.createWaitForGameMessage());

                if (reply.isOK()) {
                    sentWFGMessage = true;
                } else {
                    //terminate? what happens when reply is not okay
                    break;
                }
            } else {
                handleMessage(connection.readFromServer());
            }
        }

        //end of program destructor
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

        //game destructors
        waitingForGames = true;
        sentWFGMessage = false;

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
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
                //uhh
            } else {
                try {
                    board.makeMove(loc, currentColor);
                } catch (GoBoard.IllegalMoveException e) {

                }

            }
        } else {
            if (type == MessageProtocol.MOVE_PASS) {
                //uhh
            } else {
                try {
                    board.makeMove(loc, opponentColor);
                } catch (GoBoard.IllegalMoveException e) {

                }
            }
        }

        connection.sendReplyToServer(MessageFactory.createStatusOkMessage());
    }

    private Location decideMove() {
        Random rng = new Random();
        int size = board.getCurrentBoard().getSize();
        BoardLocation loc = new BoardLocation(rng.nextInt(size), rng.nextInt(size));
        int chanceOfPass = 0;
        //Vector<Location> invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);

        boolean valid = false;
        while (!valid) {

            //adds .5% of pass per try
            chanceOfPass += 5;

            if (chanceOfPass >= 10000 || rng.nextInt(10000 - chanceOfPass) == 0) {
                return null;
            }

            loc = new BoardLocation(rng.nextInt(size), rng.nextInt(size));
            //invalidatedLocations = Rules.getCapturedStones(goBoard.board, getPlayerColor(), loc);

            try {
                board.testMove(loc, currentColor);
                valid = true;
            } catch (GoBoard.IllegalMoveException e) {

            }
        }

        return MessageFactory.createLocationInfo(loc.getX(), loc.getY());
    }

    @Override
    protected void handleGetMove() throws IOException {
        long startTime = System.currentTimeMillis();
        int moveTime = 2000;

        byte moveCode;

        while (true) {

            //not entirely sure this times out correctly
            if (System.currentTimeMillis() - startTime >= moveTime) {
                return;
            }

            Location loc = decideMove();

            if (loc == null) {
                moveCode = MessageProtocol.MOVE_PASS;
                loc = MessageFactory.createLocationInfo(0, 0);
            } else {
                moveCode = MessageProtocol.MOVE_STONE;
            }

            connection.sendReplyToServer(MessageFactory.createGetMoveStatusOkMessage(moveCode, loc));
        }
    }
}
