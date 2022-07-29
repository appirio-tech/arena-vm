/*
 * AutoReconnectableConnectionFactory
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import java.io.IOException;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionFactory;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnectionHandler;

/**
 * This Connection factory creates reconnectable connections.
 * 
 * It implements the reconnectable behavior using another connection,
 * factory providing connections and listening events of the created connections.
 * When a connection is lost, a new connection is created using the factory and 
 * it is initilized before indicating reconnection.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AutoReconnectConnectionFactory implements ConnectionFactory {
    /**
     * Number of tries to get a new connection that a reconnect attempt
     * will do. 
     */
    private int maxTries = 5;
    
    /**
     * Time in ms to wait before trying again 
     * after an attempt to get a connection failed
     */
    private int msBetweenTries = 1000;
    
    /**
     * Factory providing the underlying connection
     */
    private ConnectionFactory factory;

    /**
     * Creates a new AutoReconnectConnectionFactory that will
     * use the provided factory to create underliying connections
     * of the  AutoReconnectConnection
     * 
     * @param factory Factory to use for connection creation
     */
    public AutoReconnectConnectionFactory(ConnectionFactory factory) {
        this.factory = factory;
    }

    /**
     * Creates a new AutoReconnectConnectionFactory that will
     * use the provided factory to create underliying connections
     * of the  AutoReconnectConnection.
     * 
     * @param factory Factory to use for connection creation
     * @param maxTries  Number of tries to get a new connection that a reconnect attempt
     *                  will do.
     * @param msBetweenTries  Time in ms to wait before trying again 
     *                        after an attempt to get a connection failed
     */
    public AutoReconnectConnectionFactory(ConnectionFactory factory, int maxTries, int msBetweenTries) {
        this.factory = factory;
        this.maxTries = maxTries;
        this.msBetweenTries = msBetweenTries;
    }

    public Connection create(ConnectionHandler handler) throws IOException {
        return new AutoReconnectConnection(factory, maxTries, msBetweenTries, (ReconnectableConnectionHandler) handler);
    }

    public Connection create() throws IOException {
        return new AutoReconnectConnection(factory, maxTries,  msBetweenTries);
    }
}
