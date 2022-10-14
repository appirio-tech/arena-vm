/*
 * MessageProcessor
 * 
 * Created 07/25/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import com.topcoder.farm.shared.net.connection.api.Connection;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface MessageProcessor {
    public boolean processMessage(Connection connection, Object message);
    public void connectionLost(Connection connection);
}
