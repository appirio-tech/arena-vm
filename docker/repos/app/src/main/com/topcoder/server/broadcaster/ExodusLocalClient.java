package com.topcoder.server.broadcaster;

public final class ExodusLocalClient extends LocalClient {

    public ExodusLocalClient() {
        super("broadaster.ExodusLocalClient");
    }

    int getPort() {
        return BroadcasterProps.getExodusLocalPort();
    }

    public Object receive() throws InterruptedException {
        return super.receive();
    }

}
