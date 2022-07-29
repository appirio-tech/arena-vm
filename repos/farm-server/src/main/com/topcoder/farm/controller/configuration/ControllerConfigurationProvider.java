/*
 * ControllerConfigurationProvider
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.configuration;



/**
 * ControllerConfigurationProvider is responsible for obtaining the information
 * and building the configuration for the controller
 * 
 * Users of this class must use the static method getConfiguration to obtain the 
 * configuration for the controller
 * 
 * Subclass of this class must implement the buildConfiguration that returns the 
 * ControllerConfiguration. This instance is kept and returned every time the 
 * getConfiguration method is invoked.
 *
 * The class used for obtaining the ControllerConfiguration is the one defined
 * in configurationProvider.class System property.
 *  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class ControllerConfigurationProvider {
    private static final String CONFIGURATION_PROVIDER_CLASS_KEY = "configurationProvider.class";
    private static ControllerConfiguration instance = null;

    /**
     * @return the controller configuration
     * @throws IllegalStateException If the configuration could not be obtained
     */
    public static synchronized ControllerConfiguration getConfiguration() {
        if (instance == null) {
            instance = getImpl().buildConfiguration();
        }
        return instance;
    }
    
    /**
     * Builds the ControllerConfiguration, this method is invoked only one time
     * 
     * @return The controller configuration
     */
    protected abstract ControllerConfiguration buildConfiguration();
    
    /**
     * Gets an instance of the class specified in configurationProvider.class system property
     */
    private static ControllerConfigurationProvider getImpl() {
        String className = System.getProperty(CONFIGURATION_PROVIDER_CLASS_KEY);
        try {
            return (ControllerConfigurationProvider) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException(CONFIGURATION_PROVIDER_CLASS_KEY+" property value is invalid",e);
        }
    }
}
