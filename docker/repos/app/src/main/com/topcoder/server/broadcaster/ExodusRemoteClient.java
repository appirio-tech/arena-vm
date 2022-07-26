package com.topcoder.server.broadcaster;

import java.net.InetAddress;
import java.net.UnknownHostException;

final class ExodusRemoteClient extends BroadcasterClient {

    private final InetAddress host;
    private final int port;

    ExodusRemoteClient(String host, int port) {
        super("broadcaster.ExodusRemoteClient", true);
        this.port = port;
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            throw new RuntimeException("cannot resolve host=" + host + ", " + e);
        }
    }

    InetAddress getHost() {
        return host;
    }

    int getPort() {
        return port;
    }

}
