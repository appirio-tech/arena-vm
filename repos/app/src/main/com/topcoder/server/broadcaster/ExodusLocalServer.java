package com.topcoder.server.broadcaster;

final class ExodusLocalServer extends LocalServer {

    ExodusLocalServer() {
        super("broadcaster.ExodusLocalServer");
    }

    int getPort() {
        return BroadcasterProps.getExodusLocalPort();
    }

}
