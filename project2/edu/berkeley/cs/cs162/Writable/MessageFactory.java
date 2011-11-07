package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.Game;


/**
 * Factory class for Message. This class will be used for all Message construction.
 */

public class MessageFactory {
    /**
     * Reads an opcode from the input and returns it as an byte.
     *
     * @param input the InputStream to read from.
     * @throws IOException
     */
    public static byte readOpCodeFrom(InputStream input) throws IOException {
        return (byte) input.read();
    }

    public static Message createGenericOpCodeOnlyMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.UNUSED);
    }


    /**
     * The following methods are used to read
     * Messages in via InputStreams and fill
     * them out.
     */

    // Client Messages
    public static Message readClientMessage(InputStream in) throws IOException {
        byte opCode = DataTypeIO.readByte(in);
        Message msgContainer = null;

        // Create the correct message container based on the opcode
        switch (opCode) {
            case MessageProtocol.OP_TYPE_CONNECT:
                msgContainer = new ClientMessages.ConnectMessage();
                break;
            case MessageProtocol.OP_TYPE_DISCONNECT:
            case MessageProtocol.OP_TYPE_WAITFORGAME:
            case MessageProtocol.OP_TYPE_LISTGAMES:
                msgContainer = new OpCodeOnlyMessage(opCode);
                break;
            case MessageProtocol.OP_TYPE_JOIN:
                msgContainer = new ClientMessages.JoinMessage();
                break;
            case MessageProtocol.OP_TYPE_LEAVE:
                msgContainer = new ClientMessages.LeaveMessage();
                break;
            default:
                assert false : "Unimplemented method";
        }

        // Fill out the container via the InputStream
        msgContainer.readDataFrom(in);
        return msgContainer;
    }


    // Server Messages
    public static Message readServerMessage(InputStream in) throws IOException {
        byte opCode = DataTypeIO.readByte(in);
        Message container = null;

        // Create the correct message container based on the opcode
        switch (opCode) {
            case MessageProtocol.OP_TYPE_GAMESTART:
                container = new ServerMessages.GameStartMessage();
                break;
            case MessageProtocol.OP_TYPE_GAMEOVER:
                container = new ServerMessages.GameOverMessage();
                break;
            case MessageProtocol.OP_TYPE_MAKEMOVE:
                container = new ServerMessages.MakeMoveMessage();
                break;
            case MessageProtocol.OP_TYPE_GETMOVE:
                container = new OpCodeOnlyMessage(opCode);
                break;
            default:
                assert false : "Unimplemented method";
        }

        // Fill out the container via the InputStream
        container.readDataFrom(in);
        return container;
    }

    // Response Messages
    public static Message readResponseMessage(InputStream in, Message sentMessage) throws IOException {
        byte opCode = DataTypeIO.readByte(in);
        Message container = null;

        // Create the correct type of Message container, or return an OpCodeOnlyMessage.
        switch (opCode) {
            case MessageProtocol.OP_ERROR_INVALID_GAME:
            case MessageProtocol.OP_ERROR_INVALID_USER:
            case MessageProtocol.OP_ERROR_REJECTED:
            case MessageProtocol.OP_ERROR_UNCONNECTED:
                return new OpCodeOnlyMessage(opCode);
            case MessageProtocol.OP_STATUS_OK:
                switch (sentMessage.getMsgType())
                {
                    case MessageProtocol.OP_TYPE_LISTGAMES:
                        container = new ResponseMessages.ListGamesStatusOkMessage();
                        break;
                    case MessageProtocol.OP_TYPE_JOIN:
                        container = new ResponseMessages.JoinStatusOkMessage();
                        break;
                    case MessageProtocol.OP_TYPE_GETMOVE:
                        container = new ResponseMessages.GetMoveStatusOkMessage();
                        break;
                    default:
                        return new OpCodeOnlyMessage(opCode);
                } break;
            default:
                assert false : "Unimplemented method";
        }

        // Fill out the container via the InputStream.
        container.readDataFrom(in);
        return container;
    }


    /**
     * The following methods are used to
     * construct the various method types
     * to send over an OutputStream.
     */

    /* Client Messages */

    // Connect
    public static Message createConnectMessage(ClientInfo cInfo) {
        return new ClientMessages.ConnectMessage(cInfo);
    }

    // Disconnect
    public static Message createDisconnectMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_DISCONNECT);
    }

    // Wait for Game
    public static Message createWaitForGameMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_WAITFORGAME);
    }

    // List Games
    public static Message createListGamesMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_LISTGAMES);
    }

    // Join
    public static Message createJoinMessage(GameInfo gInfo) {
        return new ClientMessages.JoinMessage(gInfo);
    }

    // Leave
    public static Message createLeaveMessage(GameInfo gInfo) {
        return new ClientMessages.LeaveMessage(gInfo);
    }


    /* Server Messages */

    // Game Start
    public static Message createGameStartMessage(GameInfo game, BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
        return new ServerMessages.GameStartMessage(game, board, blackPlayer, whitePlayer);
    }
	public static Message createGameStartMessage(Game game) {
		return createGameStartMessage(game.makeGameInfo(), game.makeBoardInfo(),game.getBlackPlayer().makeClientInfo(), game.getWhitePlayer().makeClientInfo());
	}
    
    // Game Over
    public static Message createGameOverMessage(GameInfo game, double blackScore, double whiteScore, ClientInfo winner) {
        return new ServerMessages.GameOverMessage(game, new WritableDouble(blackScore), new WritableDouble(whiteScore), winner, new WritableByte(MessageProtocol.GAME_OK));
    }

    // Game Over (Error)
    public static Message createGameOverErrorMessage(GameInfo game, double blackScore, double whiteScore, ClientInfo winner, byte reason, ClientInfo player, String errormsg) {
        return new ServerMessages.GameOverMessage(game, new WritableDouble(blackScore), new WritableDouble(whiteScore), winner, new WritableByte(reason), player, new WritableString(errormsg));
    }
    
    // Make Move
    public static Message createMakeMoveMessage(GameInfo game, ClientInfo player, byte moveType, BoardLocation loc, Collection<BoardLocation> locationList) {
        WritableList locInfoList = new WritableList(Location.class);
    	for (BoardLocation l : locationList)
    	{
    		locInfoList.add(new Location(l.getX(), l.getY()));
    	}
        return new ServerMessages.MakeMoveMessage(game, player, moveType, loc.makeLocationInfo(), locInfoList);
    }

    // Get Move
    public static Message createGetMoveMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_GETMOVE);
    }

    /* Response Messages */

    // Status OK
    public static Message createStatusOkMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_STATUS_OK);
    }

    // Status OK, response to List Games
    public static Message createListGamesStatusOkMessage(WritableList gameList) {
        assert gameList.getObjectType() == GameInfo.class : "WritableList should contain GameInfo objects";
        return new ResponseMessages.ListGamesStatusOkMessage(gameList);
    }

    // Status OK, response to Join
    public static Message createJoinStatusOkMessage(BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
        return new ResponseMessages.JoinStatusOkMessage(board, blackPlayer, whitePlayer);
    }

    // Status OK, response to Get Move
    public static Message createGetMoveStatusOkMessage(byte moveType, Location loc) {
        return new ResponseMessages.GetMoveStatusOkMessage(moveType, loc);
    }

    // Error (Rejected)
    public static Message createErrorRejectedMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_REJECTED);
    }

    // Error (Unconnected)
    public static Message createErrorUnconnectedMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_UNCONNECTED);
    }

    // Error (Invalid Game)
    public static Message createErrorInvalidGameMessage(){
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_INVALID_GAME);
    }

    // Error (Invalid User)
    public static Message createErrorInvalidUserMessage(){
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_INVALID_USER);
    }


    /**
     * Client Info.
     */

    public static ClientInfo createHumanPlayerClientInfo(String name) {
        return new ClientInfo(name, MessageProtocol.TYPE_HUMAN);
    }

    public static ClientInfo createMachinePlayerClientInfo(String name) {
        return new ClientInfo(name, MessageProtocol.TYPE_MACHINE);
    }

    public static ClientInfo createObserverClientInfo(String name) {
        return new ClientInfo(name, MessageProtocol.TYPE_OBSERVER);
    }


    /**
     * StoneColor Info.
     */

    public static StoneColorInfo createStoneColorInfo(byte color) {
        return new StoneColorInfo(color);
    }

    /**
     * Board Info.
     */

    public static BoardInfo createBoardInfo(Board currentBoard) {
        BoardInfo boardInfo = new BoardInfo(currentBoard.getSize());
        for (int i = 0; i < currentBoard.getSize(); i++) {
            for (int j = 0; j < currentBoard.getSize(); j++) {
                boardInfo.getStoneColorState()[i][j] = new StoneColorInfo(currentBoard.getAtLocation(new BoardLocation(i, j)).getByte());
            }
        }
        return boardInfo;
    }

    public static WritableList createWritableList(Class<? extends Writable> storedClass, Writable... writables) {
        WritableList list = new WritableList(storedClass);
        for (Writable e : writables) {
            list.add(e);
        }
        return list;
    }

    public static WritableList createWritableListFromCollection(Class<? extends Writable> storedClass, Collection<? extends Writable> writables) {
        WritableList list = new WritableList(storedClass);
        for (Writable e : writables) {
            list.add(e);
        }
        return list;
    }

	public static Location createLocationInfo(int x, int y) {
		return new Location(x,y);
	}
	
	public static GameInfo createGameInfo(String name)
	{
		return new GameInfo(name);
	}

    /* Writable Wrappers... should only be used for testing purposes */

    public static WritableByte createWritableByte(byte b){
        return new WritableByte(b);
    }

    public static WritableDouble createWritableDouble(double d){
        return new WritableDouble(d);
    }

    public static WritableString createWritableString(String s){
        return new WritableString(s);
    }



}
