/*
 * ProxiedSocketClientConnector
 *
 * Created 04/03/2007
 */
package com.topcoder.netCommon.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * ClientConnector that uses an intermediate proxy to connect to its final
 * destination.<p>The proxy is a HTTP proxy which supports CONNECT method.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProxiedSocketClientConnector implements ClientConnector {
    private Socket socket;
    private String destinationHost;
    private OutputStream outputStream;
    private InputStream inputStream;
    private String localEndpoint;

    /**
     * Creates a new instance of <code>ProxiedSocketClientConnector</code> class. The HTTP proxy host and port are given. The final
     * destination is given as a string 'IP:Port'.
     *  
     * @param proxyHost the HTTP proxy host to connect to.
     * @param proxyPort the HTTP proxy port to connect to.
     * @param destinationHost the final network destination to connect to.
     * @throws IOException if I/O error occurs.
     */
    public ProxiedSocketClientConnector(String proxyHost, int proxyPort, String destinationHost) throws IOException {
        this.socket = new Socket(proxyHost, proxyPort);
        localEndpoint = ((InetSocketAddress) socket.getLocalSocketAddress()).getAddress().getHostAddress()+":"+socket.getLocalPort();
        init();
    }

    /**
     * Initializes the underlying socket via the proxy. HTTP CONNECT commands are sent to the HTTP proxy to connect the final destination.
     * 
     * @throws IOException if I/O error occurs. 
     */
    private void init() throws IOException {
        outputStream=socket.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);
        printStream.println("CONNECT " + destinationHost + " HTTP/1.0\n");
        printStream.flush();

        inputStream = socket.getInputStream();
        DataInputStream dataInput = new DataInputStream(inputStream);
        dataInput.readLine();
        dataInput.readLine();
    }

    /**
     * Gets the input stream which data is retrieved from the network.
     * 
     * @return the input stream of the network.
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * Gets the output stream which data is written to the network.
     * 
     * @return the output stream of the network.
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    /**
     * Closes the network connection.
     * 
     * @throws IOException if an I/O error occurs when closing this connector
     */
    public void close() throws IOException {
        socket.close();
    }

    /**
     * Returns the Local endpoint this connector is using.
     *
     * @return The local endpoint
     */
    public String getLocalEndpoint() {
        return localEndpoint;
    }

    /**
     * Returns the Remote endpoint this connector is connected to. The final destination will be returned.
     *
     * @return The remote endpoint
     */
    public String getRemoteEndpoint() {
        return destinationHost;
    }
}
