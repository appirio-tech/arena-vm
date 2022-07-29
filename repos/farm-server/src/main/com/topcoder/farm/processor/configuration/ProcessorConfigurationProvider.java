/*
 * ProcessorConfigurationProvider
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.processor.configuration;


/**
 * ProcessorConfigurationProvider is responsible for obtaining the information
 * and building the configuration for the controller
 * 
 * Users of this class must use the static method getConfiguration to obtain the 
 * configuration for the controller
 * 
 * Subclass of this class must implement the buildConfiguration that returns the 
 * ProcessorConfiguration. This instance is kept and returned every time the 
 * getConfiguration method is invoked.
 *
 * The class used for obtaining the ProcessorConfiguration is the one defined
 * in configurationProvider.class System property.
 *  
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public abstract class ProcessorConfigurationProvider {
    private static ProcessorConfiguration instance = null;

    /**
     * @return the controller configuration
     * @throws IllegalStateException If the configuration could not be obtained
     */
    public static synchronized ProcessorConfiguration getConfiguration() {
        if (instance == null) {
            instance = getImpl().buildConfiguration();
        }
        return instance;
    }
    
    /**
     * Builds the ProcessorConfiguration, this method is invoked only one time
     * 
     * @return The controller configuration
     */
    protected abstract ProcessorConfiguration buildConfiguration();
    
    /**
     * Gets an instance of the class specified in configurationProvider.class system property
     */
    private static ProcessorConfigurationProvider getImpl() {
        String className = System.getProperty("configurationProvider.class");
        try {
            return (ProcessorConfigurationProvider) Class.forName(className).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("configurationProvider.class is invalid",e);
        }
    }
}
