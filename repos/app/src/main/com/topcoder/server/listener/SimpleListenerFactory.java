package com.topcoder.server.listener;

import java.util.Collection;

import com.topcoder.server.listener.monitor.MonitorInterface;
import com.topcoder.shared.netCommon.CSHandlerFactory;

/**
 * The factory class for creating <code>SimpleListener</code> instances.
 *
 * @author  Timur Zambalayev
 */
public final class SimpleListenerFactory extends ListenerFactory {

    /**
     * Creates a new instance of this class.
     */
    public SimpleListenerFactory() {
    }

    public ListenerInterface createListener(String ipAddress, int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ipsSet, boolean isAllowedSet, int minConnectionId, int maxConnectionId) {
        return new SimpleListener(port, processor, monitor, minConnectionId, maxConnectionId);
    }

    /**
     * Returns the name of this class.
     *
     * @return  the name of this class.
     */
    public String toString() {
        return "SimpleListenerFactory";
    }

}
