/*
 * ClientControllerLocator
 * 
 * Created 07/06/2006
 */
package com.topcoder.farm.client;

import com.topcoder.farm.controller.api.ClientControllerNode;
import com.topcoder.farm.shared.util.version.LazyProvider;

/**
 * The ClientControllerLocator class provides a centralized way to obtain 
 * ClientControllerNode interface.
 * 
 * The ClientControllerLocator must be configured with a LazyProvider that will
 * in fact provide the ClientControllerNode. 
 * 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientControllerLocator {
    /**
     * The LazyProvider used to obtain the ClientControllerNode.
     */
    private static LazyProvider provider;

    /**
     * Sets the LazyProvider used to obtain the ClientControllerNode.
     * @param provider the provider to set
     */
    public static void setClientControllerProvider(LazyProvider  provider) {
        ClientControllerLocator.provider = provider; 
    }
    
    /**
     * Returns a ClientControllerNode instance.

     * @return a ClientControllerNode  
     */
    public static ClientControllerNode getController() {
        return (ClientControllerNode) ClientControllerLocator.provider.getObject();
    }
}
