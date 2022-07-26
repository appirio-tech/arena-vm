/*
 * ProcessorRunner
 * 
 * Created 08/17/2006
 */
package com.topcoder.farm;

import com.topcoder.farm.processor.ProcessorMain;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ProcessorRunner {
    public static void main(String[] args) {
        System.setProperty("configurationProvider.class", "com.topcoder.farm.processor.configuration.XMLConfigurationProvider");
        System.setProperty("configuration.xml.url", "http://localhost:8080/farm-deployer/config?type=processor&id="+args[0]); 
        ProcessorMain.main(args);
    }
}
