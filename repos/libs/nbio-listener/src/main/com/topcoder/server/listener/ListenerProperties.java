package com.topcoder.server.listener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * This class is used for accessing parameters loaded from app/resources/listener.properties.
 */
public final class ListenerProperties {
    public static final String USE_HTTP_LISTENER = "httpListener";
    public static final String HTTP_LISTENER_PREFIX = "httpListener.";
    public static final String NUM_ACCEPT_THREADS = "numAcceptThreads";
    public static final String NUM_READ_THREADS = "numReadThreads";
    public static final String NUM_WRITE_THREADS = "numWriteThreads";

    private static final String USE_NBIO = "useNBIO";
    private static final String BANNED_IPS = "bannedIPs";

    private static final Properties defaultProperties = getDefaultProperties();
    private static final ResourceBundle properties = ResourceBundle.getBundle("listener");

    private ListenerProperties() {
    }

    private static Properties getDefaultProperties() {
        Properties props = new Properties();
        props.setProperty(USE_NBIO, "false");
        props.setProperty(NUM_ACCEPT_THREADS, "1");
        props.setProperty(NUM_READ_THREADS, "2");
        props.setProperty(NUM_WRITE_THREADS, "2");
        props.setProperty(BANNED_IPS, "");
        props.setProperty(USE_HTTP_LISTENER, "false");
        return props;
    }

    public static Collection getBannedIPs() {
        return getBannedIPs("");
    }

    public static Collection getBannedIPs(String prefix) {
        String s = getProperty(prefix+BANNED_IPS);
        StringTokenizer tk = new StringTokenizer(s, ", \t");
        Collection r = new ArrayList(tk.countTokens());
        while (tk.hasMoreTokens()) {
            r.add(tk.nextToken());
        }
        return r;
    }

    private static int parseInt(String s) {
        int v;
        try {
            v = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            v = 0;
        }
        return v;
    }

    private static int threadValue(String s) {
        return parseInt(s);
    }

    private static String getProperty(String key) {
        String value = properties.getString(key);
        if (value == null) {
            return defaultProperties.getProperty(key);
        }
        return value;
    }

    public static int numThreads(String name) {
        return threadValue(getProperty(name)) - 1;
    }

    public static int numThreads(String prefix, String name) {
        return threadValue(getProperty(prefix + name)) - 1;
    }

    public static int bindPort(String prefix) {
        return parseInt(getProperty(prefix + "bindPort"));
    }

    public static String bindIp(String prefix) {
        return getProperty(prefix + "bindIp");
    }

    public static boolean useHTTPTunnel() {
        String value = getProperty(USE_HTTP_LISTENER);
        value = value.toLowerCase();
        return value.startsWith("true") || value.startsWith("yes") || value.startsWith("on");
    }
}
