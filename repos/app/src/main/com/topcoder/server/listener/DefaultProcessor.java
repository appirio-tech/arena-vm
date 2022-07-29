package com.topcoder.server.listener;

import com.topcoder.server.listener.monitor.ArenaMonitor;
import com.topcoder.server.processor.RequestProcessor;
import com.topcoder.server.processor.ResponseProcessor;

/**
 * The default processor. Represents the processor component from the <code>processor</code> package.
 *
 * @author  Timur Zambalayev
 */
public final class DefaultProcessor implements ArenaProcessor {

    /**
     * Creates a new default processor.
     */
    public DefaultProcessor() {
    }

    public void setListener(ListenerInterface controller) {
        ResponseProcessor.setListener(controller);
    }

    public void start() {
        RequestProcessor.start();
    }

    public void stop() {
        RequestProcessor.stop();
    }

    public void newConnection(int connection_id, String remoteIP) {
        RequestProcessor.newConnection(connection_id, remoteIP);
    }

    public void receive(int connection_id, Object request) {
        RequestProcessor.process(connection_id, request);
    }

    public void lostConnection(int connection_id) {
        RequestProcessor.lostConnection(connection_id);
    }
    
    //this will start timer and queueing for messages
    public void lostConnectionTemporarily(int connection_id) {
        RequestProcessor.handleLostConnection(connection_id, false);
    }

    /**
     * Returns the name of this class.
     *
     * @return  the name of this class.
     */
    public String toString() {
        return "DefaultProcessor";
    }

    public void setArenaMonitor(ArenaMonitor monitor) {
        RequestProcessor.setMonitor(monitor);
    }
}
