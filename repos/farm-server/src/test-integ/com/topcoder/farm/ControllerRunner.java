/*
 * ControllerRunner
 *
 * Created 08/17/2006
 */
package com.topcoder.farm;

import com.topcoder.farm.controller.ControllerMain;
import com.topcoder.farm.controller.configuration.ControllerConfiguration;
import com.topcoder.farm.controller.configuration.ControllerConfigurationProvider;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ControllerRunner {

    public static final String HIBERNATE_CFG = "hibernate-integ.cfg.xml";

    public static void main(String[] args) {
        System.setProperty("configurationProvider.class", "com.topcoder.farm.controller.configuration.XMLConfigurationProvider");
        System.setProperty("configuration.xml.url", "http://localhost:8080/farm-deployer/config?type=controller&id=CONTROLLER-1");
        ControllerConfiguration cfg = ControllerConfigurationProvider.getConfiguration();
        cfg.setDatabaseConfigurationFile(HIBERNATE_CFG);
        
        try {
        new IntegTestDataGenerator().generate();
            new ControllerMain("CONTROLLER-1").run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
