package com.topcoder.server.broadcaster;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class BroadcasterProps {

    private static final String MIT_LOCAL_PORT = "MITLocalPort";
    private static final String EXODUS_LOCAL_PORT = "ExodusLocalPort";

    private static final int DEFAULT_MIT_LOCAL_PORT = 4990;
    private static final int DEFAULT_EXODUS_LOCAL_PORT = 4991;

    private static final ResourceBundle bundle = getBundle();

    private BroadcasterProps() {
    }

    private static ResourceBundle getBundle() {
        try {
            return ResourceBundle.getBundle("broadcaster");
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getString(String key) {
        try {
            if (bundle != null) {
                return bundle.getString(key);
            }
        } catch (MissingResourceException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getString(key));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }

    static int getMITLocalPort() {
        return getInt(MIT_LOCAL_PORT, DEFAULT_MIT_LOCAL_PORT);
    }

    static int getExodusLocalPort() {
        return getInt(EXODUS_LOCAL_PORT, DEFAULT_EXODUS_LOCAL_PORT);
    }

}
