package edu.berkeley.cs.cs162.Client;

import edu.berkeley.cs.cs162.Writable.*;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;

public class PrintingObserver extends Observer {

    public PrintingObserver(String name) {
        super(name, MessageProtocol.TYPE_OBSERVER);
    }

    public static void main(String[] args){
        PrintingObserver observer = new PrintingObserver(args[2]);
        String address = args[0];
        Integer port = Integer.valueOf(args[1]);

        if(observer.connectTo(address, port)){
            System.out.println("Printing observer connected, yo.");
        }
    }

    public void handleMessage(Message m) {

    }
}
