/*
 * Copyright (C) 2014 TopCoder Inc., All Rights Reserved.
 */

package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.netCommon.contestantMessages.request.RoundProblemsRequest;
import com.topcoder.netCommon.contestantMessages.response.data.ComponentLabel;
import com.topcoder.netCommon.contestantMessages.response.data.ProblemLabel;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send the list of problems for the specific round and division
 * for the user who made the {@link RoundProblemsRequest} request.<br>
 * Use: When receiving this response, the client should update the current user's
 * list of problems.
 * It contains only non-sensitive information about problems.
 * Note: The response is sent directly by server as a response to {@link RoundProblemsRequest}
 * request.
 *
 * @author dexy
 * @version 1.0
 */
public class RoundProblemsResponse extends BaseResponse {
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = -1782630412823513966L;

    /** The Constant TOSTRING_BUFFER_LENGTH. */
    private static final int TOSTRING_BUFFER_LENGTH = 1000;

    /** Represents the division of the round. */
    private int divisionID = -1;

    /** Represents the ID of the round. */
    private long roundID = -1;

    /** Represents the problems assigned to the division of the round. */
    private ProblemLabel [] problems;

    /** Represents the problem components assigned to the current user of the receiver. */
    private ComponentLabel [] assignedComponents;

    /** The error message. */
    private String errorMessage = null;

    /**
     * Creates a new instance of {@link RoundProblemsResponse}.
     * It is required by custom serialization.
     */
    public RoundProblemsResponse() {
    }

    /**
     * Instantiates a new round problems response.
     *
     * @param roundID the round id
     * @param divisionID the division id
     */
    public RoundProblemsResponse(long roundID, int divisionID) {
        setRoundID(roundID);
        setDivisionID(divisionID);
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
     * Gets the problems.
     *
     * @return the problems
     */
    public ProblemLabel[] getProblems() {
        return problems;
    }

    /**
     * Sets the problems.
     *
     * @param problems the new problems
     */
    public void setProblems(ProblemLabel[] problems) {
        this.problems = problems;
    }

    /**
     * Gets the assigned components.
     *
     * @return the assigned components
     */
    public ComponentLabel[] getAssignedComponents() {
        return assignedComponents;
    }

    /**
     * Sets the assigned components.
     *
     * @param assignedComponents the new assigned components
     */
    public void setAssignedComponents(ComponentLabel[] assignedComponents) {
        this.assignedComponents = assignedComponents;
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

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customReadObject(com.topcoder.shared.netCommon.CSReader)
     */
    /**
     * Custom read object.
     *
     * @param csReader the cs reader
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void customReadObject(CSReader csReader) throws IOException {
        super.customReadObject(csReader);
        roundID = csReader.readLong();
        divisionID = csReader.readInt();
        problems = (ProblemLabel []) csReader.readObjectArray(ProblemLabel.class);
        assignedComponents = (ComponentLabel []) csReader.readObjectArray(ComponentLabel.class);
        errorMessage = csReader.readString();
    }

    /* (non-Javadoc)
     * @see com.topcoder.shared.netCommon.messages.Message#customWriteObject(com.topcoder.shared.netCommon.CSWriter)
     */
    /**
     * Custom write object.
     *
     * @param csWriter the cs writer
     * @throws IOException Signals that an I/O exception has occurred.
     */
    @Override
    public void customWriteObject(CSWriter csWriter) throws IOException {
        super.customWriteObject(csWriter);
        csWriter.writeLong(roundID);
        csWriter.writeInt(divisionID);
        csWriter.writeObjectArray(problems);
        csWriter.writeObjectArray(assignedComponents);
        csWriter.writeString(errorMessage);
    }

    /* (non-Javadoc)
     * @see com.topcoder.netCommon.contestantMessages.response.BaseResponse#toString()
     */
    /**
     * To string.
     *
     * @return the string
     */
    @Override
    public String toString() {
        StringBuffer ret = new StringBuffer(TOSTRING_BUFFER_LENGTH);
        ret.append("(");
        ret.append(getClass().getName());
        ret.append(") [");
        ret.append("roundID = ");
        ret.append(roundID);
        ret.append(", divisionID = ");
        ret.append(divisionID);
        ret.append("problems = ");
        if (problems == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < problems.length; i++) {
                ret.append(problems [i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        if (assignedComponents == null) {
            ret.append("null");
        } else {
            ret.append("{");
            for (int i = 0; i < assignedComponents.length; i++) {
                ret.append(assignedComponents [i].toString() + ",");
            }
            ret.append("}");
        }
        ret.append(", ");
        ret.append("]");
        return ret.toString();
    }
}
