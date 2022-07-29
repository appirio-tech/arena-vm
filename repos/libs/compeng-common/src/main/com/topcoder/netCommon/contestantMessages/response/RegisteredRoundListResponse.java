/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.request.RegisteredRoundListRequest;
import com.topcoder.netCommon.contestantMessages.response.data.RoundData;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of available registered rounds for the user
 * who made the {@link RegisteredRoundListRequest} request.<br>
 * Use: When receiving this response, the client should update the mechanism for
 * the current user to choose a round to enter, get problems, etc..
 * All previous registered rounds should be replaced by this new list.<br>
 * Note: The response is sent directly by server as a response to {@link RegisteredRoundListRequest}
 * request.
 *
 * @author dexy
 * @version 1.0
 */
public class RegisteredRoundListResponse extends BaseResponse {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -3368470609948920461L;

    /** The Constant TOSTRING_BUFFER_LENGTH. */
    private static final int TOSTRING_BUFFER_LENGTH = 1000;

    /** Represents the information of the rounds. */
    private RoundData[] roundData;

    /** The error message. */
    private String errorMessage;

    /**
     * Creates a new instance of {@link RegisteredRoundListResponse}.
     * It is required by custom serialization.
     */
    public RegisteredRoundListResponse() {
    }

    /**
     * Creates a new instance of {@link RegisteredRoundListResponse}.
     *
     * @param roundData the information of the rounds (only active) the user is registered to.
     */
    public RegisteredRoundListResponse(RoundData[] roundData) {
        this.roundData = roundData;
    }

    /**
     * Gets the information of the rounds. There is no copy.
     *
     * @return the information of the rounds.
     */
    public RoundData[] getRoundData() {
        return roundData;
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    @Override
    public void customReadObject(CSReader csReader) throws IOException {
        super.customReadObject(csReader);
        roundData = (RoundData[]) csReader.readObjectArray(RoundData.class);
        errorMessage = csReader.readString();
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    @Override
    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeObjectArray(roundData);
        csWriter.writeString(errorMessage);
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.response.BaseResponse#toString()
     */
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TOSTRING_BUFFER_LENGTH);
        ret.append("(");
        ret.append(getClass().getName());
        ret.append(") [");
        ret.append("roundData = ");
        if (roundData == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < roundData.length; i++) {
                ret.append(roundData[i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the error message.
     *
     * @param errorMessage the new error message
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
