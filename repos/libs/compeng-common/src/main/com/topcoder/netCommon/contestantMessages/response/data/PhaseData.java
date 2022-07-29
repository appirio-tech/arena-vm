package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contestantMessages.response.PhaseDataResponse;
import com.topcoder.netCommon.contestantMessages.response.RoundScheduleResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines the time line of a phase of a round.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: PhaseData.java 72424 2008-08-20 08:06:01Z qliu $
 * @see PhaseDataResponse
 * @see RoundScheduleResponse
 */
public final class PhaseData implements Serializable, CustomSerializable {
    /** Represents the ID of the round. */
    private int roundID;

    /** Represents the type of the phase. */
    private int phaseType;

    /** Represents the start time of the phase in milliseconds since 1/1/1970 00:00:00 GMT */
    private long startTime;

    /** Represents the end time of the phase in milliseconds since 1/1/1970 00:00:00 GMT */
    private long endTime;

    /**
     * Creates a new instance of <code>PhaseData</code>. It is required by custom serialization.
     */
    public PhaseData() {
    }

    /**
     * Creates a new instance of <code>PhaseData</code>. The time is represented in milliseconds since 01/01/1970
     * 00:00:00 GMT.
     * 
     * @param roundID the ID of the round.
     * @param phaseType the type of the phase.
     * @param startTime the start time of the phase.
     * @param endTime the end time of the phase.
     * @see #getPhaseType()
     * @see java.util.Date#getTime()
     */
    public PhaseData(int roundID, int phaseType, long startTime, long endTime) {
        this.roundID = roundID;
        this.phaseType = phaseType;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(getRoundID());
        csWriter.writeInt(getPhaseType());
        csWriter.writeLong(getStartTime());
        csWriter.writeLong(getEndTime());
    }

    public void customReadObject(CSReader csReader) throws IOException {
        roundID = csReader.readInt();
        phaseType = csReader.readInt();
        startTime = csReader.readLong();
        endTime = csReader.readLong();
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public int getRoundID() {
        return roundID;
    }

    /**
     * Gets the type of the phase.
     * 
     * @return the type of the phase.
     * @see ContestConstants#INACTIVE_PHASE
     * @see ContestConstants#STARTS_IN_PHASE
     * @see ContestConstants#REGISTRATION_PHASE
     * @see ContestConstants#ALMOST_CONTEST_PHASE
     * @see ContestConstants#CODING_PHASE
     * @see ContestConstants#INTERMISSION_PHASE
     * @see ContestConstants#CHALLENGE_PHASE
     * @see ContestConstants#PENDING_SYSTESTS_PHASE
     * @see ContestConstants#SYSTEM_TESTING_PHASE
     * @see ContestConstants#CONTEST_COMPLETE_PHASE
     * @see ContestConstants#VOTING_PHASE
     * @see ContestConstants#TIE_BREAKING_VOTING_PHASE
     * @see ContestConstants#MODERATED_CHATTING_PHASE
     */
    public int getPhaseType() {
        return phaseType;
    }

    /**
     * Gets the start time of the phase. The time is represented in milliseconds since 01/01/1970 00:00:00 GMT.
     * 
     * @return the start time.
     * @see java.util.Date#getTime()
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Gets the end time of the phase. The time is represented in milliseconds since 01/01/1970 00:00:00 GMT.
     * 
     * @return the end time.
     * @see java.util.Date#getTime()
     */
    public long getEndTime() {
        return endTime;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.PhaseData) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("phaseType = ");
        ret.append(phaseType);
        ret.append(", ");
        ret.append("startTime = ");
        ret.append(startTime);
        ret.append(", ");
        ret.append("endTime = ");
        ret.append(endTime);
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

}
