package com.topcoder.server.broadcaster;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

abstract class BroadcasterClient extends BroadcasterPoint {

    BroadcasterClient(String name, boolean heartbeat) {
        super(name, heartbeat);
    }

    final void internalStart() {
    }

    abstract InetAddress getHost();

    final boolean connect() {
        int port = getPort();
        InetAddress host = getHost();
        try {
            setSocket(new Socket(host, port));
            info("connected to " + port);
            return true;
        } catch (IOException e) {
            //info("cannot connect to "+host+":"+port+": "+e);
        }
        return false;
    }

    final void shutdown() {
    }

}
