/*
 * TunnelConnection
 *
 * Created 04/06/2007
 */
package com.topcoder.net.httptunnel.server;

/**
 * Contains all data related to a tunnel connection.
 *
 *
 * @autor Diego Belfer (Mural)
 * @version $Id$
 */
class TunnelConnection {
    /**
     * Connection used to send messages to the client.
     */
    private final HTTPConnection persistentConnection;

    /**
     * Token used to generate digest for this tunnel connection
     */
    private final String token;

    /**
     * Connection used to received messages from the client.
     * This value can be updated.
     */
    private HTTPConnection inputConnection;


    /**
     * Creates a new TunnelConnection  for the given persistent connection and Token
     *
     * @param persistentConnection The persistent connection used to send messages to the client
     * @param token The token for digest generation
     */
    public TunnelConnection(HTTPConnection persistentConnection, String token) {
        this.persistentConnection = persistentConnection;
        this.token = token;
        this.inputConnection = HTTPConnection.NO_CONNECTION;
    }

    /**
     * Returns the tunnel connection id.  <p>
     * This value is equal to the persistent connection id.
     *
     * @return The tunnel id.
     */
    public Integer getId() {
        return persistentConnection.getId();
    }

    /**
     * The remote IP of the persistent connection.
     *
     * @return The remote IP.
     */
    public String getRemoteIP() {
        return persistentConnection.getRemoteIP();
    }

    /**
     * The token for this Tunnel Connection.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Returns the id of the current input connection.
     *
     * @return The input connection id.
     */
    public Integer getInputId() {
        return inputConnection.getId();
    }

    /**
     * @return The input connection
     */
    public HTTPConnection getInputConnection() {
        return inputConnection;
    }

    /**
     * @return The output connection
     */
    public HTTPConnection getPersistentConnection() {
        return persistentConnection;
    }

    /**
     * Sets a new input connection for this tunnel connection.<p>
     *
     * @param inputConnection The new input connection to use.
     */
    public void setInputConnection(HTTPConnection inputConnection) {
        this.inputConnection = inputConnection;
    }
}