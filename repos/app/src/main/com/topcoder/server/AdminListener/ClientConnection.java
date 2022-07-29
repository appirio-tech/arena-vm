package com.topcoder.server.AdminListener;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.listener.monitor.MonitorCSHandler;
import java.io.EOFException;
import java.net.SocketException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLServerSocket;

import org.apache.log4j.Category;

public class ClientConnection implements Runnable {

    private SSLSocket acceptedSocket = null;
    private SSLServerSocket acceptorSocket = null;
    
    private int connectionId;
    private ClientConnectionSet parentSet = null;
    private static final Category log = Category.getInstance(ClientConnection.class);
    
    private boolean connectionClosed = false;
    
    private ClientSocket clientSocket = null;

    ClientConnection(SSLServerSocket acceptorSocket, int connectionId, ClientConnectionSet parentSet) {
        this.acceptorSocket = acceptorSocket;
        this.connectionId = connectionId;
        this.parentSet = parentSet;
        Thread t = new Thread(this);
        t.start();
    }

    public void run() {
        log.info("Starting thread for client " + connectionId);

        try {
            // This line blocks until an incoming connection is detected
            acceptedSocket = (SSLSocket) acceptorSocket.accept();
            log.info("Got socket connection for client " + connectionId);

            // Get the streams
            clientSocket = new ClientSocket(acceptedSocket, new MonitorCSHandler());
            
            // Start the next thread and inform the processor we have a new connection.
            // This needs to happen after the outgoing stream is created, otherwise
            // writeObject() calls triggered here that go to this outgoing stream would
            // fail.
            parentSet.gotConnection(connectionId, acceptedSocket.getInetAddress().toString());

            // Read objects indefinitely
            for (; ;) {
                // This blocks until input received
                Object obj = clientSocket.readObject();
                parentSet.enqueue(connectionId, obj);
            }
        } catch (Exception e) {
            if (e instanceof EOFException) {
                log.info("Client connection " + connectionId + " closed");
            } else if (e instanceof SocketException) {
                log.info("Client connection " + connectionId + " broken");
            } else {
                log.error("Client connection " + connectionId + " error", e);
            }
        } finally {
            parentSet.lostConnection(connectionId);
            close();
        }
        log.info("Exiting thread for client " + connectionId);
    }

    void close() {
        try {
            connectionClosed = true;
            if (acceptedSocket != null) {
                acceptedSocket.close();
                acceptedSocket = null;
                clientSocket = null;
            }
        } catch (Exception e) {
            log.error("Error closing socket", e);
        }
    }

    synchronized void writeObject(Object o) {
        if (clientSocket == null || acceptedSocket == null) {
            log.error("Write requested on inactive client stream " + connectionId);
            // dpecora - Re-notify interested parties that this connection isn't valid
            if (connectionClosed) {
                parentSet.lostConnection(connectionId);
            }
        } else {
            try {
                clientSocket.writeObject(o);
            } catch (Exception e) {
                log.error("Error writing to client " + connectionId, e);
            }
        }
    }
}

