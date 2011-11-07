package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class MachinePlayer extends Player {

    public MachinePlayer(String name) {
        super(name, MessageProtocol.TYPE_MACHINE);
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

    public void handleMessage(Message m) throws IOException {
        switch (m.getMsgType()) {
            case MessageProtocol.OP_TYPE_GAMESTART:
                handleGameStart((ServerMessages.GameStartMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GAMEOVER:
                handleGameOver((ServerMessages.GameOverMessage) m);
                break;
            case MessageProtocol.OP_TYPE_MAKEMOVE:
                handleMakeMove((ServerMessages.MakeMoveMessage) m);
                break;
            case MessageProtocol.OP_TYPE_GETMOVE:
                handleGetMove();
                break;
            default:
                break;
        }
    }

    private void handleGameStart(ServerMessages.GameStartMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        String blackName = m.getBlackClientInfo().getName();
        String whiteName = m.getWhiteClientInfo().getName();


    }

    private void handleGameOver(ServerMessages.GameOverMessage m) throws IOException {
        String gameName = m.getGameInfo().getName();
        double blackScore = m.getBlackScore();
        double whiteScore = m.getWhiteScore();
        String winner = m.getWinner().getName();
        byte reason = m.getReason();


    }

    private void handleMakeMove(ServerMessages.MakeMoveMessage m) throws IOException {
        String game = m.getGameInfo().getName();
        String player = m.getPlayer().getName();
        byte type = m.getMoveType();
        Location loc = m.getLocation();
        WritableList stonesCaptured = m.getLocationList();
    }

    private void handleGetMove() throws IOException {
        //send a message to the server with byte moveType and Location loc
    }
}
