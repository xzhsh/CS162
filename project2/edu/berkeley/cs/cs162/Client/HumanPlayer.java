package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class HumanPlayer extends Player {

    public HumanPlayer(String name) {
        super(name, MessageProtocol.TYPE_HUMAN);
    }

    protected boolean connectTo(String address, Integer port) {
        try {
            Socket c1 = new Socket(address, port);
            Socket c2 = new Socket(address, port);

            ServerConnection con = new ServerConnection(c1, c2);
            System.out.println(con.initiate3WayHandshake(new Random()));
            Message connectMessage = MessageFactory.createConnectMessage(clientInfo);

            Message ok = con.sendSyncToServer(connectMessage);

            if (ok.getMsgType() == MessageProtocol.OP_STATUS_OK) {
                System.out.println("Status OK, connected");
                return true;
            }

            return false;

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
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
