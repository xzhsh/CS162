package edu.berkeley.cs.cs162.Client;

abstract public class Player extends BaseClient {

    public Player(String name, byte type) {
        super(name, type);
    }

    public Player(String name) {
        this(name, (byte) -1);
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
