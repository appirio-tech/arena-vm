/*
 * AbstractDecoratedConnection
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;

/**
 * Base class for connection decorator
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class AbstractDecoratedConnection extends AbstractConnection {
    /**
     * The decorated connection
     */
    private volatile Connection connection; 
    
    /**
     * Contructs a new AbstractDecoratedConnection
     *
     */
    public AbstractDecoratedConnection() {
    }
    
    /**
     * Constructs a  AbstractDecoratedConnection for the specified<code>connection</code>
     * 
     * @param connection The connection to decorate
     */
    public AbstractDecoratedConnection(Connection connection) {
        setConnection(connection);
    }
    
    /**
     * Sets the decorated connection
     * 
     * @param connection The connection to decorate
     */
    public void setConnection(Connection connection) {
        Connection conn = this.connection;
        if (conn != null) {
            conn.clearHandler();
        }
        this.connection = connection;
        connection.setHandler(new ConnectionHandler() {
            public void connectionLost(Connection connection) {
                handleConnectionLost();
            }
        
            public void connectionClosed(Connection connection) {
                handleConnectionClosed();
            }
        
            public void receive(Connection connection, Object message) {
                handleReceived(message);
            }
        });
    }
    
    protected void bareSend(Object object) throws IOException {
        Connection conn = connection;
        if (conn != null) {
            conn.send(object);
        }
    }

    protected void bareClose() {
        Connection conn = connection;
        if (conn != null) { 
            conn.close();
        }
    }

    /**
     * Returns the decorated connection
     * @return the connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Releases the decorated connection.
     * Ir clears the handler, closes the decorated connection 
     */
    public void releaseConnection() {
        Connection conn = connection;
        if (conn != null) {
            conn.clearHandler();
            conn.close();
            connection = null;
        }
    }
    
    protected void release() {
        releaseConnection();
        super.release();
    }
}
