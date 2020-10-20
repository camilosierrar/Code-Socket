/***
 * ClientWriteThread
 * Date: 13/10/2020
 * Authors: Erwan Versmée, Camilo Sierra
 */
package src.stream.Client;

import java.io.*;
import java.net.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ClientWriteThread {

  public static JFrame gui;
  public static JPanel app;
  public static JPanel write;
  public static JPanel listen;
  public static JTextArea msgs;
  public static JScrollPane scroll;
  public static JButton sendBut;
  public static JTextField textField;
  public static Socket echoSocket = null;
  public static PrintStream socOut = null;
  public static BufferedReader stdIn = null;
  public static String user = "";

  /**
   * Initializes the User Interface
   */
  public static void initGUI() {
    gui = new JFrame("Discord");
    gui.setSize(500, 400);
    gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gui.addWindowListener(new WindowAdapter() {
      public void windowClosing(WindowEvent evt) {
        onExit();
      }
    });
    app = new JPanel();
    app.setLayout(new BorderLayout());
    write = new JPanel();
    write.setLayout(new BorderLayout());
    listen = new JPanel(new BorderLayout());
    msgs = new JTextArea();
    msgs.setEditable(false);
    msgs.setLineWrap(true);
    scroll = new JScrollPane(msgs);
    scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    listen.add(scroll, BorderLayout.CENTER);
    sendBut = new JButton("Send");
    ActionListener actionList = new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent evt) {
        // Your action handling code in here
        ClientWriteThread.sendMessage();
      }
    };
    sendBut.addActionListener(actionList);
    write.add(sendBut);
    textField = new JTextField("");
    textField.addActionListener(actionList);
    write.add(textField, BorderLayout.CENTER);
    write.add(sendBut, BorderLayout.LINE_END);
    app.add(listen, BorderLayout.CENTER);
    app.add(write, BorderLayout.PAGE_END);
    gui.add(app);
    gui.setVisible(true);
  }

  /**
   * Method called by WindowListener on close.
   */
  public static void onExit() {
    socOut.println("quitter");
  }

  /**
   * Sets the content of the mesage area. Adds message to the end of it. Not overwrite enabled.
   * It is called by ServerListennerThread when receiving a message.
   * @param message
   */
  public static void setTextArea(String message) {
    String old = msgs.getText();
    msgs.setText(old + message + "\n");
  }

  /**
   * Sends the message that the user typed on the text field. It is called by all action listenners.
   */
  public static void sendMessage() {
    String line = textField.getText();
    if (line.equals("quitter")) {
      socOut.println(line);
      return;
    }
    socOut.println(user + " : " + line);

    textField.setText("");
  }

  public static void main(String[] args) throws IOException {
    initGUI();

    echoSocket = null;
    socOut = null;
    // stdIn = null;
    user = "";

    if (args.length != 3) {
      System.out.println("Usage: java EchoClient <EchoServer host> <EchoServer port> <username>");
      System.exit(1);
    }

    try {
      // creation socket ==> connexion
      echoSocket = new Socket(args[0], new Integer(args[1]).intValue());
      user = args[2];
      socOut = new PrintStream(echoSocket.getOutputStream());
      // Deprecated stdIn = new BufferedReader(new InputStreamReader(System.in));
    } catch (SocketException e) {
      System.out.println("You were disconnected");
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host:" + args[0]);
      System.exit(1);
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for " + "the connection to:" + args[0]);
      System.exit(1);
    }
    ServerListennerThread slt = new ServerListennerThread(echoSocket);
    slt.start();
    /* Deprecated
     * String line; while (true) { line = stdIn.readLine(); if
     * (line.equals("quitter")) { socOut.println(line); break; } socOut.println(user
     * + " : " + line); // System.out.println("echo: " + socIn.readLine()); }
     * socOut.close(); stdIn.close();
     */
  }
}
