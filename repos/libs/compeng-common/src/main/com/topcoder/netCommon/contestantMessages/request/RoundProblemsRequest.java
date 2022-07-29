/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request of the problems for given round.
 * Use: This request is sent when the current user wants to get the list of
 * problems assigned to the specific round and division.<br>
 * Note: Only non-sensitive information about problems will be returned.
 *
 * @author dexy
 * @version 1.0
 */
public class RoundProblemsRequest extends BaseRequest {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 8068013352226719748L;

    /** The Constant TOSTRING_BUFFER_LENGTH. */
    private static final int TOSTRING_BUFFER_LENGTH = 1000;

    /** The round id. */
    private long roundID;

    /** The division id. */
    private int divisionID;

    /**
     * Creates a new instance of {@link RoundProblemsRequest}.
     * It is required by custom serialization.
     */
    public RoundProblemsRequest() {
    }

    /**
     * Instantiates a new round problems request.
     *
     * @param roundID the round id
     * @param divisionID the division id
     */
    public RoundProblemsRequest(long roundID, int divisionID) {
        this.setRoundID(roundID);
        this.setDivisionID(divisionID);
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @Override
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(roundID);
        writer.writeInt(divisionID);
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    @Override
    public void customReadObject(CSReader reader) throws IOException {
        roundID = reader.readLong();
        divisionID = reader.readInt();
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.request.BaseRequest#getRequestType()
     */
    @Override
    public int getRequestType() {
        return 0;
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.request.BaseRequest#toString()
     */
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TOSTRING_BUFFER_LENGTH);
        ret.append("(");
        ret.append(getClass().getName());
        ret.append(") [");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the round id.
     *
     * @return the round id
     */
    public long getRoundID() {
        return roundID;
    }

    /**
     * Sets the round id.
     *
     * @param roundID the new round id
     */
    public void setRoundID(long roundID) {
        this.roundID = roundID;
    }

    /**
     * Gets the division id.
     *
     * @return the division id
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * Sets the division id.
     *
     * @param divisionID the new division id
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }
}