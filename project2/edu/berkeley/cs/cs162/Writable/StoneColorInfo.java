package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StoneColorInfo implements Writable {

    byte color;

    protected StoneColorInfo(byte c) {
        this.color = c;
    }

    protected StoneColorInfo() {
        this((byte) -1);
    }

    //@Override
    public void readFrom(InputStream in) throws IOException {
        this.color = DataTypeIO.readByte(in);
    }

    //@Override
    public void writeTo(OutputStream out) throws IOException {
        DataTypeIO.writeByte(out, color);
    }

    public byte getColor() {
        // TODO should this return the object or just the byte code?
        return color;
    }
}
