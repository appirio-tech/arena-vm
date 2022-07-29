/*
 * RecalcPlaced.java
 *
 * Created on February 17, 2006, 10:43 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.utilities;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;

/**
 *
 * @author rfairfax
 */
public class QueueLongTestCases {
    
    private static int roundId;
    private static long[] testCaseIds;
    private static int[] coderIds;

    /** Creates a new instance of RecalcPlaced */
    public QueueLongTestCases() {
    }
    
    public static void main(String[] args) {
        
            System.out.println("args: roundId  codersIdsCommaSep testCasesIdsCommaSep");
            if (args.length != 3) {
                return;
            }
            parse(args);
            go();
    }
    
    private static void parse(String[] args) {
        roundId = Integer.parseInt(args[0]);
        String[] coders = args[1].split(",");
        String[] testCases = args[2].split(",");
        coderIds = parseAsInt(coders);
        testCaseIds = parseAsLongs(testCases);
    }

    private static long[] parseAsLongs(String[] values) {
        long[] result = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Long.parseLong(values[i]);
            
        }
        return result;
    }
    
    private static int[] parseAsInt(String[] values) {
        int[] result = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Integer.parseInt(values[i]);
            
        }
        return result;
    }

    public static void go() {
        try {
            
            LongContestServices ts = LongContestServicesLocator.getService();
            
            ts.queueLongSystemTestCase(roundId, coderIds, testCaseIds);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
}
