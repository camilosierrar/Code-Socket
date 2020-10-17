/***
 * ServiceClientThread
 * Date: 13/10/2020
 * Authors: Erwan Versmée, Camilo Sierra
 */


package src.stream.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class ServiceClientThread extends Thread{
    private static List<OutputStream> streamsClients = new ArrayList<>();

    private Socket clientSocket;

    ServiceClientThread(Socket s) {
        this.clientSocket = s;
        try {
            streamsClients.add(s.getOutputStream());
        } catch (IOException e) {
            System.out.println("Can't add Output Stream of new client : ");
            e.printStackTrace();
        }
    }
    
    public synchronized void envoyerMessage(String line) {
        List<PrintStream> socOut = new ArrayList<>();
        for(OutputStream os : streamsClients) {
            socOut.add(new PrintStream(os));
        }
        for(PrintStream ps: socOut)
            ps.println(line);
    }

    public void supprimerClient() {
        OutputStream clientOStream;
        try {
            clientOStream = clientSocket.getOutputStream();
            streamsClients.remove(clientOStream);
            //clientOStream.close();
        } catch (SocketException e) {
            System.out.println("Client disconnected");
        } catch (IOException e) {
            System.out.println("Can't delete Output Stream of client : ");
            e.printStackTrace();
        }
    }

    /**
  	* receives a request from client then sends an echo to the client
    * @param clientSocket the client socket
  	**/
	public void run() {
        try {
            BufferedReader socIn = null;
            PrintStream socOut = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                if(line.equals("quitter")) {
                    socOut.println(line);
                    System.out.println("Client Disconnected");
                    supprimerClient();
                    break;
                } else { 
                    envoyerMessage(line);
                }
            }
            //clientSocket.close();
        } catch (SocketException e) {
            System.out.println("Client Disconnected");
        } catch (Exception e) {
            System.err.println("Error in ServiceClientThread:" + e);
        }
    }
}