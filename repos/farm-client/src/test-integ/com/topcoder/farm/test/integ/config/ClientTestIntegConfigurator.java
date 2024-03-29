/*
 * ClientTestIntegConfigurator
 * 
 * Created 09/22/2006
 */
package com.topcoder.farm.test.integ.config;

import java.net.UnknownHostException;

import com.topcoder.farm.client.invoker.FarmFactory;
import com.topcoder.farm.client.invoker.InvalidConfigurationException;
import com.topcoder.farm.client.util.FarmFactoryProvider;
import com.topcoder.farm.test.integ.IntegConstants;

/**
 * Configures the FarmFactory to be used for Integration
 * Test.
 * 
 * 
 * Configuration:
 *  Controller must be running on the same host in which the tests will be run. 
 *      Controller must listen for client connections on {@link IntegConstants#CONTROLLER_CLIENT_PORT}
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ClientTestIntegConfigurator {

    /**
     * Configures the FarmFactorty
     * 
     * @throws InvalidConfigurationException
     * @throws UnknownHostException
     */
    public static void configure() throws InvalidConfigurationException, UnknownHostException {
        if (!FarmFactory.isConfigured()) {
            FarmFactoryProvider.getConfiguredFarmFactory();
        }
    }

}
