/*
 * ControllerConfiguration
 * 
 * Created 07/26/2006
 */
package com.topcoder.farm.controller.configuration;

import com.topcoder.farm.controller.remoting.net.ConnectionControllerConfiguration;

/**
 * Configuration holding all required parameters to run Controller application
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerConfiguration {
	/**
     * Specifies the path to root folder of the controller, in that folder is where
     * controller is deployed. Script to start controller is deployed there during first
     * installation. 
	 */
    private String rootFolder;
    /**
     * Configuration values for client connections
     */
    private ConnectionControllerConfiguration clientConnectionConfiguration;
    /**
     * Configurations values for processor connections
     */
    private ConnectionControllerConfiguration processorConnectionConfiguration;
    /**
     * Classpath related path to the file containing database configuration (hibernate)
     */
    private String databaseConfigurationFile;
    /**
     * Configuration values used for ControllerNode configuration. 
     */
    private ControllerNodeConfiguration nodeConfiguration;

    
    public ConnectionControllerConfiguration getClientConnectionConfiguration() {
        return clientConnectionConfiguration;
    }
    
    public void setClientConnectionConfiguration(ConnectionControllerConfiguration config) {
        this.clientConnectionConfiguration = config;
    }
    
    public ConnectionControllerConfiguration getProcessorConnectionConfiguration() {
        return processorConnectionConfiguration;
    }
    
    public void setProcessorConnectionConfiguration(ConnectionControllerConfiguration config) {
        this.processorConnectionConfiguration = config;
    }
    
    public String getDatabaseConfigurationFile() {
        return databaseConfigurationFile;
    }
    
    public void setDatabaseConfigurationFile(String databaseConfurationFile) {
        this.databaseConfigurationFile = databaseConfurationFile;
    }
    
    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }

    public ControllerNodeConfiguration getNodeConfiguration() {
        return nodeConfiguration;
    }

    public void setNodeConfiguration(ControllerNodeConfiguration nodeConfiguration) {
        this.nodeConfiguration = nodeConfiguration;
    }
}
