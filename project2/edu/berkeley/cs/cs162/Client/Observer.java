package edu.berkeley.cs.cs162.Client;


import edu.berkeley.cs.cs162.Writable.GameInfo;
import edu.berkeley.cs.cs162.Writable.MessageFactory;

import java.util.ArrayList;

abstract public class Observer extends BaseClient {

    protected int joinedGames;

    public Observer() {
        this("");
    }

    public Observer(String name) {
        this(name, (byte) -1);
    }

    public Observer(String name, byte type) {
    	super(name,type);
        joinedGames = 0;
    }

   /*public void setName(String name) {
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
    }*/

}
