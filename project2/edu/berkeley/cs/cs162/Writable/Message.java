package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;

public abstract class Message implements Writable {

    /**
     * Specifies the opCode of the Message
     */
    byte opCode;

    public Message() {}

    public byte getMsgType() {
        return opCode;
    }

    public boolean isOK(){
        return opCode == MessageProtocol.OP_STATUS_OK;
    }

    public abstract boolean isSynchronous();

    /**
     * Read the non-opcode part of the message from the inputstream.
     *
     * @param in
     * @throws IOException
     */
    public abstract void readDataFrom(InputStream in) throws IOException;
}
