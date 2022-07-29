/*
 * NullReconnectableConnectionHandler
 * 
 * Created 07/17/2006
 */
package com.topcoder.farm.shared.net.connection.impl.reconnectable;

import com.topcoder.farm.shared.net.connection.api.ReconnectableConnection;
import com.topcoder.farm.shared.net.connection.api.ReconnectableConnectionHandler;
import com.topcoder.farm.shared.net.connection.impl.NullConnectionHandler;

/**
 * Null Object for ReconnectableConnectionHandler
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NullReconnectableConnectionHandler extends NullConnectionHandler implements ReconnectableConnectionHandler {
    /**
     * The unique instance of this class
     */
    public final static ReconnectableConnectionHandler INSTANCE = new NullReconnectableConnectionHandler();

    protected NullReconnectableConnectionHandler() {
    }

    public void performReconnection(ReconnectableConnection connection) {
    }
    
    public void reconnected(ReconnectableConnection connection) {
    }

    public void reconnecting(ReconnectableConnection connection) {
    }
}
