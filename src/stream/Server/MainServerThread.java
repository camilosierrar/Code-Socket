/***
 * MainServerThread
 * Date: 13/10/2020
 * Authors: Erwan Versmée, Camilo Sierra
 */

package src.stream.Server;

import java.io.*;
import java.net.*;

public class MainServerThread {

    public static void main(String args[]) {
        ServerSocket listenSocket;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }

        try {
			listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port
			System.out.println("Server ready..."); 
			ServiceClientThread.getMessagesTxt("src/stream/Server/messages.txt");
			while (true) {
				Socket clientSocket = listenSocket.accept();
				System.out.println("connexion from:" + clientSocket.getInetAddress());
				ServiceClientThread sct = new ServiceClientThread(clientSocket);
				sct.start();
			}
    	} catch (Exception e) {
    	    System.err.println("Error in MainServerThread:" + e);
    	}
    }
}