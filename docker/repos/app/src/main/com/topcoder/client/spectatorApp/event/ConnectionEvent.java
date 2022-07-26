/**
 * ConnectionEvent.java
 *
 * Description:		Contains information related to a connection.
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.event;


public class ConnectionEvent extends java.util.EventObject {

    /** The remote IP address */
    private String remoteIP;
    /** The remote port number */
    private int remotePort;
    /** The exception, if any, that occurred */
    private String exception;

    /**
     *  Constructor of a Connection Event
     *
     *  @param source     the source of the event
     *  @param remoteIP   the remote IP address related to the connection
     *  @param remotePort the remote port number related to the connection
     */
    public ConnectionEvent(Object source, String remoteIP, int remotePort) {
        this(source, remoteIP, remotePort, null);
    }

    /**
     *  Constructor of a Connection Event
     *
     *  @param source     the source of the event
     *  @param remoteIP   the remote IP address related to the connection
     *  @param remotePort the remote port number related to the connection
     *  @param exception  the exception related to the event
     */

    public ConnectionEvent(Object source, String remoteIP, int remotePort, String exception) {
        super(source);
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
        this.exception = exception;
    }

    /** Gets the getremoteIP */
    public String getRemoteIP() {
        return remoteIP;
    }

    /** Gets the remotePort */
    public int getRemotePort() {
        return remotePort;
    }

    /** Gets the exception.  Returns null if not applicable. */
    public String getException() {
        return exception;
    }
}


/* @(#)ConnectionEvent.java */
