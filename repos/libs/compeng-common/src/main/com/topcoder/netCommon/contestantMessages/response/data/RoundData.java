package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.netCommon.contest.round.RoundCustomProperties;
import com.topcoder.netCommon.contestantMessages.response.CreateRoundListResponse;
import com.topcoder.netCommon.contestantMessages.response.UpdateRoundListResponse;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * Defines a round. For practice rounds, there is only one room and one division in the round.
 * 
 * @author Matthew P. Suhocki (msuhocki)
 * @version $Id: RoundData.java 72424 2008-08-20 08:06:01Z qliu $
 * @see CreateRoundListResponse
 * @see UpdateRoundListResponse
 */
public class RoundData implements Serializable, CustomSerializable, Cloneable {
    /** Represents the ID of the round. */
    int roundID;

    /** Represents the name of the contest which the round belongs to. */
    String contestName;

    /** Represents the name of the round. */
    String roundName;

    /** Represents the type of the round. */
    int roundType;

    /** Represents the category of the round if the round is a practice round. */
    int roundCategoryID;

    /** Represents the current phase of the round. */
    PhaseData phaseData;

    /** Represents a flag indicating if the round is enabled. */
    boolean enabled;

    /** Represents the division of the round if the round is a practice round. */
    private int practiceRoundDivision;

    /** Represents the ID of the only room in the round if the round is a practice round. */
    private int practiceRoomID;

    /** Represents the properties of the round. */
    private RoundCustomProperties customProperties;

    /**
     * Creates a new instance of <code>RoundData</code>. It is required by custom serialization.
     */
    public RoundData() {
    }

    /**
     * Creates a new instance of <code>RoundData</code>. This constructor is used for non-practice rounds, since the
     * division of the practice round, the room of the practice round, and the category of the practice round are unset.
     * 
     * @param roundID the ID of the round.
     * @param contestName the name of the contest which the round belongs to.
     * @param roundName the name of the round.
     * @param roundType the type of the round.
     * @param phaseData the current phase of the round.
     * @param enabled <code>true</code> if the round is enabled; <code>false</code> otherwise.
     * @param customProperties the properties of the round.
     * @see #getRoundType()
     */
    public RoundData(int roundID, String contestName, String roundName, int roundType, PhaseData phaseData,
        boolean enabled, RoundCustomProperties customProperties) {
        this.roundID = roundID;
        this.contestName = contestName;
        this.roundName = roundName;
        this.roundType = roundType;
        this.phaseData = phaseData;
        this.enabled = enabled;
        this.customProperties = customProperties;
    }

    /**
     * Creates a new instance of <code>RoundData</code>. It is intended for practice rounds.
     * 
     * @param roundID the ID of the round.
     * @param contestName the name of the contest which the round belongs to.
     * @param roundName the name of the round.
     * @param roundType the type of the round.
     * @param roundCategoryID the category of the practice round.
     * @param phaseData the current phase of the round.
     * @param enabled <code>true</code> if the round is enabled; <code>false</code> otherwise.
     * @param customProperties the properties of the round.
     * @param practiceRoundDivision the division of the practice round.
     * @param practiceRoomID the ID of the only room in the practice round.
     * @see #getRoundType()
     */
    public RoundData(int roundID, String contestName, String roundName, int roundType, int roundCategoryID,
        PhaseData phaseData, RoundCustomProperties customProperties, int practiceRoundDivision, int practiceRoomID) {
        this(roundID, contestName, roundName, roundType, phaseData, true, customProperties);
        this.practiceRoundDivision = practiceRoundDivision;
        this.practiceRoomID = practiceRoomID;
        this.roundCategoryID = roundCategoryID;
    }

    public void customWriteObject(CSWriter csWriter) throws IOException {
        csWriter.writeInt(getRoundID());
        csWriter.writeString(getContestName());
        csWriter.writeString(getRoundName());
        csWriter.writeInt(getRoundType());
        csWriter.writeInt(getRoundCategoryID());
        csWriter.writeObject(getPhaseData());
        csWriter.writeBoolean(enabled);
        csWriter.writeInt(practiceRoundDivision);
        csWriter.writeInt(practiceRoomID);
        csWriter.writeObject(this.customProperties);
    }

    public void customReadObject(CSReader csReader) throws IOException, ObjectStreamException {
        roundID = csReader.readInt();
        contestName = csReader.readString();
        roundName = csReader.readString();
        roundType = csReader.readInt();
        roundCategoryID = csReader.readInt();
        phaseData = (PhaseData) csReader.readObject();
        enabled = csReader.readBoolean();
        practiceRoundDivision = csReader.readInt();
        practiceRoomID = csReader.readInt();
        customProperties = (RoundCustomProperties) csReader.readObject();
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
     * Gets the name of the contest which the round belongs to.
     * 
     * @return the contest name.
     */
    public String getContestName() {
        return contestName;
    }

    /**
     * Gets the name of the round.
     * 
     * @return the round name.
     */
    public String getRoundName() {
        return roundName;
    }

    /**
     * Gets the category of the round if the round is a practice round.
     * 
     * @return the practice round category.
     */
    public int getRoundCategoryID() {
        return roundCategoryID;
    }

    /**
     * Gets the type of the round.
     * 
     * @return the type of the round.
     * @see ContestConstants#SRM_ROUND_TYPE_ID
     * @see ContestConstants#TOURNAMENT_ROUND_TYPE_ID
     * @see ContestConstants#INTRO_EVENT_ROUND_TYPE_ID
     * @see ContestConstants#PRIVATE_LABEL_TOURNAMENT_ROUND_TYPE_ID
     * @see ContestConstants#TEAM_SRM_ROUND_TYPE_ID
     * @see ContestConstants#LONG_ROUND_TYPE_ID
     * @see ContestConstants#MODERATED_CHAT_ROUND_TYPE_ID
     * @see ContestConstants#HS_SRM_ROUND_TYPE_ID
     * @see ContestConstants#HS_TOURNAMENT_ROUND_TYPE_ID
     * @see ContestConstants#WEAKEST_LINK_ROUND_TYPE_ID
     * @see ContestConstants#FORWARDER_ROUND_TYPE_ID
     * @see ContestConstants#FORWARDER_LONG_ROUND_TYPE_ID
     * @see ContestConstants#LONG_PROBLEM_ROUND_TYPE_ID
     * @see ContestConstants#LONG_PROBLEM_TOURNAMENT_ROUND_TYPE_ID
     * @see ContestConstants#EDUCATION_ALGO_ROUND_TYPE_ID
     * @see ContestConstants#AMD_LONG_PROBLEM_ROUND_TYPE_ID
     * @see ContestConstants#PRACTICE_ROUND_TYPE_ID
     * @see ContestConstants#LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID
     * @see ContestConstants#TEAM_PRACTICE_ROUND_TYPE_ID
     * @see ContestConstants#AMD_LONG_PROBLEM_PRACTICE_ROUND_TYPE_ID
     */
    public int getRoundType() {
        return roundType;
    }

    /**
     * Gets the current phase of the round.
     * 
     * @return the current phase of the round.
     */
    public PhaseData getPhaseData() {
        return phaseData;
    }

    /**
     * Gets a flag indicating if the round is enabled.
     * 
     * @return <code>true</code> if the round is enabled; <code>false</code> otherwise.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets a flag indicating if the round is enabled.
     * 
     * @param enabled <code>true</code> if the round is enabled; <code>false</code> otherwise.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the division of the round if the round is a practice round.
     * 
     * @return the division of the practice round.
     */
    public int getPracticeRoundDivision() {
        return practiceRoundDivision;
    }

    /**
     * Gets the ID of the only room in the round if the round is a practice round.
     * 
     * @return the ID of the practice room in the round.
     */
    public int getPracticeRoomID() {
        return practiceRoomID;
    }

    /**
     * Gets the properties of the round.
     * 
     * @return the properties of the round.
     */
    public RoundCustomProperties getCustomProperties() {
        return customProperties;
    }

    public Object clone() {
        RoundData roundData = new RoundData(roundID, contestName, roundName, roundType, roundCategoryID, phaseData,
            customProperties, practiceRoundDivision, practiceRoomID);
        roundData.enabled = enabled;
        return roundData;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.data.RoundData) [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", ");
        ret.append("contestName = ");
        if (contestName == null) {
            ret.append("null");
        } else {
            ret.append(contestName.toString());
        }
        ret.append(", ");
        ret.append("roundName = ");
        if (roundName == null) {
            ret.append("null");
        } else {
            ret.append(roundName.toString());
        }
        ret.append(", ");
        ret.append("roundType = ");
        ret.append(roundType);
        ret.append(", ");
        ret.append("roundCategoryID = ");
        ret.append(roundCategoryID);
        ret.append(", ");
        ret.append("phaseData = ");
        ret.append(phaseData);
        ret.append(", ");
        ret.append("enabled = ");
        ret.append(enabled);
        ret.append("]");
        return ret.toString();
    }
}
