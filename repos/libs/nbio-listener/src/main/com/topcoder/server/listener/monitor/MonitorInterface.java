package com.topcoder.server.listener.monitor;

public interface MonitorInterface {

    void newConnection(int id, String remoteIP);

    void lostConnection(int id);

    void bytesRead(int id, int numBytes);

    void associateConnections(int existentConnectionId, int newConnectionID);
}
