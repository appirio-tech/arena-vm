package com.topcoder.server.broadcaster;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;

abstract class BroadcasterServer extends BroadcasterPoint {

    private ServerSocket serverSocket;

    BroadcasterServer(String name, boolean heartbeat) {
        super(name, heartbeat);
    }

    final void internalStart() {
        int port = getPort();
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(50);
            info("bound to " + port);
            info("up and running");
        } catch (IOException e) {
            throw new RuntimeException("cannot create server socket, port=" + port + ": " + e);
        }
    }

    final boolean connect() {
        try {
            Socket socket;
            try {
                socket = serverSocket.accept();
                setSocket(socket);
            } catch (InterruptedIOException e) {
                return false;
            }
            info("accepted socket: " + socket);
            return true;
        } catch (IOException e) {
            info("cannot accept a socket: " + e);
        }
        return false;
    }

    final void shutdown() throws IOException {
        serverSocket.close();
    }

}
