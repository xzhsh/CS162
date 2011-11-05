package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class WritableString implements Writable {

    private String value;

    protected WritableString(String value) {
        this.value = value;
    }

    public void writeTo(OutputStream output) throws IOException {
        DataTypeIO.writeString(output, value);
    }

    public void readFrom(InputStream input) throws IOException {
        value = DataTypeIO.readString(input);
    }

}
