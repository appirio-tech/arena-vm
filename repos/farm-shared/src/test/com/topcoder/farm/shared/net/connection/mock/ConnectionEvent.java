/*
 * ConnectionEvent
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.net.connection.mock;

import com.topcoder.farm.shared.net.connection.api.Connection;

public class ConnectionEvent {
    public static final int RECEIVED = 1;
    public static final int CLOSED = 2;
    public static final int LOST = 3;

    private Connection connection;
    private int type;
    private Object arg;
    
    public ConnectionEvent(Connection connection, int type, Object arg) {
        this.connection = connection;
        this.type = type;
        this.arg = arg;
    }

    protected Object getArg() {
        return arg;
    }

    protected Connection getConnection() {
        return connection;
    }

    protected int getType() {
        return type;
    }
    
    public static ConnectionEvent lostEvent(Connection cnn) {
        return new ConnectionEvent(cnn, LOST, null);
    }
    
    public static ConnectionEvent closedEvent(Connection cnn) {
        return new ConnectionEvent(cnn, CLOSED, null);
    }
    
    public static ConnectionEvent receivedEvent(Connection cnn, Object o) {
        return new ConnectionEvent(cnn, RECEIVED, o);
    }

}