package edu.berkeley.cs.cs162.Writable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ClientInfo implements Writable {
    
    public ClientInfo() {
        // TODO Auto-generated constructor stub    
    }
    
    @Override
    public void readFrom(InputStream in) throws IOException {
        // TODO Auto-generated method stub  
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        // TODO Auto-generated method stub
        
    }

    public String getName() {
    	// TODO Auto-generated method stub
        return null;
    }
    
    public byte getPlayerType() {
    	// TODO Auto-generated method stub
        return 0;
    }
}
