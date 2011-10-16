package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Writable {
    /**
     * Deserializes the Writable based on input from the InputStream
     * @param in
     */
    public void readFrom(InputStream in) throws IOException;
    /**
     * Serializes the Writable out over the OutputStream 
     * @param out
     */
    public void writeTo(OutputStream out) throws IOException;
}
