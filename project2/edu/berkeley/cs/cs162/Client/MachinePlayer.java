package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MachinePlayer extends Player {

    public MachinePlayer(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
    }

    public static void main(String[] args){
        assert args.length == 3 : "Enter arguments in the following format: <host> <port> <playername>";
        MachinePlayer player = new MachinePlayer(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if(player.connectTo(address, port)){
            System.out.println("MachinePlayer " + player.getName() + " is connected to the server!");
        }
    }

    @Override
    protected void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackPlayerName = m.getBlackClientInfo().getName();
        String whitePlayerName = m.getWhiteClientInfo().getName();


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

    }

    @Override
    protected void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String player = m.getPlayer().getName();
        byte type = m.getMoveType();
        Location loc = m.getLocation();
        WritableList stonesCaptured = m.getLocationList();

    }

    @Override
    protected void handleGetMove() throws IOException {
        //send a message to the server with byte moveType and Location loc
    }
}
