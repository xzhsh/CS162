package edu.berkeley.cs.cs162.Writable;


public class ResponseMessages {

    // TODO [LIST] Assert that the list contains GameInfo
    public static class ListGamesStatusOkMessage extends GenericMessage {

        // Used for receiving
        protected ListGamesStatusOkMessage(){
            super(MessageProtocol.OP_STATUS_OK, new WritableList(GameInfo.class));
        }

        // Used for sending
        protected ListGamesStatusOkMessage(WritableList gameList) {
            super(MessageProtocol.OP_STATUS_OK, gameList);
        }

        public WritableList getGameList() {
            return (WritableList) getWritable(0);
        }
    }

    public static class JoinStatusOkMessage extends GenericMessage {

        // Used for receiving
        protected JoinStatusOkMessage() {
            super(MessageProtocol.OP_STATUS_OK, new BoardInfo(), new ClientInfo(), new ClientInfo());
        }

        // Used for sending
        protected JoinStatusOkMessage(BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
            super(MessageProtocol.OP_STATUS_OK, board, blackPlayer, whitePlayer);
        }

        public BoardInfo getBoardInfo() {
            return (BoardInfo) getWritable(0);
        }

        public ClientInfo getBlackPlayer() {
            return (ClientInfo) getWritable(1);
        }

        public ClientInfo getWhitePlayer() {
            return (ClientInfo) getWritable(2);
        }
    }

    public static class GetMoveStatusOkMessage extends GenericMessage {

        // Used for receiving
        protected GetMoveStatusOkMessage() {
            super(MessageProtocol.OP_STATUS_OK, new WritableByte(), new Location());
        }

        // Used for sending
        protected GetMoveStatusOkMessage(byte moveType, Location loc) {
            super(MessageProtocol.OP_STATUS_OK, new WritableByte(moveType), loc);
        }

        public byte getMoveType() {
            return ((WritableByte) getWritable(0)).getValue();
        }

        public Location getLocation() {
            return (Location) getWritable(1);
        }
    }
}
