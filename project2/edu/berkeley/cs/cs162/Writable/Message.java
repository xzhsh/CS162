package edu.berkeley.cs.cs162.Writable;

public abstract class Message implements Writable {
    
    /**
     * Specifies the opCode of the Message
     */
    int opCode;
    
    Message(int msgType) {
        this.opCode = msgType;
    }
    
    int getMsgType() {
        return opCode;
    }
}
