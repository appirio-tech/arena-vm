/*
 * LongContestRound
 * 
 * Created 05/29/2007
 */
package com.topcoder.server.common;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.server.services.CoreServices;

/**
 * @author Diego Belfer (mural)
 * @version $Id: LongContestRound.java 67962 2008-01-15 15:57:53Z mural $
 */
public class LongContestRound extends BaseRound {
    private Integer mainRoomId;
    
    public LongContestRound(int contestId, int roundId, int roundType, String contestName, String roundName) {
        super(contestId, roundId, roundType, contestName, roundName);
        if (!ContestConstants.isLongRoundType(new Integer(roundType))) {
            throw new IllegalArgumentException("Invalid round type specified");
        }
    }

    protected ComponentChallengeData createComponentChallengeData(int problemID) {
        return null;
    }

    public boolean isLongContestRound() {
        return true;
    }

    public boolean isLongRound() {
        return false;
    }

    public boolean isModeratedChat() {
        return false;
    }

    public boolean isTeamRound() {
        return false;
    }
    
    public Integer getMainRoomId() {
        if (mainRoomId == null) {
            mainRoomId = getNonAdminRoom();
        }
        return mainRoomId;
    }

    public boolean areRoomsAssigned() {
        return true;
    }

    public boolean isPractice() {
        return getRoundType().isPracticeRound();
    }
    
    public String getDisplayName() {
        if(getRoundTypeId() == ContestConstants.LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID)
            return super.getDisplayName();
        else
            return getRoundName();
    }
    
    public Integer getAssignedRoom(int userId) {
        if (isPractice()) {
            return getMainRoomId();
        }
        User user = CoreServices.getUser(userId);
        if (user.isLevelTwoAdmin()) {
            return getAdminRoom();
        }
        return getMainRoomId();
    }
    
    public boolean inRegistration() {
        long now = System.currentTimeMillis();
        return getPhase() == ContestConstants.REGISTRATION_PHASE || (getRegistrationStart().getTime() < now && now < getRegistrationEnd().getTime());
    }
}
