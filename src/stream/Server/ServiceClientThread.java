package src.stream.Server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
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
    
    public synchronized void envoyerMessage(String line, PrintStream streamClient) {
        streamClient.println(line);
    }

    public void supprimerClient() {
        OutputStream clientOStream;
        try {
            clientOStream = clientSocket.getOutputStream();
            streamsClients.remove(clientOStream);
            clientOStream.close();
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
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            List<PrintStream> socOut = new ArrayList<>();
            for(OutputStream os : streamsClients) {
                socOut.add(new PrintStream(clientSocket.getOutputStream()));
            }
            while (true) {
                String line = socIn.readLine();
                for(PrintStream ps: socOut)
                    envoyerMessage(line, ps);
                if(line.equals("quitter")) {
                    supprimerClient();
                    break;
                }
            }
            for(OutputStream os : streamsClients) {
                os.close();
            }
            socIn.close();
            clientSocket.close();
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }
}