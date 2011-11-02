package edu.berkeley.cs.cs162.Writable;

import edu.berkeley.cs.cs162.Server.StoneColor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;

/**
 * Factory class for Message.
 * 
 * Kunal, please use this class file to do most of the simpler messages so that it doesn't clog up our project. Thanks :D
 * @author xshi
 *
 */
public class MessageFactory {
	//TODO add simple message classes as static classes.
	public static class OpCodeOnlyMessage extends Message {

        byte opCode;

        public OpCodeOnlyMessage(byte opCode) {
			this.opCode = opCode;
		}

        public void readFrom(InputStream in) throws IOException {
			//There shouldn't be any more information other than the opcode
			//so this method should be a no-op
		}

		public void writeTo(OutputStream out) throws IOException {
			out.write(opCode);
		}
		
		public int hashCode() {
			return opCode;
		}
		
		public boolean equals(Object other) {
			return other.hashCode() == this.hashCode();
		}
		
		public boolean isSynchronous() {
			//for now, all non-disconnect op-code messages are synchronous.
			return opCode != MessageProtocol.OP_TYPE_DISCONNECT;
		}
	}
	
    /**
     * Reads an opcode from the input and returns it as an byte.
     * @param input the InputStream to read from.
     * @throws IOException 
     */
	public static byte readOpCodeFrom(InputStream input) throws IOException {
		return (byte) input.read();
	}
	
    /**
     * Reads a message including the op code from the input and returns it.
     * @param input the InputStream to read from.
     * @throws IOException 
     */
	public static Message readMessageFromInput(InputStream input) throws IOException {
		//TODO fill this in
		return null;
	}
	
	//TODO add create________Messsage(args) methods for all messages.
	public static Message createStatusOkMessage() {
		return new OpCodeOnlyMessage(MessageProtocol.OP_STATUS_OK);
	}
	
	public static ClientInfo createHumanPlayerClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_HUMAN);
	}
	
	public static ClientInfo createMachinePlayerClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_MACHINE);
	}
	
	public static ClientInfo createObserverClientInfo(String name) {
		return new ClientInfo(name, MessageProtocol.TYPE_OBSERVER);
	}

	public static Message createConnectMessage(ClientInfo cInfo) {
		return new CompositeMessage(MessageProtocol.OP_TYPE_CONNECT, cInfo);
	}

    public static StoneColorInfo createStoneColorInfo(byte color) {
        return new StoneColorInfo(color);
    }
}
