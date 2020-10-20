/***
 * ServiceClientThread
 * Date: 13/10/2020
 * Authors: Erwan Versmée, Camilo Sierra
 */

package src.stream.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
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
    private static String messages = "";

    private Socket clientSocket;
    /**
     * Constructor. Adds the client to the Client List.
     * @param s
     */
    ServiceClientThread(Socket s) {
        this.clientSocket = s;
        try {
            streamsClients.add(s.getOutputStream());
        } catch (IOException e) {
            System.out.println("Can't add Output Stream of new client : ");
            e.printStackTrace();
        }
    }
    
    /**
     * Sends message to all clients from the List. Line is the content of the message
     * @param line
     */

    public synchronized void envoyerMessage(String line) {
        List<PrintStream> socOut = new ArrayList<>();
        for(OutputStream os : streamsClients) {
            socOut.add(new PrintStream(os));
        }
        for(PrintStream ps: socOut) {
            ps.println(line);
        }
        messages += line + "\n";
    }

    /**
     * Reads the messages from the text file called filename
     * @param filename
     */

    public static void getMessagesTxt (String filename) {
        try {
            FileReader reader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(reader);
 
            String line;
 
            while ((line = bufferedReader.readLine()) != null) {
                messages += line += "\n";
            }
            reader.close();
 
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Saves messages from the server to the text file named filename
     * @param filename
     */
    public synchronized void saveMessagesTxt(String filename) {
        try {
            FileWriter writer = new FileWriter(filename, false);
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
 
            bufferedWriter.write(messages);
 
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Removes Client from List
     */
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


	public void run() {
        try {
            //System.out.println("Working Directory = " + System.getProperty("user.dir"));
            //getMessagesTxt("src/stream/Server/messages.txt");
            BufferedReader socIn = null;
            PrintStream socOut = null;
            socIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); 
            socOut = new PrintStream(clientSocket.getOutputStream());
            socOut.print(messages);
            while (true) {
                if(socIn == null || socOut == null)
                    throw new SocketException();
                
                String line = socIn.readLine();
                if(line.equals("quitter")) {
                    saveMessagesTxt("src/stream/Server/messages.txt");
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