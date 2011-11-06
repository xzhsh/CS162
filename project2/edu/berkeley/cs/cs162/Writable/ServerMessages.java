package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.util.List;

/**
 * Messages from the server to client.
 *
 * @author xshi
 */
public class ServerMessages {

    /**
     * GameStart
     */
    public static class GameStartMessage extends GenericMessage {

        // Used for receiving
        protected GameStartMessage() {
            super(MessageProtocol.OP_TYPE_GAMESTART, new GameInfo(), new BoardInfo(), new ClientInfo(), new ClientInfo());
        }

        // Used for sending
        protected GameStartMessage(GameInfo game, BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
            super(MessageProtocol.OP_TYPE_GAMESTART, game, board, blackPlayer, whitePlayer);
        }

        public GameInfo getGameInfo() {
            return (GameInfo) getWritable(0);
        }

        public BoardInfo getBoardInfo() {
            return (BoardInfo) getWritable(1);
        }

        public ClientInfo getBlackClientInfo() {
            return (ClientInfo) getWritable(2);
        }

        public ClientInfo getWhiteClientInfo() {
            return (ClientInfo) getWritable(3);
        }
    }

    /**
     * GameOver
     */
    public static class GameOverMessage extends GenericMessage {

        // Used for receiving
        protected GameOverMessage() {
            super(MessageProtocol.OP_TYPE_GAMEOVER, new GameInfo(), new WritableDouble(), new WritableDouble(), new ClientInfo(), new WritableByte());
        }

        // Used for sending
        protected GameOverMessage(GameInfo game, WritableDouble blackScore, WritableDouble whiteScore, ClientInfo winner, WritableByte reason){
            super(MessageProtocol.OP_TYPE_GAMEOVER, game, blackScore, whiteScore, winner, reason);
        }

        // Used for sending with error
        protected GameOverMessage(GameInfo game, WritableDouble blackScore, WritableDouble whiteScore, ClientInfo winner, WritableByte reason, ClientInfo player, WritableString errormsg){
            super(MessageProtocol.OP_TYPE_GAMEOVER, game, blackScore, whiteScore, winner, reason, player, errormsg);
        }

        // TODO This won't work, as the messages are always of type Message. Find a way around this. Possibly add in GenericMessage? Bad practice...
        @Override
        public void readDataFrom(InputStream in) throws IOException {
            // First, read the standard message in
            super.readDataFrom(in);

            // If the game ended due to error...
            if(getReason() != MessageProtocol.GAME_OK){
                writables.add(new ClientInfo());
                getWritable(5).readFrom(in);
                writables.add(new WritableString());
                getWritable(6).readFrom(in);
            }
        }

        public GameInfo getGameInfo(){
            return (GameInfo) getWritable(0);
        }

        public double getBlackScore(){
            return ((WritableDouble) getWritable(1)).getValue();
        }

        public double getWhiteScore(){
            return ((WritableDouble) getWritable(2)).getValue();
        }

        public ClientInfo getWinner(){
            return (ClientInfo) getWritable(3);
        }

        public byte getReason(){
            return ((WritableByte) getWritable(4)).getValue();
        }

        public ClientInfo getErrorPlayer(){
            if(getReason() != MessageProtocol.GAME_OK)
                return (ClientInfo) getWritable(5);

            return null;
        }

        public String getErrorMessage(){
            if(getReason() != MessageProtocol.GAME_OK)
                return ((WritableString) getWritable(6)).getValue();

            return null;
        }
    }

    /**
     * MakeMove
     */
    public static class MakeMoveMessage extends GenericMessage {

        // Used for receiving
        protected MakeMoveMessage() {
            super(MessageProtocol.OP_TYPE_MAKEMOVE, new GameInfo(), new ClientInfo(), new WritableByte(), new Location(), new WritableLocationList());
        }

        // Used for sending
        protected MakeMoveMessage(GameInfo game, ClientInfo player, byte moveType, Location loc, List<Location> locationlist) {
            super(MessageProtocol.OP_TYPE_MAKEMOVE, game, player, new WritableByte(moveType), loc, new WritableLocationList(locationlist));
        }

        public GameInfo getGameInfo(){
            return (GameInfo) getWritable(0);
        }

        public ClientInfo getPlayer(){
            return (ClientInfo) getWritable(1);
        }

        public byte getMoveType(){
            return ((WritableByte) getWritable(2)).getValue();
        }

        public Location getLocation(){
            return (Location) getWritable(3);
        }

        //TODO [LIST] return the list of locations.
    }

    /**
     * GetMove
     *
     * This class is required to check for a special STATUS_OK message.
     */
    public static class GetMoveMessage extends OpCodeOnlyMessage {
        protected GetMoveMessage() {
            super(MessageProtocol.OP_TYPE_GETMOVE);
        }
    }
}
