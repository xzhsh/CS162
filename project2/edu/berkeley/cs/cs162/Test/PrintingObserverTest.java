package edu.berkeley.cs.cs162.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Random;

import org.junit.Test;

import edu.berkeley.cs.cs162.Client.PrintingObserver;
import edu.berkeley.cs.cs162.Server.Board;
import edu.berkeley.cs.cs162.Server.BoardLocation;
import edu.berkeley.cs.cs162.Server.ClientConnection;
import edu.berkeley.cs.cs162.Writable.BoardInfo;
import edu.berkeley.cs.cs162.Writable.ClientInfo;
import edu.berkeley.cs.cs162.Writable.ClientMessages;
import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.Message;
import edu.berkeley.cs.cs162.Writable.MessageFactory;
import edu.berkeley.cs.cs162.Writable.MessageProtocol;
import edu.berkeley.cs.cs162.Writable.WritableList;

public class PrintingObserverTest {

	@Test
	public void test() throws IOException {

		final int TEST_PORT = 1234;
		final String TEST_NAME = "TestObserver";
		final String TEST_GAME_NAME = "TestGame";

		BoardInfo boardInfo = MessageFactory.createBoardInfo(new Board(9));
		GameInfo gameInfo = MessageFactory.createGameInfo(TEST_GAME_NAME);
		ClientInfo blackPlayerInfo = MessageFactory.createHumanPlayerClientInfo("BlackPlayer");
		ClientInfo whitePlayerInfo = MessageFactory.createHumanPlayerClientInfo("WhitePlayer");
		
		ServerSocket server = new ServerSocket(TEST_PORT);
		Thread observerThread = new Thread() {
			public void run()
			{
				PrintingObserver.main(new String[] {"localhost",String.valueOf(TEST_PORT),TEST_NAME});
			}
		};
		observerThread.start();
		ClientConnection connection;
		{
			Socket c1 = server.accept();
			//c1.setSoTimeout(3000);
			Socket c2 = server.accept();
			//c2.setSoTimeout(3000);
			int syn_id = (new DataInputStream(c1.getInputStream())).readInt();
			int syn_id2 = (new DataInputStream(c2.getInputStream())).readInt();
			assertEquals(syn_id,syn_id2);
			connection = new ClientConnection(c1, c2, syn_id);
		}
		connection.receive3WayHandshake(new Random());
		Message connectMsg = connection.readFromClient();
		assertEquals(connectMsg.getMsgType(), MessageProtocol.OP_TYPE_CONNECT);
		
		ClientInfo cInfo = ((ClientMessages.ConnectMessage)connectMsg).getClientInfo();
		assertEquals(cInfo.getName(), TEST_NAME);
		assertEquals(cInfo.getPlayerType(), MessageProtocol.TYPE_OBSERVER);
		connection.sendReplyToClient(MessageFactory.createStatusOkMessage());
		Message listMessage = connection.readFromClient();
		assertEquals(listMessage.getMsgType(), MessageProtocol.OP_TYPE_LISTGAMES);
		WritableList gameList = MessageFactory.createWritableList(GameInfo.class, gameInfo);
		connection.sendReplyToClient(MessageFactory.createListGamesStatusOkMessage(gameList));
		ClientMessages.JoinMessage joinMessage = (ClientMessages.JoinMessage) connection.readFromClient();
		assertEquals(joinMessage.getGameInfo().getName(), TEST_GAME_NAME);

		Message joinReply = MessageFactory.createJoinStatusOkMessage(boardInfo, blackPlayerInfo, MessageFactory.createHumanPlayerClientInfo("WhitePlayer"));
		connection.sendReplyToClient(joinReply);
		
		Message genericMessageThatReturnsStatusOk = MessageFactory.createGameStartMessage(gameInfo, boardInfo, blackPlayerInfo, whitePlayerInfo);
		connection.sendToClient(MessageFactory.createGameStartMessage(gameInfo, boardInfo, blackPlayerInfo, whitePlayerInfo));
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		System.out.println("Game Start Worked");
		connection.sendToClient(MessageFactory.createMakeMoveMessage(gameInfo, blackPlayerInfo, 
				MessageProtocol.MOVE_STONE, new BoardLocation(0,0), Collections.<BoardLocation> emptyList()));
		System.out.println("Make Move Sent");
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		System.out.println("Make Move Worked");
		connection.sendToClient(MessageFactory.createMakeMoveMessage(gameInfo, blackPlayerInfo, 
				MessageProtocol.MOVE_STONE, new BoardLocation(1,1), Collections.<BoardLocation> emptyList()));
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		
		connection.sendToClient(MessageFactory.createMakeMoveMessage(gameInfo, blackPlayerInfo, 
				MessageProtocol.MOVE_PASS, new BoardLocation(0,0), Collections.<BoardLocation> emptyList()));
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		
		connection.sendToClient(MessageFactory.createMakeMoveMessage(gameInfo, blackPlayerInfo, 
				MessageProtocol.MOVE_PASS, new BoardLocation(1,1), Collections.<BoardLocation> emptyList()));
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		
		connection.sendToClient(MessageFactory.createGameOverMessage(gameInfo, 0, 1, whitePlayerInfo));
		assertTrue(connection.readReplyFromClient(genericMessageThatReturnsStatusOk).isOK());
		
	}

}
