package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

public class CompositeMessage extends Message {
    List<Writable> writables;

    protected CompositeMessage(byte opCode, Writable... writables) {
        this.opCode = opCode;
        this.writables = Arrays.asList(writables);
    }

    @Override
    public void readFrom(InputStream in) throws IOException {
        byte opcheck = DataTypeIO.readByte(in);
        if (opcheck != getMsgType()) {
            throw new AssertionError("Unexpected message");
        }
        readDataFrom(in);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        DataTypeIO.writeByte(out, opCode);
        //writables = new ArrayList<Writable>(length);
        for (int i = 0; i < writables.size(); i++) {
            writables.get(i).writeTo(out);
        }
    }

    @Override
    public boolean isSynchronous() {
        // Almost all Messages are Synchronous
        return true;
    }

    public Writable getWritable(int i) {
        return writables.get(i);
    }

    @Override
    public void readDataFrom(InputStream in) throws IOException {
        for (int i = 0; i < writables.size(); i++) {
            writables.get(i).readFrom(in);
        }
    }

}
