package edu.berkeley.cs.cs162.Writable;

/**
 * Message classes for client to server messages.
 * @author xshi
 *
 */
public class ClientMessages {
	public static class ConnectMessage extends CompositeMessage{
		
		public ConnectMessage(ClientInfo cInfo)
		{
			super(MessageProtocol.OP_TYPE_CONNECT, cInfo);
		}
		
		public ConnectMessage()
		{
			super(MessageProtocol.OP_TYPE_CONNECT, new ClientInfo());
		}
		
		public ClientInfo getClientInfo()
		{
			return (ClientInfo)super.getWritable(0);
		}
	}
}
