package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Message classes for client to server messages.
 *
 * @author xshi
 */
public class ClientMessages {

    /**
     * Connect
     */
    public static class ConnectMessage extends GenericMessage {

        // Used for receiving
        protected ConnectMessage() {
            super(MessageProtocol.OP_TYPE_CONNECT, new ClientInfo());
        }

        // Used for sending
        protected ConnectMessage(ClientInfo player) {
            super(MessageProtocol.OP_TYPE_CONNECT, player);
        }

        public ClientInfo getClientInfo() {
            return (ClientInfo) super.getWritable(0);
        }
    }

    /**
     * Join
     */
    public static class JoinMessage extends GenericMessage {

        // Used for receiving
        protected JoinMessage() {
            super(MessageProtocol.OP_TYPE_JOIN, new GameInfo());
        }

        // Used for sending
        protected JoinMessage(GameInfo game) {
            super(MessageProtocol.OP_TYPE_JOIN, game);
        }

        public GameInfo getGameInfo() {
            return (GameInfo) super.getWritable(0);
        }
    }

    /**
     * Leave
     */
    public static class LeaveMessage extends GenericMessage {

        // Used for receiving
        protected LeaveMessage() {
            super(MessageProtocol.OP_TYPE_LEAVE, new GameInfo());
        }

        // Used for sending
        protected LeaveMessage(GameInfo game) {
            super(MessageProtocol.OP_TYPE_LEAVE, game);
        }

        public GameInfo getGameInfo() {
            return (GameInfo) super.getWritable(0);
        }
    }

    /**
     * List Games
     *
     * This class is required to check for special STATUS_OK messages
     */
    public static class ListGamesMessage extends OpCodeOnlyMessage {

        protected ListGamesMessage(){
            super(MessageProtocol.OP_TYPE_LISTGAMES);
        }
    }

    /**
     * Get Move
     *
     * This class is required to check for special STATUS_OK messages
     */
    public static class GetMoveMessage extends OpCodeOnlyMessage {

        protected GetMoveMessage(){
            super(MessageProtocol.OP_TYPE_GETMOVE);
        }
    }



    public static Message readFromInput(InputStream in) throws IOException {
        byte opCode = DataTypeIO.readByte(in);
        Message msgContainer = null;
        switch (opCode) {
            case MessageProtocol.OP_TYPE_CONNECT:
                msgContainer = new ConnectMessage();
                break;
            case MessageProtocol.OP_TYPE_DISCONNECT:
            case MessageProtocol.OP_TYPE_WAITFORGAME:
            case MessageProtocol.OP_TYPE_LISTGAMES:
                msgContainer = new OpCodeOnlyMessage(opCode);
                break;
            case MessageProtocol.OP_TYPE_JOIN:
                msgContainer = new JoinMessage();
                break;
            case MessageProtocol.OP_TYPE_LEAVE:
                msgContainer = new LeaveMessage();
                break;
            default:
                assert false : "Unimplemented method";
        }
        msgContainer.readDataFrom(in);
        return msgContainer;
    }

}
