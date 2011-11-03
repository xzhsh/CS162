package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Message classes for client to server messages.
 * @author xshi
 *
 */
public class ClientMessages {
	/**
	 * Composite message classes for clients. This is used for client messages because 
	 * some of the opcodes are same for different messages when send by servers or clients.
	 * @author xshi
	 */
	public static abstract class ClientCompositeMessage extends CompositeMessage
	{
		protected ClientCompositeMessage(byte opCode, Writable ... writables) {
			super(opCode, writables);
		}
		
		@Override
		public boolean isSynchronous()
		{
			return true;
		}
	}
	public static class ConnectMessage extends ClientCompositeMessage {	
		protected ConnectMessage(ClientInfo cInfo)
		{
			super(MessageProtocol.OP_TYPE_CONNECT, cInfo);
		}
		
		protected ConnectMessage()
		{
			super(MessageProtocol.OP_TYPE_CONNECT, new ClientInfo());
		}
		
		public ClientInfo getClientInfo()
		{
			return (ClientInfo)super.getWritable(0);
		}
	}
	public static class JoinMessage extends ClientCompositeMessage {
		protected JoinMessage(GameInfo gInfo)
		{
			super(MessageProtocol.OP_TYPE_JOIN, gInfo);
		}
		protected JoinMessage()
		{
			super(MessageProtocol.OP_TYPE_JOIN, new GameInfo());
		}
		public GameInfo getGameInfo()
		{
			return (GameInfo)super.getWritable(0);
		}
	}
	public static class LeaveMessage extends ClientCompositeMessage{
		protected LeaveMessage(GameInfo gInfo)
		{
			super(MessageProtocol.OP_TYPE_LEAVE, gInfo);
		}
		protected LeaveMessage()
		{
			super(MessageProtocol.OP_TYPE_LEAVE, new GameInfo());
		}
		public GameInfo getGameInfo()
		{
			return (GameInfo)super.getWritable(0);
		}
	}

	public static Message readFromInput(InputStream in) throws IOException
	{
		byte opCode = DataTypeIO.readByte(in);
		Message msgContainer = null; 
		switch (opCode)
		{
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
