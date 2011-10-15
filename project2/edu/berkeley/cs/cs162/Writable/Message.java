package edu.berkeley.cs.cs162.Writable;

public abstract class Message implements Writable {
    
    /**
     * Specifies the opCode of the Message
     */
    byte opCode;
    
    public Message() {
        // TODO Auto-generated constructor stub    
    }
    
    public byte getMsgType() {
        return opCode;
    }
}
