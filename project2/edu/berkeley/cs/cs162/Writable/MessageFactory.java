package edu.berkeley.cs.cs162.Writable;

import edu.berkeley.cs.cs162.Client.Client;
import edu.berkeley.cs.cs162.Server.Board;
import sun.plugin2.main.client.MessagePassingOneWayJSObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * Factory class for Message.
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

    // TODO Have methods to create special STATUS_OK messages
    public static Message createStatusOkMessage(Writable... writables) {
        return new GenericMessage(MessageProtocol.OP_STATUS_OK, writables);
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
