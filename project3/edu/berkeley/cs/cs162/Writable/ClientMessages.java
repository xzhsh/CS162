package edu.berkeley.cs.cs162.Writable;

/**
 * Message classes for client to server messages.
 *
 * @author xshi
 */
public class ClientMessages {

    /**
     * Register
     */
    public static class RegisterMessage extends GenericMessage {

    	// Used for receiving
        protected RegisterMessage() {
            this(new ClientInfo(), "");
        }

        // Used for sending
        protected RegisterMessage(ClientInfo player, String passwordHash) {
            super(MessageProtocol.OP_TYPE_REGISTER, player, MessageFactory.createWritableString(passwordHash));
        }

        public ClientInfo getClientInfo() {
            return (ClientInfo) super.getWritable(0);
        }
        public String getPasswordHash() {
            return ((WritableString) super.getWritable(1)).getValue();
        }
	}

    /**
     * Change Password
     */
	public static class ChangePasswordMessage extends GenericMessage{

    	// Used for receiving
        protected ChangePasswordMessage() {
            this(new ClientInfo(), "");
        }

        // Used for sending
        protected ChangePasswordMessage(ClientInfo player, String passwordHash) {
            super(MessageProtocol.OP_TYPE_CHANGEPW, player, MessageFactory.createWritableString(passwordHash));
        }

        public ClientInfo getClientInfo() {
            return (ClientInfo) super.getWritable(0);
        }

        public String getPasswordHash() {
            return ((WritableString) super.getWritable(1)).getValue();
        }
	}

	/**
     * Connect
     */
    public static class ConnectMessage extends GenericMessage {

        // Used for receiving
        protected ConnectMessage() {
            this(new ClientInfo(), "");
        }

        // Used for sending
        protected ConnectMessage(ClientInfo player, String passwordHash) {
            super(MessageProtocol.OP_TYPE_CONNECT, player, MessageFactory.createWritableString(passwordHash));
        }

        public ClientInfo getClientInfo() {
            return (ClientInfo) super.getWritable(0);
        }

        public String getPasswordHash() {
            return ((WritableString) super.getWritable(1)).getValue();
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
