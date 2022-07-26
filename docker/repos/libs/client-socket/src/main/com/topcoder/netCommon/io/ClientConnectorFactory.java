/*
 * ClientSocketFactory
 * 
 * Created Jun 18, 2008
 */
package com.topcoder.netCommon.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import com.topcoder.net.httptunnel.client.HTTPTunnelClientConnector;

/**
 * Defines a factory which creates proper <code>ClientConnector</code> instances according to the given arguments. 
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ClientConnectorFactory {
    /** Represents the singleton of the <code>SSLSocketFactory</code> used to create SSL sockets. */
    private static SSLSocketFactory sslFactory = null;

    /**
     * Creates a socket-based <code>ClientConnector</code>. The remote address and port are given. Based on the <code>useSSL</code>
     * argument, it creates either a plain socket or an SSL socket. When an SSL socket is created, the actual SSL port is specified by
     * the sum of <code>port</code> and <code>SSLOffset</code> arguments.
     * 
     * @param address the remote address to connect to.
     * @param port the port number if a plain socket is used.
     * @param useSSL a flag indicating if an SSL socket should be used instead of plain socket.
     * @param SSLOffset an offset of the port if an SSL socket should be used.
     * @return the <code>ClientConnector</code> instance using an underlying socket (either plain or SSL).
     * @throws IOException if an I/O error occurs when creating the connection.
     */
    public static ClientConnector createSocketConnector(String address, int port, boolean useSSL, int SSLOffset) throws IOException {
        Socket finalSocket = null;
        if (useSSL) {
            if (sslFactory == null) {
                synchronized (ClientConnectorFactory.class) {
                    if (sslFactory == null) {
                        sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                    }
                }
            }
            SSLSocket socket = (SSLSocket) sslFactory.createSocket(address, port + SSLOffset);
            // Enable all possible cipher combinations
            socket.setEnabledCipherSuites(socket.getSupportedCipherSuites());
            finalSocket = socket;
        } else {
            finalSocket = new Socket(address, port);
        }
        return new SocketClientConnectorAdapter(finalSocket);
    }

    /**
     * Creates an HTTP(S) tunnel-based <code>ClientConnector</code>. The URL of the HTTP tunnel is given. Based on the <code>useSSL</code>
     * argument, it creates either an HTTP or an HTTPS connection. When using SSL, if the port of the tunnel is standard HTTP port (80), 
     * a standard HTTPS port (443) will be used. Otherwise, the port of the original tunnel will be added with the <code>SSLOffset</code>
     * argument. 
     * 
     * @param tunnelLocation the URL of the HTTP tunnel to be connect to.
     * @param useSSL a flag indicating if an SSL socket should be used instead of plain socket.
     * @param SSLOffset an offset of the port if a non-standard port and SSL should be used.
     * @return the <code>ClientConnector</code> instance using an underlying HTTP(S) tunnel.
     * @throws IOException if an I/O error occurs when creating the connection.
     */
    public static ClientConnector createTunneledConnector(String tunnelLocation, boolean useSSL, int SSLOffset) throws IOException {
        tunnelLocation = fixURL(tunnelLocation, useSSL, SSLOffset);
        return new HTTPTunnelClientConnector(tunnelLocation);
    }

    /**
     * Transforms a tunnel URL based on the given HTTP tunnel URL and SSL usage. If SSL is not used, the given HTTP tunnel is returned.
     * If SSL is used, while the port of the given tunnel URL is standard HTTP port (80), a standard HTTPS port (443) will be used. Otherwise,
     * the port of the original tunnel will be added with the <code>SSLOffset</code> argument. 
     * 
     * @param tunnelLocation the URL of the HTTP tunnel to be transformed.
     * @param useSSL a flag indicating if an SSL socket should be used instead of plain socket.
     * @param SSLOffset an offset of the port if a non-standard port and SSL should be used.
     * @return a transformed tunnel URL based on the given HTTP tunnel URL and SSL usage.
     * @throws MalformedURLException if <code>tunnelLocation</code> is malformed.
     */
    private static String fixURL(String tunnelLocation, boolean useSSL, int SSLOffset) throws MalformedURLException {
        URL url = new URL(tunnelLocation);
        int port = url.getPort();
        if (port >= 0 && port != 80 && useSSL) {
            port = port + SSLOffset;
        } else if (useSSL && port == 80) {
            port = 443;
        }
        String newURL = new URL(useSSL ? "https" : "http", url.getHost(), port, url.getFile()).toExternalForm();
        return newURL;
    }
}
