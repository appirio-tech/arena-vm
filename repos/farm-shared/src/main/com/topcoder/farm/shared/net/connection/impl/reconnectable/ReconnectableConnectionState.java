/*
 * ReconnectableConnectionState
 * 
 * Created 07/11/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.impl.ConnectionState;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ReconnectableConnectionState extends ConnectionState {

    /**
     * Called when the reconnection attemps have failed
     * 
     * @param connection The connection on which the reconnection attemps have failed
     */
    void handleReconnectFail(AutoReconnectConnection connection);

    /**
     * Called when the reconnection attemps have succeeded
     * 
     * @param connection The connection on which the reconnection attemps have succeeded
     */
    void handleReconnectSucceed(AutoReconnectConnection connection);
}
