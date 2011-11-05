package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WritableByte implements Writable {

    private byte value;

    protected WritableByte() {
        value = (byte) 0;
    }

    protected WritableByte(byte value) {
        this.value = value;
    }

    public void writeTo(OutputStream output) throws IOException {
        DataTypeIO.writeByte(output, value);
    }

    public void readFrom(InputStream input) throws IOException {
        value = DataTypeIO.readByte(input);
    }

}
