package com.topcoder.server.broadcaster;

public final class ExodusBroadcaster extends Broadcaster {

    public ExodusBroadcaster(String host, int remotePort) {
        super(new ExodusRemoteClient(host, remotePort), new ExodusLocalServer());
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: com.topcoder.server.broadcaster.ExodusBroadcaster <host> <port>");
            return;
        }
        String host = args[0];
        int remotePort = Integer.parseInt(args[1]);
        Broadcaster broadcaster = new ExodusBroadcaster(host, remotePort);
        broadcaster.start();
    }

}
