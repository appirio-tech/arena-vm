package com.topcoder.server.AdminListener;

import java.net.ServerSocket;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Category;

import com.topcoder.shared.util.SerialIntGenerator;

public class ClientConnectionSet {

    private AdminProcessor processor;
    private ServerSocket acceptorSocket;
    private SerialIntGenerator generator;
    private Map activeConnections = new ConcurrentHashMap();
//    private int listenPort;
    private ClientConnection nextConnection = null;
    private static final Category log = Category.getInstance(ClientConnectionSet.class);

    ClientConnectionSet(AdminProcessor processor, int listenPort) throws Exception {
        this.processor = processor;
//        this.listenPort = listenPort;
        generator = new SerialIntGenerator(AdminConstants.FIRST_CLIENT_CONNECTION_ID);

        acceptorSocket = new ServerSocket(listenPort);
    }

    void start() {
        startReaderThread();
    }

    private void startReaderThread() {
        int nextConnectionId = generator.next();
        if (log.isDebugEnabled())
            log.debug("Preparing reader thread for next connection (" + nextConnectionId + ")");
        nextConnection = new ClientConnection(acceptorSocket, nextConnectionId, this);
    }

    void closeConnection(int connectionId) {
        if (log.isDebugEnabled())
            log.debug("Closing connection " + connectionId);
        ClientConnection conn = (ClientConnection) activeConnections.get(new Integer(connectionId));
        if (conn == null) {
            log.error("Attempt to close inactive connection " + connectionId);
            return;
        }
        conn.close();
        // dpecora - Remove connection from active lists as well
        lostConnection(connectionId);
    }

    void stop() {
        if (log.isDebugEnabled())
            log.debug("Stopping client connection set...");
        try {
            acceptorSocket.close();
        } catch (Exception e) {
            log.error("Error closing acceptor socket", e);
        }
        if (nextConnection != null)
            nextConnection.close();
        synchronized (activeConnections) {
            Iterator it = activeConnections.values().iterator();
            while (it.hasNext()) {
                ClientConnection conn = (ClientConnection) it.next();
                conn.close();
            }
        }
    }

    void enqueue(int senderId, Object obj) {
        processor.receive(senderId, obj);
    }

    void writeObject(Object o, int connectionId) {
        ClientConnection conn = (ClientConnection) activeConnections.get(new Integer(connectionId));
        if (conn == null) {
            log.error("Write request to inactive connection " + connectionId);
            // dpecora - Re-inform processor that this connection doesn't exist anymore.
            processor.lostConnection(connectionId);
            return;
        }
        conn.writeObject(o);
    }

    void broadcast(Object o) {
        synchronized (activeConnections) {
            Iterator it = activeConnections.values().iterator();
            while (it.hasNext()) {
                ClientConnection conn = (ClientConnection) it.next();
                conn.writeObject(o);
            }
        }
    }

    void gotConnection(int connectionId, String remoteIp) {
        if (log.isDebugEnabled())
            log.debug("Got connection: " + connectionId + ", " + remoteIp);
        activeConnections.put(new Integer(connectionId), nextConnection);
        startReaderThread();
        processor.newConnection(connectionId, remoteIp);
    }

    void lostConnection(int connectionId) {
        if (log.isDebugEnabled())
            log.debug("Lost connection: " + connectionId);
        try {
            processor.lostConnection(connectionId);
        } finally {
            activeConnections.remove(new Integer(connectionId));
        }
    }
}

