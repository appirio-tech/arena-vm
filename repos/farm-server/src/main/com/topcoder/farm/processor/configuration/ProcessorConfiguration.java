/*
 * ProcessorConfiguration
 * 
 * Created 06/29/2006
 */
package com.topcoder.farm.processor.configuration;

import com.topcoder.farm.satellite.SatelliteConfiguration;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
@Deprecated
public class ProcessorConfiguration extends SatelliteConfiguration {
    private String rootFolder;
    private String workFolder;

    /**
     * Returns the path to the folder where resources are deployed
     * @return An slash terminated path
     */
    public String getResourceFolder() {
        return rootFolder+"resource/";
    }

    /**
     * Returns the path to the folder where processor has permissions to work
     * @return An slash terminated path
     */
    public String getWorkFolder() {
        return workFolder;
    }

    public void setWorkFolder(String workFolder) {
        this.workFolder = workFolder;
    }

    /**
     * Returns the path to root folder of the processor, in that folder is where
     * processor is deployed. Script to start processor is deployed there during first
     * installation
     * 
     * @return An slash terminated path
     */
    public String getRootFolder() {
        return rootFolder;
    }

    public void setRootFolder(String rootFolder) {
        this.rootFolder = rootFolder;
    }
}
