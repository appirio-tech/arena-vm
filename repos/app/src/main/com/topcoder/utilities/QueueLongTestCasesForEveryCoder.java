/*
 * QueueLongTestCasesForEveryCoder
 * 
 * Created Jul 23, 2008
 */
package com.topcoder.utilities;

import com.topcoder.server.ejb.TestServices.LongContestServices;
import com.topcoder.server.ejb.TestServices.LongContestServicesLocator;

/**
 * Allows queueing a set of system test cases for all coder of
 * a given round. If the tests were already run for any coder, previous results
 * are deleted.
 *  
 * @author Diego Belfer (Mural)
 * @version $Id: QueueLongTestCasesForEveryCoder.java 74113 2008-12-29 19:34:43Z dbelfer $
 */
public class QueueLongTestCasesForEveryCoder {
    private static int roundId;
    private static long[] testCaseIds;

    /** Creates a new instance of RecalcPlaced */
    public QueueLongTestCasesForEveryCoder() {
    }
    
    public static void main(String[] args) {
        
            System.out.println("args: roundId testCasesIdsCommaSep");
            if (args.length != 2) {
                return;
            }
            parse(args);
            go();
    }
    
    private static void parse(String[] args) {
        roundId = Integer.parseInt(args[0]);
        String[] testCases = args[1].split(",");
        testCaseIds = parseAsLongs(testCases);
    }

    private static long[] parseAsLongs(String[] values) {
        long[] result = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            result[i] = Long.parseLong(values[i]);
            
        }
        return result;
    }
    
    public static void go() {
        try {
            LongContestServices ts = LongContestServicesLocator.getService();
            
            ts.queueLongSystemTestCase(roundId, testCaseIds);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
}
