package com.topcoder.server.broadcaster;

final class MITRemoteServer extends BroadcasterServer {

    private final int port;

    MITRemoteServer(int port) {
        super("broadcaster.MITRemoteServer", true);
        this.port = port;
    }

    int getPort() {
        return port;
    }

}
