package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Location implements Writable {

    private int x;
    private int y;

    protected Location(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Location() {
        this(0, 0);
    }

    @Override
    public void readFrom(InputStream in) throws IOException {
        x = DataTypeIO.readInt(in);
        y = DataTypeIO.readInt(in);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        DataTypeIO.writeInt(out, x);
        DataTypeIO.writeInt(out, y);
    }

    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
