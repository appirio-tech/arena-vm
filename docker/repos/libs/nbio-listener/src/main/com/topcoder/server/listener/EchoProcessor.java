package com.topcoder.server.listener;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple echo processor. It returns every message back.
 */
public final class EchoProcessor implements ProcessorInterface {

    private final Set connections = new HashSet();
    private ListenerInterface controller;

    /**
     * Creates a new echo processor.
     */
    public EchoProcessor() {
    }

    public void setListener(ListenerInterface controller) {
        this.controller = controller;
    }

    public void start() {
    }

    public void stop() {
    }

    public void newConnection(int id, String remoteIP) {
        assertTrue(connections.add(new Integer(id)));
    }

    public void receive(int id, Object request) {
        assertTrue(connections.contains(new Integer(id)));
        controller.send(id, request);
    }

    public void lostConnection(int id) {
         assertTrue(connections.remove(new Integer(id)));
    }
    
    public void lostConnectionTemporarily(int connection_id) {
        assertTrue(connections.remove(new Integer(connection_id)));
    }

    private void assertTrue(boolean b) {
        if (!b) {
            throw new RuntimeException("assertion failed!");
        }
    }

    /**
     * Returns the name of this class.
     *
     * @return  the name of this class.
     */
    public String toString() {
        return "EchoProcessor";
    }

}
