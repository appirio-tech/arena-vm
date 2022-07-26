package com.topcoder.server.broadcaster;

import java.net.InetAddress;
import java.net.UnknownHostException;

abstract class LocalClient extends BroadcasterClient {

    private static final InetAddress LOCAL_HOST = getLocalHost();

    LocalClient(String name) {
        super(name, false);
    }

    InetAddress getHost() {
        return LOCAL_HOST;
    }

    private static InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            throw new RuntimeException("cannot get local host");
        }
    }

}
