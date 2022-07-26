package com.topcoder.server.broadcaster;

abstract class Broadcaster {

    private final BroadcasterPoint remotePoint;
    private final LocalServer localServer;

    Broadcaster(BroadcasterPoint remotePoint, LocalServer localServer) {
        this.remotePoint = remotePoint;
        this.localServer = localServer;
        remotePoint.setSender(localServer);
        localServer.setSender(remotePoint);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            public void run() {
                stop();
            }
        }));
    }

    final BroadcasterPoint getRemotePoint() {
        return remotePoint;
    }

    public final void start() {
        remotePoint.start();
        localServer.start();
    }

    final void stop() {
        localServer.stop();
        remotePoint.stop();
    }

}
