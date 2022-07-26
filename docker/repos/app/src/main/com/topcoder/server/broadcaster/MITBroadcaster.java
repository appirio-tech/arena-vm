package com.topcoder.server.broadcaster;

public final class MITBroadcaster extends Broadcaster {

    public MITBroadcaster(int remotePort) {
        super(new MITRemoteServer(remotePort), new MITLocalServer());
    }

    int getUnconfirmedSize() {
        return getRemotePoint().getUnconfirmedSize();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: com.topcoder.server.broadcaster.MITBroadcaster <port>");
            return;
        }
        int remotePort = Integer.parseInt(args[0]);
        Broadcaster broadcaster = new MITBroadcaster(remotePort);
        broadcaster.start();
    }

}
