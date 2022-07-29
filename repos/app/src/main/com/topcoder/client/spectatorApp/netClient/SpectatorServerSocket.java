/**
 * SpectatorServerSocket.java
 *
 * Description:		Spectator app's server socket
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.netClient;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Category;

public class SpectatorServerSocket extends Thread {

	private static final Category cat = Category.getInstance(SpectatorServerSocket.class.getName());

	/** The server socket */
    private ServerSocket server;

    /** Switched used to keep the socket running */
    private boolean keepRunning = true;

    /** Reference to the dispatch thread */
    private DispatchThread dispatch = DispatchThread.getInstance();

    /**
     * SpectatorServerSocket Constructor
     *
     * @param port   port to listenon
     */
    public SpectatorServerSocket(int port) throws IOException {
        // Get the SpectatorServerSocket socket
        server = new ServerSocket(port);

        // Make a deamon thread
        setDaemon(true);

        // Start the thread
        start();
    }

    /**
     * Connect and process all objects (and eventually disconnect) to/from the server)
     */
    public void run() {

        // Loop around reading the next object
        while (keepRunning) {

            try {
                // Accept the connection
                Socket clientConnection = server.accept();

                try {
                    ObjectInputStream inObject = new ObjectInputStream(clientConnection.getInputStream());

                    // Do NOT throw the connection off to a processing thread
                    // This enforces that only ONE announcer connection is allowed by blocking

                    while (keepRunning) {
                        dispatch.queueMessage(inObject.readObject());
                    }
                } catch (EOFException e) {
                    // Ignore - simply closed

                    // Catch error
                } catch (Throwable e) {
                    cat.error("Error sending object", e);

                    // Clean up
                } finally {
                    clientConnection.close();
                }

            } catch (Throwable e) {
				cat.error("Error sending object", e);
            }
        }

        // Close the server
        try {
            server.close();
        } catch (IOException e) {
        }

    }


    /**
     * Closes the current connection
     */
    public void close() {
        // Shutdown the loop and interrupt the thread
        keepRunning = false;
        this.interrupt();
    }


}
