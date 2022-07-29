/*
* Copyright (C) - 2014 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.listener;

import java.util.Collection;

import com.topcoder.shared.netCommon.CSHandlerFactory;
import com.topcoder.server.listener.monitor.EmptyMonitor;
import com.topcoder.server.listener.monitor.MonitorInterface;

/**
 * <p>
 * Changes in version 1.1 (Make Admin Listener Work With Main Listener Through Loopback Address v1.0)
 * <ol>
 *      <li>Add {@link #createListener(String ipAddress, int port, ProcessorInterface processor, MonitorInterface monitor,
 *           int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
 *           Collection ips, boolean isAllowedSet)} method.</li>
 * </ol>
 * </p>
 * @author savon_cn
 * @version 1.1
 */
public abstract class ListenerFactory {

    public final ListenerInterface createListener(int port, ProcessorInterface processor, CSHandlerFactory csHandlerFactory) {
        return createListener(port, processor, new EmptyMonitor(), csHandlerFactory);
    }

    public final ListenerInterface createListener(int port, ProcessorInterface processor, MonitorInterface monitor, CSHandlerFactory csHandlerFactory) {
        return createListener(port, processor, monitor, 0, 0, 0, csHandlerFactory);
    }

    public final ListenerInterface createListener(int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory) {
        return createListener(port, processor, monitor, numAcceptThreads, numReadThreads, numWriteThreads,
                csHandlerFactory, null, false);
    }

    public final ListenerInterface createListener(int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ips, boolean isAllowedSet) {
        return createListener(port, processor, monitor, numAcceptThreads, numReadThreads, numWriteThreads, csHandlerFactory,
                ips, isAllowedSet, 0, Integer.MAX_VALUE);
    }
    /**
     * Create the listener
     * @param ipAddress the ipaddress
     * @param port the port
     * @param processor the processor
     * @param monitor the monitor
     * @param numAcceptThreads the accepted threads
     * @param numReadThreads the read threads
     * @param numWriteThreads the write threads
     * @param csHandlerFactory the cs handler factory
     * @param ips the ips
     * @param isAllowedSet whether it is allowed set.
     * @return
     */
    public final ListenerInterface createListener(String ipAddress, int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ips, boolean isAllowedSet) {
        return createListener(ipAddress, port, processor, monitor, numAcceptThreads, numReadThreads, numWriteThreads, csHandlerFactory,
                ips, isAllowedSet, 0, Integer.MAX_VALUE);
    }

    public final ListenerInterface createListener(int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ips, boolean isAllowedSet, int minConnectionId, int maxConnectionId) {
        return createListener(null, port, processor, monitor, numAcceptThreads, numReadThreads,
                numWriteThreads, csHandlerFactory, ips, isAllowedSet, minConnectionId, maxConnectionId);
    }

    public abstract ListenerInterface createListener(String ipAddress, int port, ProcessorInterface processor, MonitorInterface monitor,
            int numAcceptThreads, int numReadThreads, int numWriteThreads, CSHandlerFactory csHandlerFactory,
            Collection ips, boolean isAllowedSet, int minConnectionId, int maxConnectionId);

}
