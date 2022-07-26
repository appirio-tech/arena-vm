package com.topcoder.server.broadcaster;

abstract class LocalServer extends BroadcasterServer {

    LocalServer(String name) {
        super(name, false);
    }

}
