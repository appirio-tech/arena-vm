/*
 * LongSystemTests
 * 
 * Created 05/09/2006
 */
package com.topcoder.utilities.lcontest;

import java.util.HashMap;
import java.util.Map;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;

/**
 * Runner class to execute long system tests tasks
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongSystemTests.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class LongSystemTests {
    /**
     * Constant used for key without value, like flags
     */
    private static final String NO_VALUE = "NO-VALUE";
    

    public static void main(String[] args)  {
        if (args.length == 0) {
            usage();
        }
        String action = args[0];
        Map params = parseParams(args);
        try {
            if ("start".equals(action)) {
                startSystemTests(params);
                return;
            }
            System.out.println("action '"+action+"' unknown.");
            usage();
        } catch (Exception e) {
            System.out.println("Exception when trying to execute action="+action);
            System.out.println("using args: " + params);
            e.printStackTrace();
        }
    }


    /**
     * Starts the system tests for a round using the TestServices
     * @arg params containing the roundid string value
     */
    private static void startSystemTests(Map params) throws Exception {
        int roundId = getArgAsInt(params, "roundid");
        LongContestServices service = LongContestServicesLocator.getService();
        service.startLongSystemTests(roundId);
    }

    
    /**
     * Prints usage to stdout and exits (-1)
     */
    private static void usage() {
        System.out.println("Usage:");
        System.out.println("com.topcoder.utilities.lcontest.LongSystemTests <action> -<argname> [<value>]");
        System.out.println(" actions: start - Start system tests for the specified round. Args: roundid");
        System.exit(-1);
    }
    

    /**
     * Parses arguments starting at position 1
     */
    private static Map parseParams(String[] args) {
        String lastKey = null;
        Map params = new HashMap();
        for (int i = 1; i < args.length; i++) {
            String value = args[i];
            if (value.charAt(0) != '-') {
                if (lastKey == null) {
                    usage();
                }
                params.put(lastKey, value);  
            } else {
                if (!params.containsKey(lastKey) && lastKey != null) {
                    params.put(lastKey, NO_VALUE);
                }
                lastKey = value.substring(1);
            }
        }
        if (!params.containsKey(lastKey) && lastKey != null) {
            params.put(lastKey, NO_VALUE);
        }
        return params;
    }

    /**
     * Helper method to get argument as int.
     * If is missing or if its value is not an int, prints usage and exits
     */
    private static int getArgAsInt(Map params, String key) {
        String value = getArg(params, key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            usage();
            return 0;
        }
    }
    
    /**
     * Helper method to get argument .
     * If is missing prints usage and exits
     */
    private static String getArg(Map params, String key) {
        String value = (String) params.get(key);
        if (value == null) {
            usage();
        }
        return value;
    }
}
