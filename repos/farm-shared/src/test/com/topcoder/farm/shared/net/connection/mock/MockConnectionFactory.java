/*
 * MockConnectionFactory
 * 
 * Created 07/14/2006
 */
package com.topcoder.farm.shared.net.connection.mock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;

/**
 * Mock connection factory
 * 
 * This factory provides to simulate failures during connection
 * creation. 
 * 
 * This factory creates MockConnection objects
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class MockConnectionFactory implements ConnectionFactory {
    
    /**
     * When set, every call to create will throw an IOException
     */
    private boolean throwIOException;
    
    /**
     * Number of connections that were sucessfully created
     */
    private int connectionCount = 0;
    
    /**
     * Number of invocations to the create method
     */
    private int invocationCount = 0;
    
    /**
     * Connections to return when the create method is called.
     */
    private List connections = new ArrayList();
    
    
    /**
     * @see ConnectionFactory#create()
     * @throw {@link IOException} if the throwIOException is set
     */
    public synchronized Connection create() throws IOException {
        invocationCount++;
        if (throwIOException) throw new IOException("Cannot get connection");
        connectionCount++;
        return getConnection();
    }

    /**
     * @see ConnectionFactory#create(ConnectionHandler)
     * @throw {@link IOException} if the throwIOException is set
     */
    public synchronized Connection create(ConnectionHandler handler) throws IOException {
        invocationCount++;
        if (throwIOException) throw new IOException("Cannot get connection");
        connectionCount++;
        MockConnection mockConnection = getConnection();
        mockConnection.setHandler(handler);
        return mockConnection;
    }

    /**
     * Try to obtain a connection from the <code>connections </code>list. 
     * If none is available, returns a new MockConnection. Otherwise, removes the
     * first in the list and returns it.
     *  
     * @return the connection
     */
    private MockConnection getConnection() {
        if (connections.size() == 0) {
            return new MockConnection();
        } else {
            return (MockConnection) connections.remove(0);
        }
    }

    public synchronized boolean isThrowIOException() {
        return throwIOException;
    }

    public synchronized void setThrowIOException(boolean throwIOException) {
        this.throwIOException = throwIOException;
    }

    /**
     * @return Number of connections that were sucessfully created
     */
    public synchronized int getConnectionCount() {
        return connectionCount;
    }

    /**
     * @return Number of invocations to the create method
     */
    public synchronized int getInvocationCount() {
        return invocationCount;
    }
 
    /**
     * Add a connection to the <code>connections </code>list.
     * Connections added using this method, will be returned 
     * in the same order they were added by the create methods.
     * 
     * @param connection The connection to add
     */
    public void addConnection(MockConnection connection) {
        connections.add(connection);
    }
}
