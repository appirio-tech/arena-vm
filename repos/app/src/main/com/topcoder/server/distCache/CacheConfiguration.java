package com.topcoder.server.distCache;

import java.util.Properties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.rmi.registry.Registry;

public class CacheConfiguration {

    static final String RESOURCES_DIR = "resources";
    static final String RESOURCES_FILES = "cache.properties";

    static final String PROP_PRIMARY = "cache.primary";
    static final String PROP_SECONDARY = "cache.secondary";
    static final String PROP_SIZE = "cache.size";
    static final String PROP_SYNC = "cache.synctime";

    static final String PROP_EXPIREDELAY = "cache.expirecheck";
    static final String PROP_EXPIRETIME = "cache.expiretime";

    static Properties _properties = null;

    // --------------------------------------------------

    /**
     *  maximum cache size, -1 means no limit
     */

    public static int getSize() {
        int size = -1;
        String sizestr = getProperties().getProperty(PROP_SIZE);

        if ((sizestr != null) && (sizestr.length() > 0)) {
            try {
                size = Integer.parseInt(sizestr);
            } catch (NumberFormatException e) {
                System.out.println("cannot parse cache size: " + sizestr);
            }
        }

        return size;
    }

    /**
     *  time between syncrhonization attempts with peer
     */

    public static int getSynchronizationDelay() {
        int delay = 10000; // 10 seconds
        String delaystr = getProperties().getProperty(PROP_SYNC);

        if ((delaystr != null) && (delaystr.length() > 0)) {
            try {
                delay = Integer.parseInt(delaystr);
            } catch (NumberFormatException e) {
                System.out.println("cannot parse cache sync time: " + delaystr);
            }
        }

        return delay;
    }

    public static int getExpirationCheckDelay() {
        int delay = 60000; // 60 seconds
        String delaystr = getProperties().getProperty(PROP_EXPIREDELAY);

        if ((delaystr != null) && (delaystr.length() > 0)) {
            try {
                delay = Integer.parseInt(delaystr);
            } catch (NumberFormatException e) {
                System.out.println("cannot parse " + PROP_EXPIREDELAY + " value " + delaystr);
            }
        }

        return delay;
    }


    public static long getExpirationTime() {
        long delay = 24 * 60 * 60000; // 1 day
        String strval = getProperties().getProperty(PROP_EXPIRETIME);

        if ((strval != null) && (strval.length() > 0)) {
            try {
                delay = Long.parseLong(strval);
            } catch (NumberFormatException e) {
                System.out.println("cannot parse " + PROP_EXPIRETIME + " value " + strval);
            }
        }

        return delay;
    }


    public static String[] getURLS() {
        return new String[]{
            getPrimaryClientURL(),
            getSecondaryClientURL()
        };
    }


    public static String getPrimaryClientURL() {
        return "rmi://" + getProperties().getProperty(PROP_PRIMARY) + "/client/primary";
    }

    public static String getSecondaryClientURL() {
        return "rmi://" + getProperties().getProperty(PROP_SECONDARY) + "/client/secondary";
    }


    public static String getPrimaryServerURL() {
        return "rmi://" + getProperties().getProperty(PROP_PRIMARY) + "/server/primary";
    }

    public static String getSecondaryServerURL() {
        return "rmi://" + getProperties().getProperty(PROP_SECONDARY) + "/server/secondary";
    }


    public static String getPrimaryServerHost() {
        return extractHost(getProperties().getProperty(PROP_PRIMARY));
    }

    public static String getSecondaryServerHost() {
        return extractHost(getProperties().getProperty(PROP_SECONDARY));
    }

    public static int getPrimaryServerPort() {
        return extractPort(getProperties().getProperty(PROP_PRIMARY));
    }

    public static int getSecondaryServerPort() {
        return extractPort(getProperties().getProperty(PROP_SECONDARY));
    }


    // --------------------------------------------------

    static Properties getProperties() {
        if (_properties != null) {
            return _properties;
        }

        File propfile = new File(new File(RESOURCES_DIR), RESOURCES_FILES);
        if (!propfile.exists()) {
            System.err.println(propfile.getPath());
            throw new RuntimeException("Can't find cache properties file " + propfile.getName());
        }

        Properties p = new Properties();

        try {
            p.load(new FileInputStream(propfile));
        } catch (IOException e) {
            throw new RuntimeException("Error reading propreites file: " + e.getMessage());
        }

        _properties = p;

        return _properties;
    }

    private static int extractPort(String hostname) {
        if (hostname == null) {
            return Registry.REGISTRY_PORT;
        }

        int pos = hostname.indexOf(':');

        if (pos == -1) {
            return Registry.REGISTRY_PORT;
        }

        return Integer.parseInt(hostname.substring(pos + 1));
    }


    private static String extractHost(String hostname) {
        if (hostname == null) {
            return "";
        }

        int pos = hostname.indexOf(':');

        if (pos == -1) {
            return "";
        }

        return hostname.substring(0, pos);
    }


}
