/*
 * ListenerToConnectionAdapter
 *
 * Created 06/27/2006
 */
package com.topcoder.farm.server.net.connection.listener;

import com.topcoder.farm.shared.net.connection.impl.AbstractConnection;
import com.topcoder.server.listener.ListenerInterface;

/**
 * Connection Adapter for Listener connections
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ListenerToConnectionAdapter extends AbstractConnection {
    /**
     * Id given by the listener for this connection connection
     */
    private int connectionId;

    /**
     * Remote ip address of the connection
     */
    private String remoteIP;

    /**
     * Reference to the listener
     */
    private ListenerInterface listenerInterface;


    /**
     * Creates a new ListenerToConnectionAdapter with the specified arguments
     *
     * @param connectionId Id of the connection
     * @param remoteIP Remote ip of the connection
     * @param listenerInterface Listener reference
     */
    public ListenerToConnectionAdapter(int connectionId, String remoteIP, ListenerInterface listenerInterface) {
        this.connectionId = connectionId;
        this.listenerInterface = listenerInterface;
        this.remoteIP = remoteIP;
    }

    /**
     * @see AbstractConnection#bareSend(Object)
     */
    protected void bareSend(Object message) {
        listenerInterface.send(connectionId, message);
    }


    /**
     * @see AbstractConnection#bareClose
     */
    protected void bareClose() {
        //TODO Verify behaviour because all messages sent to the connection are not written as they should
        //before the close is executed
        listenerInterface.shutdown(connectionId, true);
    }

    /**
     * @return the id of the connection
     */
    public int getConnectionId() {
        return connectionId;
    }

    /**
     * @return The remote ip of this connection
     */
    public String getRemoteIP() {
        return remoteIP;
    }

    public String toString() {
        return "ListenerToConnectionAdapter[id=" + connectionId + ",ip=" + remoteIP + "]";
    }
}
