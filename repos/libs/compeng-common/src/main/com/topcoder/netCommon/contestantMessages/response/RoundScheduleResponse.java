package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.PhaseData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the schedule of a round to the client.<br>
 * Use: This response is specific to <code>RoundScheduleRequest</code>. The schedule of the round should be shown to
 * the current user when receiving this response.<br>
 * Note: The round information should be available to the current user, since the client can only request for an active
 * round.
 * 
 * @author Lars Backstrom
 * @version $Id: RoundScheduleResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class RoundScheduleResponse extends BaseResponse {
    /** Represents the ID of the round. */
    private long roundID;

    /** Represents the schedule of each phase in the round. */
    private PhaseData[] schedule = null;

    /**
     * Creates a new instance of <code>RoundScheduleResponse</code>. It is required by custom serialization.
     */
    public RoundScheduleResponse() {
    }

    /**
     * Creates a new instance of <code>RoundScheduleResponse</code>. There is no copy.
     * 
     * @param roundID the ID of the round.
     * @param schedule the schedule of each phase in the round.
     */
    public RoundScheduleResponse(long roundID, PhaseData[] schedule) {
        this.roundID = roundID;
        this.schedule = schedule;
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeLong(roundID);
        writer.writeObjectArray(schedule);
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        roundID = reader.readLong();
        schedule = (PhaseData[]) reader.readObjectArray(PhaseData.class);
    }

    /**
     * Gets the ID of the round.
     * 
     * @return the round ID.
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Gets the schedule of each phase in the round. There is no copy.
     * 
     * @return the schedule of each phase in the round.
     */
    public PhaseData[] getSchedule() {
        return schedule;
    }

    public String toString() {
        return "(RoundScheduleResponse)[roundID = " + roundID + " schedule = " + Arrays.asList(schedule) + "]";
    }
}