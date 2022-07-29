package com.topcoder.server.listener.monitor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.Arrays;

public final class MonitorProperties {

    private static final String ALLOWED_IPS = "allowedIPs";
    private static final String TABOO_WORDS = "tabooWords";

    private static final ResourceBundle bundle = ResourceBundle.getBundle("monitor_server");

    private MonitorProperties() {
    }

    public static Collection getAllowedIPs() {
        return Arrays.asList(getStrtokString(ALLOWED_IPS));
    }

    static String[] getTabooWords() {
        return getStrtokString(TABOO_WORDS);
    }

    private static String[] getStrtokString(String name) {
        try {
            return strtok(bundle.getString(name));
        } catch (MissingResourceException e) {
            return new String[0];
        }
    }

    private static String[] strtok(String s) {
        List list = new ArrayList();
        StringTokenizer tk = new StringTokenizer(s, "\t,");
        while (tk.hasMoreTokens()) {
            list.add(tk.nextToken().toLowerCase());
        }
        String[] r = new String[list.size()];
        for (int i = 0; i < r.length; i++) {
            r[i] = (String) list.get(i);
        }
        return r;
    }

}
