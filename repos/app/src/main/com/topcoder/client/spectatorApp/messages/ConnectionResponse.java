/**
 * ConnectionResponse.java
 *
 * Description:		Connection response message
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.messages;


public class ConnectionResponse {

    /** Remote IP Address */
    public String remoteIP;
    /** Remote Port */
    public int remotePort;
    /** Reason message (may be null if not applicable) */
    public String reason;

    /**
     * ConnectionResponse constructor
     *
     * @param remoteIP   the remote IP Address of the connection
     * @param remotePort the remote port of the connection
     */
    public ConnectionResponse(String remoteIP, int remotePort) {
        this(remoteIP, remotePort, null);
    }

    /**
     * ConnectionResponse constructor
     *
     * @param remoteIP   the remote IP Address of the connection
     * @param remotePort the remote port of the connection
     * @param reason     textual information describing error (null if no error)
     */
    public ConnectionResponse(String remoteIP, int remotePort, String reason) {
        this.remoteIP = remoteIP;
        this.remotePort = remotePort;
        this.reason = reason;
    }

    /** Gets the remoteIP */
    public String getRemoteIP() {
        return remoteIP;
    }

    /** Gets the remotePort */
    public int getRemotePort() {
        return remotePort;
    }

    /** Gets the reason (may be null if not applicable) */
    public String getReason() {
        return reason;
    }

    public String toString() {
        return new StringBuffer().append("(ConnectionResponse)[").append(remoteIP).append(", ").append(remotePort).append(", ").append(reason).append("]").toString();
    }


}


/* @(#)ConnectionResponse.java */
