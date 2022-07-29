/*
 * RoundFactory
 * 
 * Created 05/29/2007
 */
package com.topcoder.server.common;

import com.topcoder.netCommon.contest.ContestConstants;

/**
 * @author Diego Belfer (mural)
 * @version $Id: RoundFactory.java 82873 2013-02-27 07:38:34Z FireIce $
 */
public class RoundFactory {
    public static Round newRound(int contestId, int roundId, int roundType, String contestName, String roundName) {
        Round contestRound;
        switch (roundType) {
            case ContestConstants.WEAKEST_LINK_ROUND_TYPE_ID:
                contestRound = new WeakestLinkRound(contestId, roundId, contestName, roundName);
                break;
            case ContestConstants.FORWARDER_ROUND_TYPE_ID:
                contestRound = new ForwarderContestRound(contestId, roundId, contestName, roundName);
                break;
            case ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID:
                contestRound = new ForwarderLongContestRound(contestId, roundId, contestName, roundName);
                break;
            case ContestConstants.INTEL_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:            
            case ContestConstants.INTEL_LONG_PROBLEM_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:            
            case ContestConstants.LONG_PROBLEM_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_QA_ROUND_TYPE_ID:
            case ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID:
            case ContestConstants.AMD_LONG_PROBLEM_ROUND_TYPE_ID:
            case ContestConstants.AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID:
                contestRound = new LongContestRound(contestId, roundId, roundType, contestName, roundName);
                break;            
            default:
                contestRound = new ContestRound(contestId, roundId, roundType, contestName, roundName);
                break;
        }
        return contestRound;
    }
}
