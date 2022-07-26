/*
 * RoundUtils
 * 
 * Created 10/26/2006
 */
package com.topcoder.server.farm.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * Helper class to deal with rounds
 *
 * @author Diego Belfer (mural)
 * @version $Id: RoundUtils.java 69043 2008-03-09 20:16:29Z mural $
 */
public class RoundUtils {
    private static final Set intelRounds = 
            new HashSet(Arrays.asList(
                        new Integer[]{
                                new Integer(ContestConstants.INTEL_LONG_PROBLEM_ROUND_TYPE_ID), 
                                new Integer(ContestConstants.INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID)}));
    private static final Set amdRounds = 
        new HashSet(Arrays.asList(
                    new Integer[]{
                            new Integer(ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID), 
                            new Integer(ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID)}));
    
    public static boolean isThreadingAllowed(int roundType) {
        return isThreadingAllowed(new Integer(roundType));
    }
    
    public static boolean isThreadingAllowed(Integer roundType) {
        return amdRounds.contains(roundType) || intelRounds.contains(roundType);
    }

    public static int maxThreadsForRoundType(int roundType) {
        return maxThreadsForRoundType(new Integer(roundType));
    }
    
    public static int maxThreadsForRoundType(Integer roundType) {
        if (roundType != null) {
            if (intelRounds.contains(roundType)) {
                return 32;
            } else if (amdRounds.contains(roundType)) {
                return 32;
            }
        }
        return 1;
    }
}
