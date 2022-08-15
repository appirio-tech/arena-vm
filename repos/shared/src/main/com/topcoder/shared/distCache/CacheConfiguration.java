package com.topcoder.shared.distCache;

import com.topcoder.shared.util.TCResourceBundle;
import com.topcoder.shared.util.logging.Logger;

import java.rmi.registry.Registry;
import java.util.MissingResourceException;

/**
 * @author orb
 * @version  $Revision$
 */
public class CacheConfiguration {
    private static Logger log = Logger.getLogger(CacheConfiguration.class);
    static final String BUNDLE_NAME = "cache";
    static final String PROP_PRIMARY = "cache.primary";
    static final String PROP_SECONDARY = "cache.secondary";
    static final String PROP_SIZE = "cache.size";
    static final String PROP_SYNC = "cache.synctime";

    static final String PROP_EXPIREDELAY = "cache.expirecheck";
//    static final String PROP_EXPIRETIME  = "cache.expiretime";

    private static TCResourceBundle _bundle = null;

    // --------------------------------------------------

    /**
     *  maximum cache size, -1 means no limit
     * @return
     */
    public static int getSize() {
        return getBundle().getIntProperty(PROP_SIZE, -1);
    }

    /**
     *  time between syncrhonization attempts with peer
     * @return
     */
    public static int getSynchronizationDelay() {
        return getBundle().getIntProperty(PROP_SYNC, 10000);
    }

    /**
     *
     * @return
     */
    public static int getExpirationCheckDelay() {
        return getBundle().getIntProperty(PROP_EXPIREDELAY, 60000);
    }

    /**
     *
     * @return
     */
    public static String[] getURLS() {
        //log.debug("primary: " + getPrimaryClientURL() + " secondary: " + getSecondaryClientURL());
        if (hasSecondary()) {
            return new String[]{
                getPrimaryClientURL(),
                getSecondaryClientURL()
            };
        } else {
            return new String[]{
                getPrimaryClientURL()
            };
        }
    }

     /**
     *
     * @return
     */
    public static String getPrimaryClientURL() {
        return "rmi://" + getBundle().getProperty(PROP_PRIMARY, "") + "/client/primary";
    }

    /**
     *
     * @return
     */
    public static String getSecondaryClientURL() {
        return "rmi://" + getBundle().getProperty(PROP_SECONDARY, "") + "/client/secondary";
    }

    /**
     *
     * @return
     */
    public static String getPrimaryServerURL() {
        return "rmi://" + getBundle().getProperty(PROP_PRIMARY, "") + "/server/primary";
    }

    /**
     *
     * @return
     */
    public static String getSecondaryServerURL() {
        return "rmi://" + getBundle().getProperty(PROP_SECONDARY, "") + "/server/secondary";
    }

    /**
     *
     * @return
     */
    public static String getPrimaryServerHost() {
        return extractHost(getBundle().getProperty(PROP_PRIMARY, ""));
    }

    /**
     *
     * @return
     */
    public static String getSecondaryServerHost() {
        return extractHost(getBundle().getProperty(PROP_SECONDARY, ""));
    }

    /**
     *
     * @return
     */
    public static int getPrimaryServerPort() {
        return extractPort(getBundle().getProperty(PROP_PRIMARY, ""));
    }

    /**
     *
     * @return
     */
    public static int getSecondaryServerPort() {
        return extractPort(getBundle().getProperty(PROP_SECONDARY, ""));
    }

    public static boolean hasSecondary() {
        boolean ret = true;
        try {
            getBundle().getProperty(PROP_SECONDARY);
        } catch (MissingResourceException e) {
            ret = false;
        }
        return ret;
    }


    // --------------------------------------------------
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


    private static TCResourceBundle getBundle() {
        if (_bundle == null) {
            _bundle = new TCResourceBundle(BUNDLE_NAME);
        }
        return _bundle;
    }


}
