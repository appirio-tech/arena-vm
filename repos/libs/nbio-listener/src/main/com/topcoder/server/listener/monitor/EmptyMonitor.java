package com.topcoder.server.listener.monitor;

public final class EmptyMonitor implements MonitorInterface {

    public void newConnection(int id, String remoteIP) {
    }

    public void lostConnection(int id) {
    }

    public void bytesRead(int id, int numBytes) {
    }

    public void associateConnections(int existentConnectionId, int newConnectionID) {
    }
}
