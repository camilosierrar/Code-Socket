package  src.stream.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServiceClientThread extends Thread{
    private static List<InputStream> streamsClients = new ArrayList<>();

    private Socket clientSocket;

    ServiceClientThread(Socket s) {
        this.clientSocket = s;
        try {
            streamsClients.add(s.getInputStream());
        } catch (IOException e) {
            System.out.println("Can't add Input Stream of new client : ");
            e.printStackTrace();
        }
	}

    /**
  	* receives a request from client then sends an echo to the client
      * @param clientSocket the client socket
      * @author ReseauxTeam
  	**/
	public void run() {
        try {
            BufferedReader socIn = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));    
            PrintStream socOut = new PrintStream(clientSocket.getOutputStream());
            while (true) {
                String line = socIn.readLine();
                socOut.println(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e); 
        }
    }
}