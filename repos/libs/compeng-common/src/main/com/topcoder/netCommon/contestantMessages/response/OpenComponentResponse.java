package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.netCommon.contest.ContestConstants;
import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to send a user's source code for a problem component in a room of a round.<br>
 * Use: This response may be part of the responses of <code>OpenComponentForCodingRequest</code> or
 * <code>GetChallengeProblemRequest</code>. Once the source code is received by the client, the current user should
 * be able to operate on the source code. The types of operations allowed depends on the status of the code. The client
 * should strictly follow it.
 * 
 * @author Lars Backstrom
 * @version $Id: OpenComponentResponse.java 72313 2008-08-14 07:16:48Z qliu $
 */
public class OpenComponentResponse extends WatchableResponse {
    /** Represents the handle of the user whose source code has been requested. */
    private String coderHandle;

    /** Represents the ID of the problem component. */
    private int componentID;

    /** Represents the source code. */
    private String code;

    /** Represents the status of the source code. */
    private int editable;

    /** Represents the ID of the programming language. */
    private Integer languageID;

    /**
     * Creates a new instance of <code>OpenComponentResponse</code>. It is required by custom serialization.
     */
    public OpenComponentResponse() {
        super(-1, -1);
    }

    /**
     * Creates a new instance of <code>OpenComponentResponse</code>.
     * 
     * @param coderHandle the handle of the user whose source code has been requested.
     * @param componentID the ID of the problem component.
     * @param code the source code.
     * @param editable the status of the source code.
     * @param roomType the type of the room where the user is assigned to.
     * @param roomID the ID of the room where the user is assigned to.
     * @param languageID the ID of the programming language.
     * @see #getEditable()
     */
    public OpenComponentResponse(String coderHandle, int componentID, String code, int editable, int roomType,
        int roomID, int languageID) {
        super(roomType, roomID);
        this.coderHandle = coderHandle;
        this.componentID = componentID;
        this.code = code;
        this.editable = editable;
        this.languageID = new Integer(languageID);
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(coderHandle);
        writer.writeInt(componentID);
        writer.writeString(code);
        writer.writeInt(editable);
        writer.writeInt(languageID.intValue());
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coderHandle = reader.readString();
        componentID = reader.readInt();
        code = reader.readString();
        editable = reader.readInt();
        languageID = new Integer(reader.readInt());
    }

    /**
     * Gets the handle of the user whose source code has been requested.
     * 
     * @return the handle of the user whose source code has been requested.
     */
    public String getWriterHandle() {
        return coderHandle;
    }

    /**
     * Gets the ID of the problem component.
     * 
     * @return the ID of the problem component.
     */
    public int getComponentID() {
        return componentID;
    }

    /**
     * Gets the source code.
     * 
     * @return the source code.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the status of the source code.
     * <ul>
     * <li><code>ContestConstants.EDIT_SOURCE_RW</code>: The current user do all operations.</li>
     * <li><code>ContestConstants.EDIT_SOURCE_RO</code>: The current user cannot do editing operations, but can copy
     * the code.</li>
     * <li><code>ContestConstants.VIEW_SOURCE</code>: The current user cannot do any operations, including coping
     * the code.</li>
     * </ul>
     * 
     * @return the status of the source code.
     * @see ContestConstants#EDIT_SOURCE_RW
     * @see ContestConstants#EDIT_SOURCE_RO
     * @see ContestConstants#VIEW_SOURCE
     */
    public int getEditable() {
        return editable;
    }

    /**
     * Gets the ID of the programming language.
     * 
     * @return the programming language ID.
     */
    public Integer getLanguageID() {
        return languageID;
    }

    public String toString() {
        StringBuffer ret = new StringBuffer(1000);
        ret.append("(com.topcoder.netCommon.contestantMessages.response.OpenComponentResponse) [");
        ret.append("componentID = ");
        ret.append(componentID);
        ret.append(", ");
        ret.append("code = ");
        if (code == null) {
            ret.append("null");
        } else {
            ret.append(code.toString());
        }
        ret.append(", ");
        ret.append("editable = ");
        ret.append(editable);
        ret.append(", ");
        ret.append("language = ");
        ret.append(languageID);
        ret.append(", ");
        ret.append("coderHandle = ");
        ret.append(coderHandle);
        ret.append("]");
        return ret.toString();
    }
}