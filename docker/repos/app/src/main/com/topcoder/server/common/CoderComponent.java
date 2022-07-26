/**
 * Class CoderComponent
 *
 * Author: Hao Kung
 *
 * Description: This class will contain info for a coder's work on one specific problem
 */
package com.topcoder.server.common;

import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.util.logging.Logger;

public class CoderComponent extends BaseCoderComponent implements Serializable {
    private static final Logger log = Logger.getLogger(CoderComponent.class);
    private long succesfullyChallengedTime = 0;
    private Object[] challengeArgs = new Object[0];
    private String challenger;
    private Integer passedSystemTests;

    public CoderComponent(int coderID, int componentID, int pointValue) {
        super(coderID, componentID, pointValue);
    }

    public Integer getPassedSystemTests() {
        return passedSystemTests;
    }

    public void setPassedSystemTests(Integer passedSystemTests) {
        this.passedSystemTests = passedSystemTests;
    }
    
    public boolean isWritable() {
        return getStatus() == ContestConstants.LOOKED_AT || getStatus() == ContestConstants.COMPILED_UNSUBMITTED;
    }

    public boolean isChallenged() {
        return getStatus() == ContestConstants.CHALLENGE_SUCCEEDED;
    }

    public long getSuccesfullyChallengedTime() {
        return succesfullyChallengedTime;
    }

    public void setSuccesfullyChallengedTime(long succesfullyChallengedTime) {
        this.succesfullyChallengedTime = succesfullyChallengedTime;
    }
    
    public Object[] getChallengeArgs() {
        return challengeArgs;
    }
    
    public void setChallengeArgs(Object[] a) {
        challengeArgs = a;
    }

    public String getChallenger() {
        return challenger;
    }

    public void setChallenger(String challenger) {
        this.challenger = challenger;
    }

    public boolean sysTestCheck() {
        switch (getStatus()) {
        case ContestConstants.NOT_OPENED:
        case ContestConstants.LOOKED_AT:
//      case ContestConstants.PASSED:
        case ContestConstants.COMPILED_UNSUBMITTED:
        case ContestConstants.CHALLENGE_SUCCEEDED:
            return false;
        case ContestConstants.NOT_CHALLENGED:   // submitted
        case ContestConstants.CHALLENGE_FAILED:
            return true;
            // try again on these I guess
        case ContestConstants.SYSTEM_TEST_FAILED:
            return true;
        case ContestConstants.SYSTEM_TEST_SUCCEEDED:
            return true;
        }
        return false;
    }
    String getTimeToSubmit() {
        long submittedTime = getSubmittedTime();
        if (submittedTime == 0) {
            return "Not Submitted";
        }
        long openedTime = getOpenedTime();
        long diff = submittedTime - openedTime;
        if (diff >= 2 * 60 * 60 * 1000) {
            log.error("submittedTime=" + submittedTime + ", openedTime=" + openedTime);
        }
        return getTimeString(diff);
    }

    static String getTimeString(long diff) {
        final int N = 60 * 1000;
        long min = diff / N;
        long ms = diff % N;
        String sec = "" + ((ms + 500) / 1000);
        if (sec.length() <= 1) {
            sec = "0" + sec;
        }
        return min + " min " + sec + " sec";
    }

}
