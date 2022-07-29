/*
 * Invoker
 * 
 * Created 07/25/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

import com.topcoder.farm.shared.net.connection.api.Connection;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Invoker {
    
    public Object invokeSync(Connection connection, Object request)
            throws TimeoutException, RemotingException, InterruptedException,
            Exception;
    
    public void invokeAsync(Connection connection, Object request)
            throws RemotingException;
    
    public void invoke(Connection connection, Object request)
            throws TimeoutException, RemotingException, InterruptedException;
}
