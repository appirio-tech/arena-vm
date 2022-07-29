/*
 * ConnectionStatusMonitorProcessorDecorator
 *
 * Created 03/30/2006
 */
package com.topcoder.server.listener;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.topcoder.server.listener.ConnectionStatusMonitor.Listener;
import com.topcoder.shared.util.logging.Logger;

/**
 * This decorator adds connection status monitoring.
 * This ConnectionStatusMonitorProcessorDecorator intercepts calls between the
 * <code>processor</code> (ProcessorInterface) and the <code>listener</code> (ListenerInterface),
 * and it notifies the <code>connectionMonitor</code> (ConnectionStatusMonitor) about events
 * in the connections.
 *
 * When the connectionMonitor reports connections as inactives, this decorator will shutdown the
 * connections on the listener and report them as lost (lostConnectionTemporarily) to
 * the processor
 *
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class ConnectionStatusMonitorProcessorDecorator implements ProcessorInterface {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(ConnectionStatusMonitorProcessorDecorator.class);

    /**
     * connectionMonitor used to detect inactive connections that can be closed
     */
    ConnectionStatusMonitor connectionMonitor;

    /**
     * Decorated processor
     */
    ProcessorInterface processor;

    /**
     * The Listener used by the processor
     */
    ListenerInterface listener;


    /**
     * Creates a new ConnectionStatusMonitorProcessorDecorator for the
     * processor specified as argument.
     *
     * @param processor The processor to be decorated.
     */
    public ConnectionStatusMonitorProcessorDecorator(ProcessorInterface processor) {
        this.connectionMonitor = new ConnectionStatusMonitor();
        this.connectionMonitor.setListener(buildConnectionStatusListener());
        this.processor = processor;
    }

    /**
     * Creates a new ConnectionStatusMonitorProcessorDecorator for the
     * processor specified as argument.
     *
     * @param processor The processor to be decorated.
     * @param scanInterval interval between connections scan when looking for lost connections
     * @param keepAliveTimeout KeepAlive timeout used by the client to send keep alive messages
     */
    public ConnectionStatusMonitorProcessorDecorator(ProcessorInterface processor, long scanInterval, long keepAliveTimeout) {
        this.connectionMonitor = new ConnectionStatusMonitor(scanInterval, keepAliveTimeout);
        this.connectionMonitor.setListener(buildConnectionStatusListener());
        this.processor = processor;
    }

    /**
     * Notifies the closed connection to the connectionMonitor.
     * Delegates the call to the processor.
     */
    public void lostConnection(int connection_id) {
        connectionMonitor.connectionClosed(new Integer(connection_id));
        processor.lostConnection(connection_id);
    }

    /**
     * Notifies the closed connection to the connectionMonitor.
     * Delegates the call to the processor.
     */
    public void lostConnectionTemporarily(int connection_id) {
        connectionMonitor.connectionClosed(new Integer(connection_id));
        processor.lostConnectionTemporarily(connection_id);
    }

    /**
     * Notifies the new connection to the connectionMonitor.
     * Delegates the call to the processor.
     */
    public void newConnection(int connection_id, String remoteIP) {
        connectionMonitor.newConnectionRegistered(new Integer(connection_id));
        processor.newConnection(connection_id, remoteIP);
    }

    /**
     * Notifies to the connectionMonitor that a request
     * has been received for the connection.
     * Delegates the call to the processor
     */
    public void receive(int connection_id, Object request) {
        connectionMonitor.requestReceived(new Integer(connection_id));
        processor.receive(connection_id, request);
    }


    public void setListener(ListenerInterface listener) {
        if (this.listener == null) {
            this.processor.setListener(buildListenerProxy());
        }
        this.listener = listener;
    }

    public void start() {
        connectionMonitor.start();
        processor.start();
    }

    public void stop() {
        processor.stop();
        connectionMonitor.stop();
    }

    /**
     * Builds a monitor listener that will close (lost) all connections reported as inactives
     * by connection monitor.
     *
     * @return The listener
     */
    private Listener buildConnectionStatusListener() {
        return new ConnectionStatusMonitor.Listener() {
            public void inactiveConnectionsDetected(List inactiveConnections) {
                for (Iterator iter = inactiveConnections.iterator(); iter.hasNext();) {
                    Integer connectionId = (Integer) iter.next();
                    try {
                        int id = connectionId.intValue();
                        listener.shutdown(id);
                        processor.lostConnectionTemporarily(id);
                    } catch (Exception e) {
                        log.error("Exception notifying lost connection: "+ connectionId, e);
                    }
                }
            }
        };
    }

    /**
     * Builds a Listener proxy that intercepts calls from the processor
     * to the listener.
     * This proxy will notify to the connectionMonitor
     * when the processor invokes shutdown for a connection.
     *
     * @return The result of the built
     */
    private ListenerInterface buildListenerProxy() {
        return new ListenerInterface() {
            public int getOutTrafficSize() {
                return listener.getOutTrafficSize();
            }

            public int getInTrafficSize() {
                return listener.getInTrafficSize();
            }

            public int getResponseQueueSize() {
                return listener.getResponseQueueSize();
            }

            public int getConnectionsSize() {
                return listener.getConnectionsSize();
            }

            public void banIP(String ipAddress) {
                listener.banIP(ipAddress);
            }

            public void shutdown(int connection_id, boolean notifyProcessor) {
                if (!notifyProcessor) {
                    connectionMonitor.connectionClosed(new Integer(connection_id));
                }
                listener.shutdown(connection_id, notifyProcessor);
            }

            public void shutdown(int connection_id) {
                connectionMonitor.connectionClosed(new Integer(connection_id));
                listener.shutdown(connection_id);
            }

            public void send(int connection_id, Object response) {
                listener.send(connection_id, response);
            }

            public void stop() {
                listener.stop();
            }

            public void start() throws IOException {
                listener.start();
            }

            public int getMinConnectionId() {
                return listener.getMinConnectionId();
            }

            public int getMaxConnectionId() {
                return listener.getMaxConnectionId();
            }

        };
    }
}
