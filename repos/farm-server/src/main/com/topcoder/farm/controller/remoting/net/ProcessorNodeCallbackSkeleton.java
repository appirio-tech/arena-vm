/*
 * ProcessorNodeCallbackSkeleton
 * 
 * Created 08/04/2006
 */
package com.topcoder.farm.controller.remoting.net;

import com.topcoder.farm.controller.remoting.net.AbstractControllerProxy.SatelliteNodeCallbackSkeleton;
import com.topcoder.farm.satellite.SatelliteNodeCallback;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorNodeCallbackSkeleton extends SatelliteNodeCallbackSkeleton 
                implements SatelliteNodeCallback {

    /**
     * @param abstractControllerProxy
     * @param realCallback
     */
    public ProcessorNodeCallbackSkeleton(AbstractControllerProxy abstractControllerProxy, SatelliteNodeCallback realCallback) {
        abstractControllerProxy.super(realCallback);
        
    }

}
