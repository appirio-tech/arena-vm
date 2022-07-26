/*
 * NullConnectionHandler
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.shared.net.connection.impl;

import com.topcoder.farm.shared.net.connection.api.Connection;
import com.topcoder.farm.shared.net.connection.api.ConnectionHandler;

/**
 * Null object for the ConnectionHandler inteface
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class NullConnectionHandler implements ConnectionHandler {
    public static final ConnectionHandler INSTANCE = new NullConnectionHandler();
    
    protected NullConnectionHandler() {
    }
    
    public void connectionLost(Connection connection) {
    }
    
    public void connectionClosed(Connection connection) {
    }
    
    public void receive(Connection connection, Object message) {
    }
}