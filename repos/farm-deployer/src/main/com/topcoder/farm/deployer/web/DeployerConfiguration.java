/*
 * DeployerConfiguration
 * 
 * Created 08/31/2006
 */
package com.topcoder.farm.deployer.web;

import java.io.File;

/**
 * Configuration for the deployer. 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DeployerConfiguration {
    private static final DeployerConfiguration instance = new DeployerConfiguration();
    private File deployerRootFolder;
    protected DeployerConfiguration() {
    }

    /**
     * Obtains the unique DeployerConfiguration instance
     * 
     * @return the instance
     */
    public static DeployerConfiguration getInstance() {
        return instance;
    }

    /**
     * Returns the Deployer application folder. All files and folders
     * used by the farm-deployer would be relative to this folder.
     * 
     * @return The root folder for the deployer
     */
    public File getDeployerRootFolder() {
        return deployerRootFolder;
    }

    /**
     * Sets the root DeployerRootFolder
     * 
     * @param deploymentFolder the folder to set as root folder
     */
    void setDeployerRootFolder(File deploymentFolder) {
        this.deployerRootFolder = deploymentFolder;
    }
    
    /**
     * Returns the folder where all deployment files resides. Configurations files for
     * deployment and deliverables files are store in inside this folder
     * 
     * @return The deployment folder
     */
    public File getDeploymentFolder() {
        return new File(getDeployerRootFolder(), "deployment");
    }
    /**
     * Returns the folder where all deliverable jars are store. Jars contained in
     * this folder are the ones deployed in processor and controllers
     * 
     * @return The jars folder
     */
    public File getJarsFolder() {
        return new File(getDeployerRootFolder(), "jars");
    }
    
    /**
     * Returns the temporary folder for the deployer. All files generated and that must be discarded
     * when the deployer ends should be generated in this folder
     * 
     * @return The temp folder
     */
    public File getTempFolder() {
        return new File(getDeployerRootFolder(), "temp");
    }

}
