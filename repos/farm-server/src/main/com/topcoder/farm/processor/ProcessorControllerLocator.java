/*
 * ControllerLocator
 * 
 * Created 07/06/2006
 */
package com.topcoder.farm.processor;

import com.topcoder.farm.controller.api.ProcessorControllerNode;
import com.topcoder.farm.shared.util.version.LazyProvider;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorControllerLocator {
    private static LazyProvider provider;

    public static void setProcessorControllerProvider(LazyProvider  provider) {
        ProcessorControllerLocator.provider = provider; 
    }
    
    public static ProcessorControllerNode getController() {
        return (ProcessorControllerNode) ProcessorControllerLocator.provider.getObject();
    }
}
