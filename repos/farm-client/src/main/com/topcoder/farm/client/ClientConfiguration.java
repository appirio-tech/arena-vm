/*
 * ClientFactoryConfiguration
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.client;

import com.topcoder.farm.client.node.ClientNodeBuilderImpl;
import com.topcoder.farm.satellite.SatelliteConfiguration;


/**
 * A ClientConfiguration contains all information that is required to 
 * start client side of the farm.
 * 
 *   
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientConfiguration extends SatelliteConfiguration {
    
    /**
     * Contains the maximun number of threads the Pool will contain for processing notifications
     * The pool is used for all clients created in the same VM 
     */
    private int processorThreadPoolSize = 5;
    
    /**
     * Contains the class name of the builder that must be used to create client nodes
     */
    private String clientNodeBuilderClassName = ClientNodeBuilderImpl.class.getName();
  
    /**
     * Prefix used to for all client names 
     */
    private String invokersPrefix = "";
  
    public String getClientNodeBuilderClassName() {
        return clientNodeBuilderClassName;
    }

    public void setClientNodeBuilderClassName(String clientNodeBuilderClassName) {
        this.clientNodeBuilderClassName = clientNodeBuilderClassName;
    }

    public void setProcessorThreadPoolSize(int processorThreadPoolSize) {
        this.processorThreadPoolSize = processorThreadPoolSize;
    }

    public int getProcessorThreadPoolSize() {
        return processorThreadPoolSize;
    }

    public String getInvokersPrefix() {
        return invokersPrefix;
    }

    public void setInvokersPrefix(String invokersPrefix) {
        this.invokersPrefix = invokersPrefix;
    }
}
