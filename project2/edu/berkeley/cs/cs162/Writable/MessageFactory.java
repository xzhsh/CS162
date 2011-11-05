package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import edu.berkeley.cs.cs162.Server.Board;

/**
 * Factory class for Message.
 * 
 * Kunal, please use this class file to do most of the simpler messages so that it doesn't clog up our project. Thanks :D
 * @author xshi
 *
 */
public class MessageFactory {
	/**
     * Reads an opcode from the input and returns it as an byte.
     * @param input the InputStream to read from.
     * @throws IOException 
     */
	public static byte readOpCodeFrom(InputStream input) throws IOException {
		return (byte) input.read();
	}
	
	//TODO add create________Messsage(args) methods for all messages.
	public static Message createStatusOkMessage(Writable ... writables) {
		return new CompositeMessage(MessageProtocol.OP_STATUS_OK, writables);
	}

	public static Message createGenericOpCodeOnlyMessage()
	{
		return new OpCodeOnlyMessage(MessageProtocol.UNUSED);
	}

    // CLIENT INFO

	public static ClientInfo createHumanPlayerClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_HUMAN);
	}
	
	public static ClientInfo createMachinePlayerClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_MACHINE);
	}
	
	public static ClientInfo createObserverClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_OBSERVER);
	}

    // CLIENT MESSAGES

	public static Message createConnectMessage(ClientInfo cInfo) {
		return new ClientMessages.ConnectMessage(cInfo);
	}
	
	public static Message createJoinMessage(GameInfo gInfo) {
		return new ClientMessages.JoinMessage(gInfo);
	}
	
	public static Message createLeaveMessage(GameInfo gInfo) {
		return new ClientMessages.LeaveMessage(gInfo);
	}
	
    public static StoneColorInfo createStoneColorInfo(byte color) {
        return new StoneColorInfo(color);
    }

	public static Message createErrorUnconnectedMessage() {
		return new OpCodeOnlyMessage(MessageProtocol.OP_ERROR_UNCONNECTED);
	}

	public static BoardInfo createBoardInfo(Board currentBoard) {
		BoardInfo boardInfo = new BoardInfo();
		for (int i = 0; i < currentBoard.getSize(); i++)
		{
			for (int j = 0; j < currentBoard.getSize(); j++)
			{
				boardInfo.getStoneColorState()[i][j] = new StoneColorInfo(currentBoard.getAtLocation(new edu.berkeley.cs.cs162.Server.Location(i,j)).getByte());
			}
		}
		return boardInfo;
	}

	public static WritableList createWritableList(Class<? extends Writable> storedClass, Writable ... writables) {
		WritableList list = new WritableList(storedClass);
		for (Writable e : writables)
		{
			list.add(e);
		}
		return list;
	}
	public static WritableList createWritableListFromCollection(Class<? extends Writable> storedClass, Collection<? extends Writable> writables) {
		WritableList list = new WritableList(storedClass);
		for (Writable e : writables)
		{
			list.add(e);
		}
		return list;
	}
}
