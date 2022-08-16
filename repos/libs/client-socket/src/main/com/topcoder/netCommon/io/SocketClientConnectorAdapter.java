/*
 * SocketClientConnectorAdapter
 *
 * Created 04/03/2007
 */
package com.topcoder.netCommon.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Adapter for Socket instances. It implements the <code>ClientConnector</code> interface using
 * a wrapped socket. The socket is handling all the traffic over the network. 
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SocketClientConnectorAdapter implements ClientConnector {
    /** Represents the wrapped socket. */ 
    private Socket socket;
    /** Represents the local endpoint description. */
    private String localEndpoint;
    /** Represents the remote endpoint description. */
    private String remoteEndpoint;

    /**
     * Creates a new SocketClientConnectorAdapter for the given socket.
     *
     * @param socket The socket to adapt
     */
    public SocketClientConnectorAdapter(Socket socket) {
        this.socket = socket;
        localEndpoint = ((InetSocketAddress) socket.getLocalSocketAddress()).getAddress().getHostAddress()+":"+socket.getLocalPort();
        remoteEndpoint = ((InetSocketAddress) socket.getRemoteSocketAddress()).getAddress().getHostAddress()+":"+socket.getPort();
    }

    /**
     * Gets the input stream used to retrieve data from the network. The input stream is obtained directly
     * by the socket.
     * 
     * @return the input stream used to retrieve data from the network.
     * @throws IOException if an I/O error occurs.
     */
    public InputStream getInputStream() throws IOException {
        return socket.getInputStream();
    }

    /**
     * Gets the output stream used to send data to the network. The output stream is obtained directly
     * by the socket.
     * 
     * @return the output stream used to send data to the network.
     * @throws IOException if an I/O error occurs.
     */
    public OutputStream getOutputStream() throws IOException {
        return socket.getOutputStream();
    }

    /**
     * Gets the string representation of this connection. In this case, it will be the remote endpoint of
     * this connection.
     * 
     * @see getRemoteEndpoint
     * @return the string representation of this connection.
     */
    public String toString() {
        return remoteEndpoint;
    }

    /**
     * Gets the local endpoint of this connection. In this case, the endpoint is represented by 'IP:Port',
     * where 'IP' is the local IP of the socket, and the 'Port' is the local port of the socket.
     * 
     * @return the local endpoint of this connection.
     */
    public String getLocalEndpoint() {
        return localEndpoint;
    }

    /**
     * Gets the remote endpoint of this connection. In this case, the endpoint is represented by 'IP:Port',
     * where 'IP' is the remote IP of the socket, and the 'Port' is the remote port of the socket.
     * 
     * @return the remote endpoint of this connection.
     */
    public String getRemoteEndpoint() {
        return remoteEndpoint;
    }

    /**
     * Closes the connection. The underlying socket is closed.
     * 
     * @throws IOException if an I/O error occurs.
     */
    public void close() throws IOException {
        socket.close();
    }
}
