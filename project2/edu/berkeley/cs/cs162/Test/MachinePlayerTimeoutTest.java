package edu.berkeley.cs.cs162.Test;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.Test;

import edu.berkeley.cs.cs162.Server.ClientConnection;

/**
 * Created by IntelliJ IDEA.
 * User: Jay
 * Date: 11/8/11
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class MachinePlayerTimeoutTest {
    @Test
    public void test() throws IOException, InterruptedException {
        final String address = "localhost";
        final int port = 1234;

        final Socket container[] = new Socket[1];

        Thread serverThread = new Thread() {
            public void run() {
                try {
                    ServerSocket server = new ServerSocket(port);
                } catch (IOException e) {

                }
            }
        };

        serverThread.start();

        Socket s1 = new Socket(address, port);
        serverThread.join();
        Socket s2 = container[0];

        ClientConnection con = new ClientConnection(s1, s2, 0, new PrintStream(new NullOutputStream()));
        con.setValid();

        //"connect" message from machineplayer

        //send an "ok" reply

        //"wfg" message from machineplayer

        //send an "ok" reply

        //send a "game start" w/ params message to machineplayer and other player

        //receive "ok" from both

        //send a "get move" message to machineplayer

        //artificially timeout

        //check if player gets a "game over"
    }
}
