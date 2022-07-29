/*
 * LongSubmitRequest Created 05/31/2007
 */
package com.topcoder.netCommon.contestantMessages.request;

import java.io.IOException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a request to submit the solution for a marathon problem.<br>
 * Use: When the current user wants to submit his solution for an open marathon problem, this request should be sent.<br>
 * Note: Unlike the algorithm submissions, there is no need to compile the marathon solution before submission. Also,
 * the user should be in the room where the marathon problem exists. The round status for the marathon problem must be
 * in coding phase. The problem must be open when doing the submission.
 * 
 * @author Diego Belfer (Mural)
 * @version $Id: LongSubmitRequest.java 72292 2008-08-12 09:10:29Z qliu $
 * @see OpenComponentForCodingRequest
 */
public class LongSubmitRequest extends BaseRequest {
    /** Represents the ID of the problem component of the solution. */
    private int componentID;

    /** Represents the ID of the programming language used by the solution. */
    private int languageID;

    /** Represents the source code of the solution. */
    private String code;

    /** Represents a flag indicating if this submission is an example submission. */
    private boolean example;

    /**
     * Creates a new instance of <code>LongSubmitRequest</code>. It is required by custom serialization.
     */
    public LongSubmitRequest() {
    }

    /**
     * Creates a new instance of <code>LongSubmitRequest</code>.
     * 
     * @param code the source code of the solution.
     * @param componentID the ID of the problem component of the solution.
     * @param languageID the ID of the programming language of the solution.
     * @param example <code>true</code> if this submission is an example submission; <code>false</code> otherwise.
     */
    public LongSubmitRequest(String code, int componentID, int languageID, boolean example) {
        this.code = code;
        this.componentID = componentID;
        this.languageID = languageID;
        this.example = example;
    }

    public int getRequestType() {
        return ContestConstants.LONG_SUBMIT_REQUEST;
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        code = reader.readString();
        componentID = reader.readInt();
        languageID = reader.readInt();
        example = reader.readBoolean();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(code);
        writer.writeInt(componentID);
        writer.writeInt(languageID);
        writer.writeBoolean(example);
    }

    /**
     * Gets the source code of the solution.
     * 
     * @return the source code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the ID of the problem component of the solution.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the ID of the programming language of the solution.
     * 
     * @return the ID of the programming language.
     */
    public int getLanguageID() {
        return languageID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.request.LongSubmitRequest) [");
        ret.append("code = ");
        if (code == null) {
            ret.append("null");
        } else {
            ret.append(code.toString());
        }
        ret.append(", ");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("example = ");
        ret.append(example);
        ret.append("]");
        return ret.toString();
    }

    /**
     * Gets a flag indicating if the submission is an example submission.
     * 
     * @return <code>true</code> if this submission is an example submission; <code>false</code> otherwise.
     */
    public boolean isExample() {
        return example;
    }
}
