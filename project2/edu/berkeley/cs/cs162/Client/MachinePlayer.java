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

    private void connectTo(String address, Integer port) {
        try {
            Socket c1 = new Socket(address, port);
            Socket c2 = new Socket(address, port);

            ServerConnection con = new ServerConnection(c1, c2);
            System.out.println(con.initiate3WayHandshake(new Random()));
            Message connectMessage = MessageFactory.createConnectMessage(clientInfo);

            Message ok = con.sendSyncToServer(connectMessage);

            if (ok.getMsgType() == MessageProtocol.OP_STATUS_OK) {
                System.out.println("Status OK, connected");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

    public void handleMessage(Message m) {
        GameInfo gameInfo;
        BoardInfo boardInfo;
        ClientInfo blackPlayerInfo;
        ClientInfo whitePlayerInfo;
        ClientInfo playerInfo;
        byte moveType;
        Location loc;
        WritableList locList;
        double blackPlayerScore;
        double whitePlayerScore;
        ClientInfo winner;
        byte reason;

        ClientInfo errorPlayer;
        String errorMsg;

        switch (m.getMsgType()) {
            case MessageProtocol.OP_TYPE_GAMESTART:
                ServerMessages.GameStartMessage gsm = (ServerMessages.GameStartMessage) m;

                gameInfo = gsm.getGameInfo();
                boardInfo = gsm.getBoardInfo();
                blackPlayerInfo = gsm.getBlackClientInfo();
                whitePlayerInfo = gsm.getWhiteClientInfo();

                break;
            case MessageProtocol.OP_TYPE_GAMEOVER:
                ServerMessages.GameOverMessage gom = (ServerMessages.GameOverMessage) m;

                gameInfo = gom.getGameInfo();
                blackPlayerScore = gom.getBlackScore();
                whitePlayerScore = gom.getWhiteScore();
                winner = gom.getWinner();
                reason = gom.getReason();

                errorPlayer = gom.getErrorPlayer();
                errorMsg = gom.getErrorMessage();

                break;
            case MessageProtocol.OP_TYPE_MAKEMOVE:
                ServerMessages.MakeMoveMessage mmm = (ServerMessages.MakeMoveMessage) m;

                gameInfo = mmm.getGameInfo();
                playerInfo = mmm.getPlayer();
                moveType = mmm.getMoveType();
                loc = mmm.getLocation();
                locList = mmm.getLocationList();

                break;
            case MessageProtocol.OP_TYPE_GETMOVE:
                //send a message to the server with byte moveType and Location loc
                break;
            default:
                break;
        }
    }
}
