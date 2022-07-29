/*
 * SocketUtil
 *
 * Created 02/01/2007
 */
package com.topcoder.netCommon.io;

/**
 * Helper class for Socket options
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class SocketUtil {

    /**
     * Return the socket buffer size specified for the client socket used to connect to port
     * <code>port</code>. Reads system property clientsocket.buffersize.port
     *
     * @param port The port number
     * @return The specified buffer size, -1 if not set
     */
    public static int getClientSocketBufferSize(int port) {
        return Integer.parseInt(System.getProperty("clientsocket.buffersize."+port, "-1"));
    }
    /**
     * Return the socket buffer size specified for the server socket used to listen on port
     * <code>port</code>. Reads system property serversocket.buffersize.port
     *
     * @param port The port number
     * @return The specified buffer size, -1 if not set
     */
    public static int getServerSocketBufferSize(int port) {
        return Integer.parseInt(System.getProperty("serversocket.buffersize."+port, "-1"));
    }
}
