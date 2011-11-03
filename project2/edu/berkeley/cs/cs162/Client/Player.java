package edu.berkeley.cs.cs162.Client;

abstract public class Player extends BaseClient {

    String name;
    byte type;

    public Player(String name, byte type) {
        this.name = name;
        this.type = type;
    }

    public Player(String name) {
        this(name, (byte)-1);
    }
    
    public Player() {
        this("");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public byte getType() {
        return this.type;
    }
    
    public void setType(byte type) {
        this.type = type;
    }
}
