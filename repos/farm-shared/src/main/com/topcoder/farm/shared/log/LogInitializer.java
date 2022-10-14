/*
 * LogInitializer
 * 
 * Created 08/15/2006
 */
package com.topcoder.farm.shared.log;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * LogInitializer class. This class is responsible 
 * for log initialization in processor and controllers
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LogInitializer {

    /**
     * Initialize the log
     */
    public static void initialize() {
        new DOMConfigurator().doConfigure(LogInitializer.class.getResourceAsStream("log4j.xml"),  
                LogManager.getLoggerRepository());
    }
    
    /**
     * Initialize the log
     */
    public static void reconfigure() {
        LogManager.resetConfiguration();
        System.out.println("Reconfiguring log4j");
        InputStream resource = LogInitializer.class.getResourceAsStream("/conf/log4j.xml");
        if (resource == null) {
            System.out.println("FileNotFound!!!!!!!!!!!!!!!!!");
            return;
        }
        try {
            new DOMConfigurator().doConfigure(resource, LogManager.getLoggerRepository());
        } finally {
            try {
                resource.close();
            } catch (IOException e) {
                //nothing to do
            }
        }
    }
}
