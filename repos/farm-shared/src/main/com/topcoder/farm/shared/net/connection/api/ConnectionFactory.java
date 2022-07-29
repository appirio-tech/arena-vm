/*
 * ConnectionFactory
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.shared.net.connection.api;

import java.io.IOException;

/**
 * Common interface for all connection factories
 * 
 * The destination of the connection is determined
 * by the factory. 
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ConnectionFactory {
    
    /**
     * Creates a new Connection.
     * 
     * @return The connection created
     * 
     * @throws IOException If the connection cannot be created
     */
    Connection create() throws IOException;
    
    /**
     * Creates a new Connection with the handler specified as the 
     * connection event handler.
     * 
     * @param handler The handler to be used for the new connection
     *  
     * @return The connection created
     * 
     * @throws IOException If the connection cannot be created
     */
    Connection create(ConnectionHandler handler) throws IOException;
}
