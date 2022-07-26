/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Arrays;

import com.topcoder.netCommon.contestantMessages.response.data.ChallengeData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * This response contains list of all challenges in a room. It should be used to
 * construct the challenges and challengers table in the web arena. It is sent
 * automatically when user enters the room or reconnects to a room
 * 
 * Added for TopCoder Competition Engine - Responses for Challenges and Challengers
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ChallengesListResponse extends WatchableResponse {

    /**
     * The list of challenges.
     */
    ChallengeData[] challenges;

    /**
     * Default constructor, required for the CustomSerialization
     */
    public ChallengesListResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>ChallengesListResponse</code>.
     * @param roomType
     *            - the room type
     * @param roomID
     *            - ID of the room
     * @param challenges
     *            - array of the previous challenges
     */
    public ChallengesListResponse(int roomType, int roomID,
            ChallengeData[] challenges) {
        super(roomType, roomID);
        this.challenges = challenges;
    }

    /**
     * Writes the object.
     * 
     * @param writer
     * @throws IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObjectArray(challenges);
    }

    /**
     * Reads the object.
     * 
     * @param reader
     * @throws IOException
     * @throws ObjectStreamException
     */
    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        super.customReadObject(reader);
        challenges = (ChallengeData[]) reader
                .readObjectArray(ChallengeData.class);
    }

    /**
     * Challenges getter.
     * 
     * @return challenges
     */
    public ChallengeData[] getChallenges() {
        return challenges;
    }

    /**
     * Challenges setter.
     * 
     * @param challenges
     */
    public void setChallenges(ChallengeData[] challenges) {
        this.challenges = challenges;
    }

    /**
     * simple to string implementation
     * 
     * @return
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ChallengesListResponse) [");
        ret.append("challenges = ");
        if (challenges == null) {
            ret.append("null");
        } else {
            ret.append(Arrays.asList(challenges));
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}