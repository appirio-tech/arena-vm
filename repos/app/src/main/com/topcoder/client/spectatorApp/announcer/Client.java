/**
 * Client.java
 *
 * Description:		Connection to the server from the client
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.announcer;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.topcoder.client.spectatorApp.announcer.events.ConnectionEvent;

public class Client {

    /** The socket */
    ArrayList socket = new ArrayList();

    /** The output stream to write objects */
    ArrayList outStream = new ArrayList();

    /**
     * Client Constructor
     */
    public Client() {
    }

    /**
     * Connects to a new socket
     *
     * @param address the connection event
     */
    public void connect(ConnectionEvent address) throws UnknownHostException, IOException {
        // Get the socket
        Socket sock = new Socket(address.getHostName(), address.getPortNo());

        // Get the output stream
        ObjectOutputStream out = new ObjectOutputStream(sock.getOutputStream());

        // Add both to our arraylist
        socket.add(sock);
        outStream.add(out);
    }

    /**
     * Sends a message to the remote servers
     *
     * @param message message to send
     * @throws when a communication exception was detected
     *
     */
    public synchronized void sendMessage(Object message) throws IOException {
    	// If no message - ignore
    	if(message==null) return;
    	
    	// Send the message
        for (int x = socket.size() - 1; x >= 0; x--) {
            ObjectOutputStream out = (ObjectOutputStream) outStream.get(x);
            out.writeObject(message);
            out.flush();
        }
    }

    /**
     * Closes the connections
     * @throws exception closing the connection
     */
    public void close() throws IOException {
        for (int x = socket.size() - 1; x >= 0; x--) {
            Socket sock = (Socket) socket.remove(x);
            ObjectOutputStream out = (ObjectOutputStream) outStream.remove(x);
            out.flush();
            sock.close();
        }
    }


}
