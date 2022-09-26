package com.topcoder.client.contestMonitor.model;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.TestConnection;
import com.topcoder.server.listener.monitor.MonitorCSHandler;
import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

//import org.apache.log4j.Category;

final class MonitorServerConnection implements StoppableThread.Client {

    private static final int DELAY = 1000;

    private final InetAddress address;
    private final int port;
    private final int id;
    private final MonitorNetClient monitorNetClient;
    private static final Logger log = Logger.getLogger(MonitorServerConnection.class);

     private ClientSocket clientSocket;
    // private SocketReaderThread readerThread;
    private SSLSocket sslSocket;
    //private BlockingReaderThread readerThread;
    private SSLSocketFactory sslFact;

    private StoppableThread retryThread;

    MonitorServerConnection(String host, int port, MonitorNetClient monitorNetClient, int id)
            throws UnknownHostException {
        this.port = port;
        this.id = id;
        this.monitorNetClient = monitorNetClient;
        clientSocket = null;
        sslSocket = null;
        //readerThread=null;
        address = InetAddress.getByName(host);
        // Create the socket factory.  This will take a while.
        sslFact = (SSLSocketFactory) SSLSocketFactory.getDefault();
        tryToConnect();
        if (!isConnected()) {
            startRetryThread();
        }
    }


    // dpecora - use blocking I/O with normal serialization
    private void tryToConnect() {
        try {
            //clientSocket=new ClientSocket(address,port,new MonitorCSHandler());
            //readerThread=new SocketReaderThread(clientSocket,monitorNetClient,id);

            // Create and test the socket.
            sslSocket = (SSLSocket) sslFact.createSocket(address, port);
            sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
            // Send a piece of data to ensure the connection is operating properly.
            clientSocket = new ClientSocket(sslSocket, new MonitorCSHandler());
            clientSocket.writeObject(new TestConnection());
            
            // Fire up the reader thread
            new BlockingReaderThread(clientSocket, monitorNetClient, id);
        } catch (Exception e) {
            // Exception means there's something haywire with the connection.  Close the socket.
            log.error("Error connecting to admin listener server", e);
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (Exception ex) {
            }
            clientSocket = null;
        }
    }

    private void startRetryThread() {
        retryThread = new StoppableThread(this, "RetryThread");
        retryThread.start();
    }

    private void stopRetryThread() {
        try {
            if (retryThread != null) {
                retryThread.stopThread();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    String getHostAddress() {
        return address.getHostAddress();
    }

    int getPort() {
        return port;
    }

    boolean isConnected() {
        return clientSocket != null;
    }

    void writeObject(Object obj) throws IOException {
        if (clientSocket != null) {
            clientSocket.writeObject(obj);
        }
    }

    void stop(boolean isStopping) {
        try {
            // With a blocking reader, this will cause the reader thread to exit quickly
            // since it will cause an exception to be thrown on that thread
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //readerThread.stop();
        clientSocket = null;
        //readerThread=null;
        connectionStatusChanged(id, false);
        if (!isStopping) {
            stopRetryThread();
            startRetryThread();
        }
    }

    public void cycle() throws InterruptedException {
        Thread.sleep(DELAY);
        tryToConnect();
        if (isConnected()) {
            connectionStatusChanged(id, true);
            retryThread.stopThread();
        }
    }

    private void connectionStatusChanged(int id, boolean connected) {
        monitorNetClient.connectionStatusChanged(id, connected);
    }
}
