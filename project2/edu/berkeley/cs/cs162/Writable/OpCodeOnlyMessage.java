package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OpCodeOnlyMessage extends Message {

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

    @Override
    public void readDataFrom(InputStream in) throws IOException {
        //There shouldn't be any more information other than the opcode
        //so this method should be a no-op
    }
}