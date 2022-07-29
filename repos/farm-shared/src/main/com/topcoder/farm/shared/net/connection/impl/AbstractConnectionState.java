/*
 * AbstractConnectionState
 * 
 * Created 07/10/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import java.io.IOException;

/**
 * Base class for States of an AbstractConnection
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class AbstractConnectionState implements ConnectionState {

    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#send(com.topcoder.farm.shared.net.connection.impl.AbstractConnection, java.lang.Object)
     */
    public void send(AbstractConnection connection, Object object) throws NotConnectedException, IOException {
        throw new NotConnectedException();
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#close(com.topcoder.farm.shared.net.connection.impl.AbstractConnection)
     */
    public void close(AbstractConnection connection) {
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#handleConnectionLost(com.topcoder.farm.shared.net.connection.impl.AbstractConnection)
     */
    public void handleConnectionLost(AbstractConnection connection) {
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#handleConnectionClosed(com.topcoder.farm.shared.net.connection.impl.AbstractConnection)
     */
    public void handleConnectionClosed(AbstractConnection connection) {
    }
    
    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#handleReceived(com.topcoder.farm.shared.net.connection.impl.AbstractConnection, java.lang.Object)
     */
    public void handleReceived(AbstractConnection connection, Object object) {
    }

    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#isClosed()
     */
    public abstract boolean isClosed();
    
    /**
     * @see com.topcoder.farm.shared.net.connection.impl.ConnectionState#isLost()
     */
    public abstract boolean isLost();
    
    public String toString() {
        String className = this.getClass().getName();
        return className.substring(className.lastIndexOf('.')+1);
    }
}