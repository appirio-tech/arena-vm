/*
 * ProcessorForListenerAdapter
 *
 * Created 06/27/2006
 */
package com.topcoder.farm.server.net.connection.listener;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.shared.net.connection.impl.ConnectionKeepAliveMessage;
import com.topcoder.server.listener.ListenerInterface;
import com.topcoder.server.listener.ProcessorInterface;

/**
 * ProcessorInterface implementation for NBIOListenerToConnectionListenerAdapter class
 *
 * This class is responsible for listen to the NBIOListener and transform events and invocations
 * between them
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorForListenerAdapter implements ProcessorInterface {
    private Log log = LogFactory.getLog(ProcessorForListenerAdapter.class);
    private ListenerInterface listener;
    private NBIOListenerToConnectionListenerAdapter adapter;

    /**
     * This map contains all active connections of the listener
     */
    private Map connections = new ConcurrentHashMap();

    /**
     * Creates a new ProcessorForListenerAdapter for the given adapter
     *
     * @param adapter The adapter this processor belongs to.
     */
    public ProcessorForListenerAdapter(NBIOListenerToConnectionListenerAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#setListener(com.topcoder.server.listener.ListenerInterface)
     */
    public void setListener(ListenerInterface listener) {
       this.listener = listener;
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#start()
     */
    public void start() {
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#stop()
     */
    public void stop() {
        connections.clear();
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#newConnection(int, java.lang.String)
     */
    public void newConnection(int connection_id, String remoteIP) {
        if (log.isDebugEnabled()) log.debug("new connection: " + connection_id + " from IP: " + remoteIP);
        ListenerToConnectionAdapter connection = new ListenerToConnectionAdapter(connection_id, remoteIP, listener);
        addConnection(connection);
        notifyNewEndPointConnection(connection);
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#receive(int, java.lang.Object)
     */
    public void receive(int connection_id, Object request) {
        if (log.isDebugEnabled()) log.debug("message received for connection: " + connection_id);
        if (request instanceof ConnectionKeepAliveMessage) {
            if (log.isDebugEnabled()) {
                log.debug("Connection: "+connection_id+" keepalive "+request);
            }
            //we return the same message back
            listener.send(connection_id, request);
            return;
        }
        getConnection(connection_id).handleReceived(request);
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#lostConnection(int)
     */
    public void lostConnection(int connection_id) {
        if (log.isDebugEnabled()) log.debug("connection closed: " + connection_id);
        final ListenerToConnectionAdapter cnn = removeConnection(connection_id);
        if (cnn != null) {
            cnn.handleConnectionClosed();
        }
    }

    /**
     * @see com.topcoder.server.listener.ProcessorInterface#lostConnectionTemporarily(int)
     */
    public void lostConnectionTemporarily(int connection_id) {
        if (log.isDebugEnabled()) log.debug("connection lost: " + connection_id);
        final ListenerToConnectionAdapter cnn = removeConnection(connection_id);
        if (cnn != null) {
            cnn.handleConnectionLost();
        }
    }


    /**
     * Notifies through the adapter about the new connection
     * @param connection new Connection
     */
    protected void notifyNewEndPointConnection(ListenerToConnectionAdapter connection) {
        adapter.notifyNewConnection(connection);
    }

    /**
     * Adds the connection to the connections map
     *
     * @param connection connection to add
     */
    private void addConnection(ListenerToConnectionAdapter connection) {
        connections.put(new Integer(connection.getConnectionId()), connection);
    }

    /**
     * Gets the ListenerToConnectionAdapter identified with the specified id
     *
     * @param connection_id id of the connection
     *
     * @return The connection
     * @throws IllegalStateException if the id is not found
     */
    private ListenerToConnectionAdapter getConnection(int connection_id) {
        ListenerToConnectionAdapter connection = (ListenerToConnectionAdapter) connections.get(new Integer(connection_id));
        if (connection ==  null) {
            throw new IllegalStateException("Connection not found : " + connection_id);
        }
        return connection;
    }

    /**
     * Gets and removes the ListenerToConnectionAdapter identified with the specified id
     *
     * @param connection_id id of the connection
     *
     * @return The connection
     * @throws IllegalStateException if the id is not found
     */
    private ListenerToConnectionAdapter removeConnection(int connection_id) {
        ListenerToConnectionAdapter connection = (ListenerToConnectionAdapter) connections.remove(new Integer(connection_id));
        return connection;
    }
}
