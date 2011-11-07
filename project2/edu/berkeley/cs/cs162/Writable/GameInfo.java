package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class GameInfo implements Writable {
    private String name;

    protected GameInfo() {
        name = null;
    }

    protected GameInfo(String name) {
        this.name = name;
    }


    @Override
    public void readFrom(InputStream in) throws IOException {
        name = DataTypeIO.readString(in);
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        DataTypeIO.writeString(out, name);
    }

    public String getName() {
        return name;
    }
}
