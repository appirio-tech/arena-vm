/*
* Copyright (C) 2006-2013 TopCoder Inc., All Rights Reserved.
*/

/*
 * SatelliteNodeCallback
 * 
 * Created 07/20/2006
 */
package com.topcoder.farm.satellite;

/**
 * Base interface for callback objects of SatelliteNodes
 * 
 * This callback object is passed during registration to the controller,
 * the controller uses it to notify the registered node about farm events/commands  
 * 
 * <p>
 * Changes in version 1.0 (TC Competition Engine - Processor and Controller Handshake Change v1.0):
 * <ol>
 *      <li>Add {@link #getEndpointIP()} method.</li>
 * </ol>
 * </p>
 * @author Diego Belfer (mural)
 * @version 1.0
 */
public interface SatelliteNodeCallback {
    
    /**
     *  Callback method to notify to the SatelliteNode that
     *  has been unregistered of the controller.
     */
    void unregistered(String cause);
    
    
    /**
     *  Callback method to notify to the SatelliteNode must
     *  disconnect from the controller
     */
    void disconnect(String cause);
    
    /**
     * Gets the endpoint string of the SatelliteNode
     */
    String getEndpointString();
    /**
     * Get the remote processor ip
     * @return the remote processor ip
     */
    String getEndpointIP();
}
