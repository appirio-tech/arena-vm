/*
 * ForwarderContestRound.java
 *
 * Created on September 28, 2006, 6:01 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.topcoder.server.common;

import java.sql.Timestamp;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.Logger;

/**
 *
 * @author rfairfax
 */
public class ForwarderLongContestRound extends LongContestRound implements ForwarderRound {
    
    public static final Logger log = Logger.getLogger(ForwarderLongContestRound.class);
            
    /** Creates a new instance of ForwarderContestRound */
    ForwarderLongContestRound(int contestId, int roundId, String contestName, String roundName) {
        super(contestId, roundId, ContestConstants.FORWARDER_LONG_ROUND_TYPE_ID, contestName, roundName);
        
        //m_cacheKey = getCacheKey(roundId);
    }
    
    //for now duplicate ids is very bad
    /*
    public static String getCacheKey(int roundID) {
        return "ForwardedContest:" + roundID;
    }*/ 
    
    /* (non-Javadoc)
     * @see com.topcoder.server.common.ForwarderRound#setNextPhase(long)
     */
    public void setNextPhase(long sec) {
        log.debug("Next Phase is: " + sec);
        nextPhase = System.currentTimeMillis() + (sec * 1000);
    }
    
    private long nextPhase = 0;

    public Timestamp getRegistrationStart() {
        return new Timestamp(nextPhase);
    }

    public Timestamp getRegistrationEnd() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getRoomAssignmentStart() {
        return new Timestamp(0);
    }

    public Timestamp getRoomAssignmentEnd() {
        return new Timestamp(0);
    }

    public Timestamp getCodingStart() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getCodingEnd() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getIntermissionStart() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getIntermissionEnd() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getChallengeStart() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getChallengeEnd() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getSystemTestStart() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getSystemTestEnd() {
        return new Timestamp(nextPhase);
    }
    
    public Timestamp getModeratedChatStart() {
        return new Timestamp(0);
    }
    
    public Timestamp getModeratedChatEnd() {
        return new Timestamp(0);
    }
}
