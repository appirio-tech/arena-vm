/*
 * ClientSocketConnectionFactory
 *
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.impl.clientsocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.netCommon.io.ClientSocket;
import com.topcoder.netCommon.io.SocketUtil;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * This factory creates a connection using a ClientSocket as the connection
 * provider.
 *
 * NOTE
 * This is an initial implementation, a new NIO factory will be
 * created to provide better performance.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientSocketConnectionFactory implements ConnectionFactory {
    /**
     * The address to which this factory create connections
     */
    private InetSocketAddress address;

    /**
     * The factory used for custom serialization
     */
    private CSHandlerFactory csHandlerFactory;

    /**
     * Inactivity timeout set to new Connections
     */
    private int inactivityTimeout;

    /**
     * KeepAlive timeout set to new Connections
     */
    private int keepAliveTimeout;

    /**
     * The buffer size to use, -1 don't set
     */
    private int bufferSize;

    /**
     * Creates a new ClientSocketConnectionFactory that will create connections
     * to <i>address</i> using a ClientSocket as underlying connection provider.
     *
     * @param address Address to which the factory will create connections.
     * @param csHandlerFactory factory used for custom serialization
     * @param inactivityTimeout
     * @param keepAliveTimeout
     */
    public ClientSocketConnectionFactory(InetSocketAddress address, CSHandlerFactory csHandlerFactory, int inactivityTimeout, int keepAliveTimeout) {
        this.address = address;
        this.csHandlerFactory = csHandlerFactory;
        this.inactivityTimeout = inactivityTimeout;
        this.keepAliveTimeout= keepAliveTimeout;
        this.bufferSize = SocketUtil.getClientSocketBufferSize(address.getPort());
    }

    /**
     * Creates a new connection.
     *
     * No handler is set, so socket won't be read until a handler be set.
     *
     * @return a new Connection
     *
     * @throws IOException If the connection cannot be created
     */
    public Connection create() throws IOException {
        return configure(new ClientSocketToConnectionAdapter(buildClientSocket()));
    }


    /**
     * Creates a new connection with the specified <code>handler</code> as
     * the event connection handler.
     *
     * @return a new Connection
     *
     * @throws IOException If the connection cannot be created
     */
    public Connection create(ConnectionHandler handler) throws IOException {
        return configure(new ClientSocketToConnectionAdapter(buildClientSocket(), handler));
    }

    /**
     * Creates the ClientSocket
     *
     * @return the created ClientSocket
     *
     * @throws IOException If ClientSocket could not be created
     */
    private ClientSocket buildClientSocket() throws IOException {
        return new ClientSocket(buildSocket(address), csHandlerFactory.newInstance());
    }


    /**
     * Configures adapter with requires values
     *
     * @param adapter the adapter
     * @return the adapter
     */
    private ClientSocketToConnectionAdapter configure(ClientSocketToConnectionAdapter adapter) {
        adapter.setInactivityTimeout(inactivityTimeout);
        adapter.setKeepAliveTimeout(keepAliveTimeout);
        return adapter;
    }

    /**
     * Creates the net Socket to the address specified
     * Sets the options for the socket
     * - KeepAlive - true
     * - Linger - false,0
     * - Timeout - 10000
     *
     * @param address Address to which the socket should connect
     *
     * @return the connected Socket
     * @throws IOException If the socket could not be created
     */
    private Socket buildSocket(InetSocketAddress address) throws IOException {
        Socket socket = new Socket();
        socket.setKeepAlive(true);
        socket.setSoLinger(false, 0);
        if (bufferSize != -1) {
            socket.setTcpNoDelay(true);
            socket.setReceiveBufferSize(bufferSize);
            socket.setSendBufferSize(bufferSize);
        }
        //DataInputStream readFully does not handle the timeout,
        //we should wrap the datainput or modify the object reader to
        //handle timeout
        //This has to be the max time between packets when reading a message
        socket.setSoTimeout(10000);
        socket.connect(address);
        return socket;
    }
}
