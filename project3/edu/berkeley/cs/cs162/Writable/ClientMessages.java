package edu.berkeley.cs.cs162.Writable;


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
}
