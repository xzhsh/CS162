package edu.berkeley.cs.cs162.Writable;

import com.sun.tools.internal.ws.processor.model.Response;
import edu.berkeley.cs.cs162.Client.Client;
import edu.berkeley.cs.cs162.Server.Board;
import sun.plugin2.main.client.MessagePassingOneWayJSObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

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


    // Read an incoming message from a Client.
    public static Message readClientMessage(InputStream in){
        return null;
    }


    // Read an incoming message from the Server.
    public static Message readServerMessage(InputStream in){
        return null;
    }

    // Read a response message.
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
                        container = new OpCodeOnlyMessage(MessageProtocol.OP_STATUS_OK); // TODO Handle the list and change this appropriately.
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

        // Fill out the container from the InputStream.
        container.readDataFrom(in);
        return container;
    }

    /**
     * Client Messages.
     */

    public static Message createConnectMessage(ClientInfo cInfo) {
        return new ClientMessages.ConnectMessage(cInfo);
    }

    public static Message createDisconnectMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_DISCONNECT);
    }

    public static Message createWaitForGameMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_WAITFORGAME);
    }

    public static Message createListGamesMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_TYPE_LISTGAMES);
    }

    public static Message createJoinMessage(GameInfo gInfo) {
        return new ClientMessages.JoinMessage(gInfo);
    }

    public static Message createLeaveMessage(GameInfo gInfo) {
        return new ClientMessages.LeaveMessage(gInfo);
    }


    /**
     * Server Messages.
     */

    public static Message createGameStartMessage(GameInfo game, BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
        return new ServerMessages.GameStartMessage(game, board, blackPlayer, whitePlayer);
    }

    public static Message createGameOverMessage(GameInfo game, double blackScore, double whiteScore, ClientInfo winner, byte reason) {
        return new ServerMessages.GameOverMessage(game, new WritableDouble(blackScore), new WritableDouble(whiteScore), winner, new WritableByte(reason));
    }

    public static Message createGameOverErrorMessage(GameInfo game, double blackScore, double whiteScore, ClientInfo winner, byte reason, ClientInfo player, String errormsg) {
        return new ServerMessages.GameOverMessage(game, new WritableDouble(blackScore), new WritableDouble(whiteScore), winner, new WritableByte(reason), player, new WritableString(errormsg));
    }

    public static Message createMakeMoveMessage(GameInfo game, ClientInfo player, byte moveType, Location loc, List<Location> locationlist) {
        return new ServerMessages.MakeMoveMessage(game, player, moveType, loc, locationlist);
    }

    public static Message createGetMoveMessage() {
        return new ServerMessages.GetMoveMessage();
    }


    /**
     * Response Messages.
     */


    public static Message createStatusOkMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_STATUS_OK);
    }

    // TODO Handle the list in the ListGamesStatusOkMessage and add a method here.

    public static Message createJoinStatusOkMessage(BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer) {
        return new ResponseMessages.JoinStatusOkMessage(board, blackPlayer, whitePlayer);
    }

    public static Message createGetMoveStatusOkMessage(byte moveType, Location loc) {
        return new ResponseMessages.GetMoveStatusOkMessage(moveType, loc);
    }

    public static Message createErrorRejectedMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_REJECTED);
    }

    public static Message createErrorUnconnectedMessage() {
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_UNCONNECTED);
    }

    public static Message createErrorInvalidGameMessage(){
        return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_INVALID_GAME);
    }

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
        BoardInfo boardInfo = new BoardInfo();
        for (int i = 0; i < currentBoard.getSize(); i++) {
            for (int j = 0; j < currentBoard.getSize(); j++) {
                boardInfo.getStoneColorState()[i][j] = new StoneColorInfo(currentBoard.getAtLocation(new edu.berkeley.cs.cs162.Server.Location(i, j)).getByte());
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

}
