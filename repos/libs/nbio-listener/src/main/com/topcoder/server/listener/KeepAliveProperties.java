/*
 * KeepAliveProperties
 * 
 * Created 03/17/2006
 */
package com.topcoder.server.listener;

import java.util.ResourceBundle;

import com.topcoder.shared.util.logging.Logger;

/**
 * This class is used for accessing properties loaded 
 * from app/resources/keepalive.properties
 * 
 * @author Diego Belfer (Mural)
 * @version $Id$
 */
public class KeepAliveProperties {
    private static final int SCAN_INTERVAL_DEFAULT = 60000;

    private static final Logger log = Logger.getLogger(ConnectionStatusMonitor.class);
    
    public static final int TIMEOUT_DEFAULT = 30000;
    public static final int HTTP_TIMEOUT_DEFAULT = 30000;
    
    public static final String TIMEOUT = "keepalive.timeout";
    public static final String HTTP_TIMEOUT = "keepalive.http.timeout";
    public static final String SCAN_INTERVAL = "keepalive.scaninterval";

    private static final ResourceBundle properties = ResourceBundle.getBundle("keepalive");
    

    private KeepAliveProperties() {
    }

    /**
     * Returns the keep-alive timeout value in milliseconds
     * Default: 30000
     * 
     * @return the keep-alive timeout value in milliseconds
     */
    public static long getTimeout() {
        return longValue(TIMEOUT, TIMEOUT_DEFAULT);
    }

    /**
     * Returns the http keep-alive timeout value in milliseconds.
     * Default: 30000
     * 
     * @return the http keep-alive timeout value in milliseconds
     */
    public static long getHttpTimeout() {
        return longValue(HTTP_TIMEOUT, HTTP_TIMEOUT_DEFAULT); 
    }
    
    /**
     * Returns the scan interval value in milliseconds
     * Default: 60000
     * 
     * @return the keep-alive timeout value in milliseconds
     */
    public static long getScanInterval() {
        return longValue(SCAN_INTERVAL, SCAN_INTERVAL_DEFAULT);
    }
    
    private static long parseLong(String s, long defaultValue) {
        long v;
        try {
            v = Long.parseLong(s);
        } catch (Exception e) {
            log.error("Invalid property value in KeepAliveProperties", e);
            v = defaultValue;
        }
        return v;
    }

    private static long longValue(String key, long defaultValue) {
        return parseLong(properties.getString(key), defaultValue);
    }
}
