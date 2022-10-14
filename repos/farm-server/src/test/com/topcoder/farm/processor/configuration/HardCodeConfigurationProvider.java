/*
 * HardCodeConfigurationProvider
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.processor.configuration;

import java.net.InetSocketAddress;

import com.topcoder.farm.test.integ.IntegConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
class HardCodeConfigurationProvider extends ProcessorConfigurationProvider {
    protected ProcessorConfiguration buildConfiguration() {
        ProcessorConfiguration cfg = new ProcessorConfiguration();
        cfg.setAddresses(new InetSocketAddress[] {new InetSocketAddress("127.0.0.1", IntegConstants.CONTROLLER_PROCESSOR_PORT)});
        return cfg;
    }
}
