package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

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
	public static Message createStatusOkMessage() {
		return new OpCodeOnlyMessage(MessageProtocol.OP_STATUS_OK);
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

    // SERVER MESSAGES

    public static Message createGameStartMessage(GameInfo game, BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer){
        return new ServerMessages.GameStartMessage(game, board, blackPlayer, whitePlayer);
    }

    public static Message createMakeMoveMessage(GameInfo game, ClientInfo player, byte moveType, Location loc, List<Location> locationlist){
        return new ServerMessages.MakeMoveMessage(game, player, moveType, loc, locationlist);
    }

    public static Message createGetMoveMessage(){
        return new ServerMessages.GetMoveMessage();
    }
}
