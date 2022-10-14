/*
 * ConnectionStatusMonitor
 *
 * Created 03/17/2006
 */
package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.topcoder.shared.util.StoppableThread;
import com.topcoder.shared.util.logging.Logger;

/**
 * This monitor scans registered connections at a specified interval,
 * looking for the inactive ones. All connections detected as inactive will be
 * reported to the <code>listener</code> object.
 * Users of this class must notify every time a connection is registered, closed, or when an incoming
 * request arrives.
 * If a request for a connection hasn't been received for over 2*keepAliveTimeout milliseconds,
 * the connection will be reported as inactive and removed from the registered connections
 * in the next scan.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ConnectionStatusMonitor {
    /**
     * Logger for this class
     */
    private static final Logger log = Logger.getLogger(ConnectionStatusMonitor.class);

    /**
     * LinkedHashMap containing all registered. Last access descending order.
     */
    private LinkedHashMap connections =  new LinkedHashMap(50, 0.75f, true);

    /**
     * Thread used to scan for inactive connections
     */
    private StoppableThread inactiveConnectionsNotifier = new StoppableThread(buildClientThread(), "connection-scanner");

    /**
     * Listener that will be notified of all inactive connections found in each scan
     */
    private Listener listener ;

    /**
     * Represents the time (in millisenconds) between scans
     */
    private long inactiveConnectionScanInterval;

    /**
     * This is the connection keep-alive timeout (in milliseconds) used to detect inactive connections
     */
    private long keepAliveTimeout;


    /**
     * Creates a new ConnectionStatusMonitor with the scan interval value and the
     * keep-alive timeout value provided by the KeepAliveProperties
     */
    public ConnectionStatusMonitor() {
        this(KeepAliveProperties.getScanInterval(), KeepAliveProperties.getTimeout());
    }

    /**
     * Creates a new ConnectionStatusMonitor with the scan interval value and the
     * keep-alive timeout specified as arguments
     *
     * @param scanIntervalMs Time (in millisenconds) between scans
     * @param timeOutMs Keep-alive timeout (in millisenconds) used to detect inactive connectins
     */
    public ConnectionStatusMonitor(long scanIntervalMs, long timeOutMs) {
        this.inactiveConnectionScanInterval = scanIntervalMs;
        this.keepAliveTimeout = timeOutMs;
        if (log.isDebugEnabled())   {
            log.debug("Initializing ConnectionStatusMonitor [scanInterval=" + inactiveConnectionScanInterval +
                    ", keepAliveTimeout=" + keepAliveTimeout +"]" );
        }
    }


    /**
     * Notifies this monitor about a new registered connection
     *
     * @param id Connection Id of the new registered connection
     */
    public void newConnectionRegistered(Integer id) {
        synchronized (connections) {
            connections.put(id, new Long(System.currentTimeMillis()));
        }
        if (log.isDebugEnabled()) {
            log.debug("new connection reported: "+ id);
        }
    }

    /**
     * Notifies this monitor that new incoming request arrives
     *
     * @param id Connection Id of the request destiny
     */
    public void requestReceived(Integer id) {
        synchronized (connections) {
            if (connections.containsKey(id)) {
                connections.put(id, new Long(System.currentTimeMillis()));
                if (log.isDebugEnabled()) {
                    log.debug("incoming request reported for connection: "+ id);
                }
            }
        }
    }

    /**
     * Notifies this monitor that a response is being sent
     *
     * @param id Connection Id
     */
    public void responseSent(Integer id) {
        synchronized (connections) {
            if (connections.containsKey(id)) {
                connections.put(id, new Long(System.currentTimeMillis()));
                if (log.isDebugEnabled()) {
                    log.debug("outgoing response reported for connection: "+ id);
                }
            }
        }
    }

    /**
     * Notifies this monitor that a connection has been closed
     *
     * @param id Connection Id of the closed connection
     */
    public void connectionClosed(Integer id) {
        synchronized (connections) {
            connections.remove(id);
        }
        if (log.isDebugEnabled()) {
            log.debug("connection-closed reported: "+ id);
        }
    }

    /**
     * Set the listener that will be notified when the scan process detects
     * inactive connections
     *
     * @param inactiveConnectionsListener the Listener to be set
     */
    public void setListener(Listener inactiveConnectionsListener) {
        this.listener = inactiveConnectionsListener;
    }

    /**
     * Starts this ConnectionStatusMonitor
     */
    public void start() {
        log.info("starting");
        synchronized (connections) {
            connections.clear();
        }
        inactiveConnectionsNotifier.start();
        log.info("started");
    }

    /**
     * Stops this ConnectionStatusMonitor
     */
    public void stop() {
        log.info("stopping");
        try {
            inactiveConnectionsNotifier.stopThread();
        } catch (InterruptedException e) {
            log.info("Thread ["+Thread.currentThread().getName()+"] interrupted");
        }
        log.info("stopped");
    }


    /**
     * Builds a client thread that scans all registered connections
     *
     * @return the result of the build
     */
    private StoppableThread.Client buildClientThread() {
        return new StoppableThread.Client() {
            public void cycle() throws InterruptedException {
                Thread.sleep(inactiveConnectionScanInterval);

                long connectionMinTime = System.currentTimeMillis() - 2 * keepAliveTimeout;
                if (log.isDebugEnabled()) {
                    log.debug("scanning for inactive connections with last activity before than: " + connectionMinTime);
                }

                List inactiveConnections = new ArrayList();
                synchronized (connections) {
                    for (Iterator it = connections.entrySet().iterator(); it.hasNext();) {
                        Map.Entry entry = (Map.Entry) it.next();
                        if ( ((Long) entry.getValue()).longValue() >= connectionMinTime) break;
                        inactiveConnections.add(entry.getKey());
                        it.remove();
                    }
                }

                if (inactiveConnections.size() > 0) {
                    notifyInactiveConnnections(inactiveConnections);
                }
            }
        };
    }

    /**
     * Notifies inactive connection to the listener if it has been set
     *
     * @param inactiveConnections inactive-connection-id List to notify to the listener
     */
    private void notifyInactiveConnnections(List inactiveConnections) {
        if (listener != null) {
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Notifying inactive connections: [" + inactiveConnections + "]");
                }
                listener.inactiveConnectionsDetected(inactiveConnections);
            } catch (Exception e) {
                log.error("Exception thrown notifying innactive connections", e);
            }
        }
    }


    /**
     * The listener interface for receiving inactive connections notification.
     */
    public interface Listener {

        /**
         * Invoked when inactive connections have been detected
         *
         * @param inactiveConnections List of inactive connections detected
         */
        void inactiveConnectionsDetected(List inactiveConnections);
    }
}
