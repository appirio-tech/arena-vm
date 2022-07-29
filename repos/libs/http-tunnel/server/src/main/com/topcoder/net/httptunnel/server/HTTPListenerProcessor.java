/*
 * HTTPListenerProcessor
 *
 * Created Apr 6, 2007
 */
package com.topcoder.net.httptunnel.server;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.topcoder.server.listener.ConnectionStatusMonitor;
import com.topcoder.server.listener.KeepAliveProperties;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;
import com.topcoder.server.listener.ConnectionStatusMonitor.Listener;
import com.topcoder.shared.util.logging.Logger;

final class HTTPListenerProcessor implements ProcessorInterface {
    private Logger log = Logger.getLogger(HTTPListenerProcessor.class);

    /**
     * connectionId->HTTPConnection map.
     */
    private final Map connections = new ConcurrentHashMap();
    /**
     * HTTPHandler that manages HTTP connection events
     */
    private final HTTPHandler handler;
    /**
     * The listener for interacting with connections
     */
    private ListenerInterface listener;
    /**
     * The Connection monitor detects unused connections and notifies them as Lost.
     * It manages InputConnections and OutputConnection, so in the later case every time
     * a response is sent to an output connection we need to notify the monitor about an activity, and we
     * do this using the method notify requestReceived.
     */
    private final ConnectionStatusMonitor connectionMonitor =
                    new ConnectionStatusMonitor(KeepAliveProperties.getScanInterval(), KeepAliveProperties.getHttpTimeout());

    HTTPListenerProcessor(HTTPHandler handler) {
        this.handler = handler;
        this.connectionMonitor.setListener(buildConnectionStatusListener());
    }

    public void receive(int connection_id, Object request) {
        HTTPConnection conn = (HTTPConnection) connections.get(new Integer(connection_id));
        if (conn == null) {
            listener.shutdown(connection_id);
            return;
        }
        try {
            conn.handleReceive(request);
        } catch (Exception e) {
            log.error("Unexpected exception in connection : " + conn, e);
            dropConnectionAsLost(conn);
        }
    }

    public void newConnection(int connection_id, String remoteIP) {
        Integer cnnId = new Integer(connection_id);
        connections.put(cnnId, new HTTPConnection(cnnId, remoteIP, this));
        connectionMonitor.newConnectionRegistered(cnnId);
    }

    public void lostConnectionTemporarily(int connection_id) {
        HTTPConnection conn = endConnection(new Integer(connection_id));
        if (conn != null) {
            conn.handleConnectionLost();
        }
    }

    public void lostConnection(int connection_id) {
        HTTPConnection conn = endConnection(new Integer(connection_id));
        if (conn != null) {
            conn.handleConnectionLost();
        }
    }

    public void stop() {
        connectionMonitor.stop();
    }

    public void start() {
        connectionMonitor.start();
    }
    
    public void setListener(ListenerInterface listener) {
        this.listener = listener;
    }

    private HTTPConnection endConnection(Integer cnnId) {
        connectionMonitor.connectionClosed(cnnId);
        return (HTTPConnection) connections.remove(cnnId);
    }

    void shutdown(Integer id) {
        endConnection(id);
        listener.shutdown(id.intValue());
    }

    void send(Integer id, Object object) {
        connectionMonitor.responseSent(id);
        listener.send(id.intValue(), object);
    }

    void notifyProcessIncoming(HTTPConnection connection, HTTPRequest req) {
        connectionMonitor.requestReceived(connection.getId());
        handler.processIncoming(connection, req);
    }

    void notifyProcessIncoming(HTTPConnection connection, HTTPChunkedContent req) {
        connectionMonitor.requestReceived(connection.getId());
        handler.processIncoming(connection, req);
    }

    void notifyConnectionLost(HTTPConnection conn) {
        handler.processConnectionLost(conn);
    }

    private void dropConnectionAsLost(HTTPConnection conn) {
        shutdown(conn.getId());
        conn.handleConnectionLost();
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
                        HTTPConnection conn = (HTTPConnection) connections.get(connectionId);
                        dropConnectionAsLost(conn);
                    } catch (Exception e) {
                        log.error("Exception notifying lost connection: "+ connectionId, e);
                    }
                }
            }
        };
    }

    public interface HTTPHandler {

        void processIncoming(HTTPConnection connection, HTTPChunkedContent req);

        void processConnectionLost(HTTPConnection conn);

        void processIncoming(HTTPConnection connection, HTTPRequest req);
    }
}