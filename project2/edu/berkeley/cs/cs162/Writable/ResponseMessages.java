package edu.berkeley.cs.cs162.Writable;

import java.io.DataInputStream;
import java.io.IOException;


public class ResponseMessages {
    public static Message readReplyFromInput(Message sentMessage, DataInputStream in) throws IOException {
        byte opCode = DataTypeIO.readByte(in);
        Message msgContainer = null;
        switch (opCode) {
            case MessageProtocol.OP_ERROR_INVALID_GAME:
            case MessageProtocol.OP_ERROR_INVALID_USER:
            case MessageProtocol.OP_ERROR_REJECTED:
            case MessageProtocol.OP_ERROR_UNCONNECTED:
                return new OpCodeOnlyMessage(opCode);
            case MessageProtocol.OP_STATUS_OK:
                msgContainer = MessageFactory.createStatusOkMessage();//TODO add return values
                //return new StatusOkMessage(sentMessage);
            default:
                assert false : "Unimplemented method";
        }
        msgContainer.readDataFrom(in);
        return msgContainer;
    }
}
