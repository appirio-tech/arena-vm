/*
 * CompositeListener
 *
 * Created 4/03/2007
 */
package com.topcoder.server.listener;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.topcoder.shared.util.logging.Logger;



/**
 * CompositeListener provides a way to handle a set of Listeners as
 * a single one.<p>
 *
 * It is required that all listeners added to this CompositeListener
 * use disjoint connection id ranges. No id conversion is done by this Listener
 * in order to avoid unnecessary tasks.<p>
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class CompositeListener implements ListenerInterface {
    private final Logger log = Logger.getLogger(CompositeListener.class);
    private ListenerInterface[] listeners;
    ProcessorInterface processor;
    private int[] maxValues;
    private int minConnectionId;
    private int maxConnectionId;

    public CompositeListener(List listeners, ProcessorInterface processor) {
        if (listeners.size() < 2) {
            throw new IllegalArgumentException("The list must contain at least two Listeners.");
        }
        this.processor = processor;
        this.listeners = (ListenerInterface[]) listeners.toArray(new ListenerInterface[listeners.size()]);
        intialize();
    }


    private void intialize() {
        Arrays.sort(listeners, new Comparator(){
            public int compare(Object o1, Object o2) {
                ListenerInterface i1 = (ListenerInterface) o1;
                ListenerInterface i2 = (ListenerInterface) o2;
                return (i1.getMinConnectionId() < i2.getMinConnectionId() ? -1 : (i1.getMinConnectionId() == i2.getMinConnectionId() ? 0 : 1));
            }
        });
        minConnectionId = listeners[0].getMinConnectionId();
        maxConnectionId = listeners[listeners.length-1].getMaxConnectionId();
        checkOverlaps();
        initializeMaxValues();
        processor.setListener(this);
    }

    /**
     * Checks that id ranges don't overlap between listeners
     */
    private void checkOverlaps() {
        for (int i = 0; i < listeners.length-1; i++) {
            if (listeners[i].getMaxConnectionId() >= listeners[i+1].getMinConnectionId()) {
                throw new IllegalArgumentException("Listeners range overlaps");
            }
        }
    }

    /**
     * Cache max connection id values for performance reasons
     */
    private void initializeMaxValues() {
        maxValues = new int[listeners.length];
        for (int i = 0; i < listeners.length; i++) {
            maxValues[i] = listeners[i].getMaxConnectionId();
        }
    }


    public void banIP(String ipAddress) {
        if (log.isDebugEnabled()) {
            log.debug("banIP: "+ipAddress);
        }
        for (int i = 0; i < listeners.length; i++) {
            if (log.isDebugEnabled()) {
                log.debug("Fowarding to: "+listeners[i]);
            }
            listeners[i].banIP(ipAddress);
        }
    }

    public int getConnectionsSize() {
        if (log.isDebugEnabled()) {
            log.debug("getConnectionsSize");
        }
        int value = 0;
        for (int i = 0; i < listeners.length; i++) {
            value += listeners[i].getConnectionsSize();
        }
        return value;

    }

    public int getInTrafficSize() {
        if (log.isDebugEnabled()) {
            log.debug("getInTrafficSize");
        }
        int value = 0;
        for (int i = 0; i < listeners.length; i++) {
            value += listeners[i].getInTrafficSize();
        }
        return value;
    }

    public int getOutTrafficSize() {
        if (log.isDebugEnabled()) {
            log.debug("getOutTrafficSize");
        }
        int value = 0;
        for (int i = 0; i < listeners.length; i++) {
            value += listeners[i].getOutTrafficSize();
        }
        return value;
    }

    public int getResponseQueueSize() {
        if (log.isDebugEnabled()) {
            log.debug("getResponseQueueSize");
        }
        int value = 0;
        for (int i = 0; i < listeners.length; i++) {
            value += listeners[i].getResponseQueueSize();
        }
        return value;
    }

    public void send(int connection_id, Object response) {
        if (log.isDebugEnabled()) {
            log.debug("send on connection: "+connection_id+" response:"+response);
        }
        getListener(connection_id).send(connection_id, response);
    }

    public void shutdown(int connection_id, boolean notifyProcessor) {
        if (log.isDebugEnabled()) {
            log.debug("shutdown on connection: "+connection_id+" notify:"+notifyProcessor);
        }
        getListener(connection_id).shutdown(connection_id, notifyProcessor);
    }

    public void shutdown(int connection_id) {
        if (log.isDebugEnabled()) {
            log.debug("shutdown on connection: "+connection_id);
        }
        getListener(connection_id).shutdown(connection_id);
    }

    public void start() throws IOException {
        processor.start();
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].start();
        }
    }

    public void stop() {
        for (int i = 0; i < listeners.length; i++) {
            listeners[i].stop();
        }
        processor.stop();
    }

    private final ListenerInterface getListener(int connection_id) {
        for (int i = 0; i < maxValues.length; i++) {
            if (connection_id < maxValues[i]) {
                if (log.isDebugEnabled()) {
                    log.debug("Found listener for connection. listener index="+i+" listener="+listeners[i]);
                }
                return listeners[i];
            }
        }
        log.error("Could not find listener for connection. "+connection_id);
        throw new IllegalArgumentException("The connection id="+connection_id+" does not belong to any listener range");
    }

    public int getMaxConnectionId() {
        return maxConnectionId;
    }

    public int getMinConnectionId() {
        return minConnectionId;
    }
}
