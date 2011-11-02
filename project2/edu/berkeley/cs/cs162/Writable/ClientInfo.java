package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientInfo implements Writable {

    String name;
    byte type;
    
    /**
     * Don't use these constructors, we should be using factory methods in {@link MessageFactory}.
     * @param name
     * @param type
     */
    protected ClientInfo(String name, byte type) {
    	this.name = name;
    	this.type = type;
    }
    
    protected ClientInfo() {
        this("", (byte) -1);
    }
    
    //@Override
    public void readFrom(InputStream in) throws IOException {
        name = DataTypeIO.readString(in);
        type = DataTypeIO.readByte(in);
    }

    //@Override
    public void writeTo(OutputStream out) throws IOException {
    	DataTypeIO.writeString(out, name);
    	DataTypeIO.writeByte(out, type);
    }

    public String getName() {
    	return name;
    }
    
    public byte getPlayerType() {
    	return type;
    }
}
