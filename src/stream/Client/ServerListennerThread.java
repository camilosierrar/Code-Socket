/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package src.stream.Client;

import java.io.*;
import java.net.*;

public class ServerListennerThread extends Thread {

    private Socket serverSocket;

    ServerListennerThread(Socket s) {
        this.serverSocket = s;
    }

    /**
     * receives a request from client then sends an echo to the client
     * 
     * @param serverSocket the client socket
     **/
    public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            while (true) {
                String line = socIn.readLine();
                if(line.equals("quitter")) {
                    break;
                }
                System.out.println(line);
            }
            this.serverSocket.close();
        } catch (SocketException e) {
            System.out.println("You were disconnected from the server");
        } catch (Exception e) {
            System.err.println("Error in ServerListennerThread:" + e);
        }
    }

}