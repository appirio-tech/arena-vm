package com.topcoder.server.services;

import java.util.HashMap;
import java.util.Map;

public final class PracticeSystemTestAssembler {

    private static final Map requestMap = new HashMap();

    private PracticeSystemTestAssembler() {
    }

    private static Object getKey(int coderID, long submitTime) {
        return new PracticeSystemTestItem(coderID, submitTime);
    }

    synchronized static void put(int coderID, long submitTime) {
        requestMap.put(getKey(coderID, submitTime), "");
    }

    public synchronized static String getMessage(int coderID, long submitTime, String message) {
        Object key = getKey(coderID, submitTime);
        String value = (String) requestMap.remove(key);
        if (value == null) {
            // only one language server is required
            return message;
        }
        if (value.equals("")) {
            // received the first result of two
            requestMap.put(key, message);
            return null;
        }
        return value + message;
    }

}
