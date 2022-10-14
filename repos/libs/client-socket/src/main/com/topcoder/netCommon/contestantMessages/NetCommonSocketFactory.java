package com.topcoder.netCommon.contestantMessages;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.security.GeneralSecurityException;

import com.topcoder.client.security.PublicKeyObtainer;
import com.topcoder.netCommon.io.ClientSocket;

/**
 * Defines a utility class which creates client sockets
 * 
 * @author Qi Liu
 * @version $Id: NetCommonSocketFactory.java 72093 2008-08-05 07:34:40Z qliu $
 */
public final class NetCommonSocketFactory {

    private NetCommonSocketFactory() {
    }

    /**
     * Creates a new client socket which connects to the given host and port directly. It uses plain socket.
     * 
     * @param host the host to be connected to.
     * @param port the port to be connected to.
     * @return the client socket.
     * @throws IOException if I/O error occurs.
     */
    public static ClientSocket newClientSocket(String host, int port) throws IOException {
        return newClientSocket(InetAddress.getByName(host), port);
    }

    /**
     * Creates a new client socket which connects to the given host and port directly. It uses plain socket.
     * 
     * @param address the host address to be connected to.
     * @param port the port to be connected to.
     * @return the client socket.
     * @throws IOException if I/O error occurs.
     */
    public static ClientSocket newClientSocket(InetAddress address, int port) throws IOException {
        return newClientSocket(new Socket(address, port));
    }

    /**
     * Creates a new client socket which wraps the given underlying communication socket.
     * 
     * @param socket the socket to be wrapped.
     * @return the client socket.
     * @throws IOException if I/O error occurs.
     */
    public static ClientSocket newClientSocket(Socket socket) throws IOException {
        return newClientSocket(socket, "");
    }

    /**
     * Creates a new client socket which wraps the given underlying communication socket or connects via the tunneling
     * location. It initializes the encryption/decryption key according to the property resource.
     * 
     * @param socket the socket to be wrapped.
     * @param tunnelLocation the HTTP tunneling URL.
     * @return the created client socket.
     * @throws IOException
     */
    private static ClientSocket newClientSocket(Socket socket, String tunnelLocation) throws IOException {
        try {
            return new ClientSocket(socket, new NetCommonCSHandler(PublicKeyObtainer.obtainPublicKey()), tunnelLocation);
        } catch (GeneralSecurityException e) {
            throw (IOException) new IOException("Encryption property invalid.").initCause(e);
        }
    }

}
