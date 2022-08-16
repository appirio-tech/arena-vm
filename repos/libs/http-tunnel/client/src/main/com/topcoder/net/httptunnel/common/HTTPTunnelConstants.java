/*
 * HTTPTunnelConstants
 *
 * Created 04/05/2007
 */
package com.topcoder.net.httptunnel.common;

/**
 * HTTP Tunnel Constants used by Client and Server sections
 * of the Tunnel component.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface HTTPTunnelConstants {
    /**
     * Header sent when initial connection is made. Must be used
     * for generating digest when new connections are made to the TunnelServer
     */
    public static final String HEADER_TC_TOKEN    = "TC-Token";
    /**
     * Header containing the Digest to authenticate new connections and
     * associted them with a previous one.
     */
    public static final String HEADER_TC_DIGEST   = "TC-Digest";
    /**
     * Header containig the tunnel-id, sent by the server on on initial connection
     * response.
     */
    public static final String HEADER_TC_TUNNELID = "TC-TunnelID";
    
    /**
     * Header set when a first byte is sent and must be removed.
     */
    public static final String HEADER_TC_OPENBYTE = "TC-Byte";

    /**
     * Header containig Server timestamp. Sent by the server on initial connection.
     * Must be re-sent to the server when a new connection is made to the server.
     * The new TS must be calculate by the client.
     */
    public static final String HEADER_TC_TS = "TC-TS";

    /**
     * Header name. The HTTP content type of the connection.
     */
    public static final String HEADER_CONTENT_TYPE = "Content-Type";

    /**
     * Header value. The HTTP content is binary stream.
     */
    public static final String HEADER_CONTENT_TYPE_APPOCTEC = "application/octet-stream";

    /**
     * Header name. Connection directive
     */
    public static final String HEADER_CONNECTION = "Connection";
    /**
     * Header Value, Keep alive the connection
     */
    public static final String HEADER_CONNECTION_KEEP_ALIVE = "keep-alive";
}
