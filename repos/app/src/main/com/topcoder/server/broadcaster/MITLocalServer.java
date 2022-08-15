package com.topcoder.server.broadcaster;

final class MITLocalServer extends LocalServer {

    MITLocalServer() {
        super("broadcaster.MITLocalServer");
    }

    int getPort() {
        return BroadcasterProps.getMITLocalPort();
    }

}
