package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Messages from the server to client.
 * 
 * @author xshi
 */
public class ServerMessages {
	public static class ServerCompositeMessage extends CompositeMessage
	{
		protected ServerCompositeMessage(byte opCode, Writable ... writables) {
			super(opCode, writables);
		}
		public boolean isSynchronous()
		{
			return true;
		}
	}

	public static class GameStartMessage extends ServerCompositeMessage
	{
		protected GameStartMessage() {
			super(MessageProtocol.OP_TYPE_GAMESTART, new GameInfo(), new BoardInfo(), new ClientInfo(), new ClientInfo());
		}

        protected GameStartMessage(GameInfo game, BoardInfo board, ClientInfo blackPlayer, ClientInfo whitePlayer)
        {
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
	public static class GameOverMessage extends ServerCompositeMessage
	{
		//TODO use some logic to determine additional parameters
        protected GameOverMessage()
        {
			super(MessageProtocol.OP_TYPE_GAMEOVER, new GameInfo(), new WritableDouble(), new WritableDouble(), new ClientInfo(), new WritableByte());
		}
	}
	public static class MakeMoveMessage extends ServerCompositeMessage
	{
		protected MakeMoveMessage()
        {
			super(MessageProtocol.OP_TYPE_MAKEMOVE, new GameInfo(), new ClientInfo(), new WritableByte(), new Location(), new WritableLocationList());
		}

        protected MakeMoveMessage(GameInfo game, ClientInfo player, byte moveType, Location loc, List<Location> locationlist)
        {
            super(MessageProtocol.OP_TYPE_MAKEMOVE, game, player, new WritableByte(moveType), loc, new WritableLocationList(locationlist));
        }

		public boolean isSynchronous()
		{
			return false;
		}
	}
	public static class GetMoveMessage extends ServerCompositeMessage
	{
		protected GetMoveMessage() {
			super(MessageProtocol.OP_TYPE_GETMOVE);
		}

		public boolean isSynchronous()
		{
			return false;
		}
	}
	public static Message readFromInput(InputStream in) throws IOException
	{
		byte opCode = DataTypeIO.readByte(in);
		Message msgContainer = null; 
		switch (opCode)
		{
			case MessageProtocol.OP_TYPE_GAMESTART:
				msgContainer = new GameStartMessage();
			break;
			case MessageProtocol.OP_TYPE_GAMEOVER:
				msgContainer = new GameOverMessage();
			break;
			case MessageProtocol.OP_TYPE_MAKEMOVE:
				msgContainer = new MakeMoveMessage();
			break;
			case MessageProtocol.OP_TYPE_GETMOVE:
				msgContainer = new GetMoveMessage();
			break;
			default:
				assert false : "Unimplemented method";
		}
		msgContainer.readDataFrom(in);
		return msgContainer;
	}
}
