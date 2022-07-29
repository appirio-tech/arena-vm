/*
 * ProcessorNode
 * 
 * Created 07/04/2006
 */
package com.topcoder.farm.processor.api;

import com.topcoder.farm.managed.LocalManagedNode;
import com.topcoder.farm.satellite.SatelliteNode;


/**
 * A ProcessorNode is a Satellite node that connects to the farm
 * and receives processing requirements from a controller. It provides 
 * methods to report results and status to the controller.  
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface ProcessorNode extends SatelliteNode, LocalManagedNode {
    /**
     * Sets the processor as Available. When the processor is set as Available,
     * it is available to receive one invocation from the farm.
     */
    void setAsAvailable();
    
    /**
     * Sets the listener to be notified about processor node events
     *  
     * @param listener The listener to use
     */
    void setListener(Listener listener);
    
    
    
    /**
     * Sets the handler used to process invocations
     * 
     * @param handler The handler to set
     */
    void setInvocationHandler(ProcessorInvocationHandler handler);
    
    /**
     * Listener interface used by a processor node to notify events 
     * to the user object
     */
    public interface Listener {
        
        /**
         * This method is invoked when the processor has lost the connection with the farm.
         * The connection might be lost for a lot of reason
         * 
         * @param cause A string containing a descriptive message of the cause 
         */
        void nodeDisconnected(String cause);
    }
}
