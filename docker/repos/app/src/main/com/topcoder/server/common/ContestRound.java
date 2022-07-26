/**
 * Class contest
 *
 * Author: Hao Kung
 *
 * Description: This class will contain all information about a contest's state
 */
package com.topcoder.server.common;

import java.io.Serializable;
import java.util.HashMap;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundCustomProperties;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentChallengeData;
import com.topcoder.server.services.CoreServices;
import com.topcoder.shared.problem.Problem;
import com.topcoder.shared.problem.ProblemComponent;

/**
 * Modifications for AdminTool 2.0 are :
 * <p>Support for newly defined "room assignment segment" 
 * <p>added : new private variables of type java.sql.Timestamp holding start 
 * and end time of contest round room assignment segment amd their appropriate 
 * accessor methods are added.
 * <p>New private variable of type RoundRoomAssignment holding the details of round 
 * room assignmen algorithm and appropriate get- and set- methods are added.
 * 
 * @author TCDEVELOPER
 */
public class ContestRound extends BaseRound implements Serializable {

    private static final long serialVersionUID = -1081345723420083469L;

    /**
     * Constructor 
     * This method was updated in AdminTool 2.0 to include the creation of
     * a RoundRoomAssignment object used to hold the room assignemnt data.
     * @author TCDEVELOPER
     * @param contestId - the contest this round is for
     * @param roundId - the round
     * @param roundType - round type 
     * @see ContestConstants
     * @param contestName - name of contest
     * @param roundName - name of the round
     */
    ContestRound(int contestId, int roundId, int roundType, String contestName, String roundName) {
        super(contestId, roundId, roundType, contestName, roundName);
    }
    
    ContestRound(int contestId, int roundId, int roundType, String contestName, String roundName, RoundCustomProperties customProperties) {
        super(contestId, roundId, roundType, contestName, roundName, customProperties);
    }

    protected ComponentChallengeData createComponentChallengeData(int problemID) {
        Problem problem = CoreServices.getProblem(problemID);
        ProblemComponent pc = problem.getPrimaryComponent();
        return new ComponentChallengeData(pc.getClassName(), pc.getMethodName(), pc.getParamTypes(),
                pc.getComponentId());
    }
    
    
    public final boolean isModeratedChat() {
        return getRoundTypeId() == ContestConstants.MODERATED_CHAT_ROUND_TYPE_ID;
    }

    public final boolean isTeamRound() {
        return getRoundType().isTeamRound();
    }

    public final boolean isLongRound() {
        return getRoundTypeId() == ContestConstants.LONG_ROUND_TYPE_ID;
    }
    
    public boolean isLongContestRound() {
        return false;
    }
    
    private HashMap m_assignedMap = new HashMap();

    public final HashMap getAssignedRoomMap() {
        return m_assignedMap;
    }

    public final void setAssignedRoomMap(HashMap map) {
        m_assignedMap = map;
    }
    
    public boolean areRoomsAssigned() {
        return m_assignedMap != null;
    }
    
    public Integer getAssignedRoom(int userId) {
        return (Integer) m_assignedMap.get(new Integer(userId));
    }
    
    public String toString() {
        return super.toString()+", assignedMap=" + m_assignedMap;
    }
    
    public long getCodingLength() {
        if (getRoundProperties().usesPerUserCodingTime()) {
            return getRoundProperties().getPerUserCodingTime().longValue();
        } else {
            Long lengthOverride = getRoundProperties().getCodingLengthOverride();
            if (lengthOverride != null) {
                return lengthOverride.longValue();
            } else  {
                return getCodingEnd().getTime() - getCodingStart().getTime();
            }
        }
    }
}
