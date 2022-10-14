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
 * This response contains data of a new challenge in a room. It should be used
 * to construct the challenges and challengers table in the web arena. It is
 * sent automatically when a solution is challenged, and is sent to all users in
 * a room.
 * 
 * Added for TopCoder Competition Engine - Responses for Challenges and Challengers
 * 
 * @author TCSASSEMBLER
 * @version 1.0
 */
public class ChallengeResponse extends WatchableResponse {

    /**
     * The challenge.
     */
    ChallengeData challenge;

    /**
     * Default constructor.
     */
    public ChallengeResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>ChallengeResponse</code>. There is
     * no copy.
     * 
     * @param roomType
     *            the type of the room.
     * @param roomID
     *            the ID of the room.
     * @param challenge
     *            - the challenge data
     */
    public ChallengeResponse(int roomType, int roomID, ChallengeData challenge) {
        super(roomType, roomID);
        this.challenge = challenge;
    }

    /**
     * writes the object.
     * 
     * @param writer
     * @throws IOException
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeObject(challenge);
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
        challenge = (ChallengeData) reader.readObject();
    }

    /**
     * Challenge getter.
     * 
     * @return challenge
     */
    public ChallengeData getChallenge() {
        return challenge;
    }

    /**
     * Challenge setter
     * 
     * @param challenge
     */
    public void setChallenge(ChallengeData challenge) {
        this.challenge = challenge;
    }

    /**
     * simple to string implementation.
     * 
     * @return - string representation of the object
     */
    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.ChallengeResponse) [");
        ret.append("challenge = ");
        if (challenge == null) {
            ret.append("null");
        } else {
            ret.append(challenge);
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}