package com.topcoder.client.contestMonitor.model;

/**
 * This class provides a simple client to the AdminListener server.  It will
 * be used in testing, for example load testing of the AdminListener server.
 * It has basic methods for sending requests and receiving responses, in
 * a nonblocking manner, and it keeps track of the time taken between
 * request and response.
 *
 * @author John Waymouth (coderlemming)
 */

import com.topcoder.server.AdminListener.AdminConstants;
import com.topcoder.server.AdminListener.request.TestConnection;
import org.apache.log4j.Logger;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MonitorTestClient implements TestBlockingReaderThread.Client {

    private String host;
    private int port;
    private int id;
    private Client client;

    private Logger log = Logger.getLogger(MonitorTestClient.class);

    private SSLSocket clientSocket = null;
    private SSLSocketFactory sslFact = null;
    // unused private ObjectInputStream inputStream=null;
    private ObjectOutputStream outputStream = null;

    private TestBlockingReaderThread reader = null;

    private Class expectedResponse = null;
    private long timeSent = -1;

    public interface Client {

        void receivedObject(int id, Object obj, long elapsedTime);
    }

    public MonitorTestClient(String host, int port, int id, Client client) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.client = client;
    }

    /**
     * Attempt to establish a connection.
     *
     */

    public synchronized void connect() throws UnknownHostException, Exception {
        InetAddress address = InetAddress.getByName(host);
        sslFact = (SSLSocketFactory) SSLSocketFactory.getDefault();

        clientSocket = (SSLSocket) sslFact.createSocket(address, port);
        String cipherStrings[] = new String[1];
        cipherStrings[0] = AdminConstants.SSL_CIPHER;
        clientSocket.setEnabledCipherSuites(cipherStrings);

        outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        reader = new TestBlockingReaderThread((Socket) clientSocket, this, id);

        // Send some data to ensure the connection works

        outputStream.writeObject(new TestConnection());

        // if the above screwed up, the test most certainly failed, so just let
        // the exception propogate
    }

    public synchronized void disconnect() {
        if (clientSocket != null) {
            reader.stop();
            try {
                clientSocket.close();
            } catch (Exception e) {
            }
            clientSocket = null;
            sslFact = null;
            outputStream = null;
            reader = null;
        }
    }

    /**
     * Send an object and start the timer.
     */

    public synchronized void sendRequest(Object request, Class expectedResponse) throws Exception {
        if (timeSent != -1 || this.expectedResponse != null)
            throw new Exception("sendRequest() called, but there is already a request awaiting a response");
        if (clientSocket == null)
            throw new Exception("sendRequest() called, but there is no connection");
        outputStream.writeObject(request);
        timeSent = System.currentTimeMillis();
        this.expectedResponse = expectedResponse;
    }

    /**
     * Receive a response, and forward it to the client.
     */

    public synchronized void receivedObject(int id, Object obj) {

        // is this our response?
        if (timeSent == -1 || expectedResponse == null || !(obj.getClass().equals(expectedResponse))) {
            //log.debug("Client[" + id + "]: Received unexpected response: " + obj + " which is an instance of " + obj.getClass());
            return;
        }
        if (id != this.id)
            log.warn("Client[" + id + "]: Received a response from the wrong thread");

        long elapsedTime = System.currentTimeMillis() - timeSent;
        timeSent = -1;
        expectedResponse = null;
        client.receivedObject(id, obj, elapsedTime);
    }

    public synchronized void stop(int id) {
        log.warn("Client[" + id + "]: Reader thread stopped.");
        disconnect();
    }
}
