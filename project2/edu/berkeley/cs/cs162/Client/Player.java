package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.ClientInfo;

abstract public class Player implements Client {
    String name;
    byte type;
    
    public Player() {
        this("");
    }

    public Player(String name) {
        this(name, (byte)-1);
    }
    
    public Player(String name, byte type) {
        this.name = name;
        this.type = type;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setType(byte type) {
        this.type = type;
    }
    
    public byte getType() {
        return this.type;
    }
    
}
