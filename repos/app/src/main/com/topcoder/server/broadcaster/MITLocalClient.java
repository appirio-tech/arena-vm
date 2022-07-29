package com.topcoder.server.broadcaster;

public final class MITLocalClient extends LocalClient {

    public MITLocalClient() {
        super("broadaster.MITLocalClient");
    }

    int getPort() {
        return BroadcasterProps.getMITLocalPort();
    }

}
