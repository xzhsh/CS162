package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.ClientInfo;

abstract public class BaseClient implements Client {

    String name;
    byte type;

    public BaseClient(String name, byte type) {
        this.name = name;
        this.type = type;
    }

    public BaseClient(String name) {
        this(name, (byte) -1);
    }

    public BaseClient() {
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

    public ClientInfo getClientInfo() {
        return null;
    }
}
