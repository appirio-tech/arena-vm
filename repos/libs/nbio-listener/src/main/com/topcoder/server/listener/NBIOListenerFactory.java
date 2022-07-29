package com.topcoder.server.listener;

import java.util.Collection;

import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.monitor.MonitorInterface;

/**
 * The factory class for creating <code>NBIOListener</code> instances.
 *
 * @author  Timur Zambalayev
 */
public final class NBIOListenerFactory extends ListenerFactory {

    /**
     * Creates a new instance of this class.
     */
    public NBIOListenerFactory() {
    }

    public ListenerInterface createListener(String ipAddress, int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ips, boolean isAllowedSet, int minConnectiondId, int maxConnectionId) {
        return new NBIOListener(ipAddress, port, processor, numAcceptThreads, numReadThreads, numWriteThreads, monitor,
                csHandlerFactory, ips, isAllowedSet, minConnectiondId, maxConnectionId);
    }

    /**
     * Returns the name of this class.
     *
     * @return  the name of this class.
     */
    public String toString() {
        return "NBIOListenerFactory";
    }

}
