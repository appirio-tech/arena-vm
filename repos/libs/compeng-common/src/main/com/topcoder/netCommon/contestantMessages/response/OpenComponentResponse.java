/*
* Copyright (C) - 2014 - 2015 TopCoder Inc., All Rights Reserved.
*/
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
 * <p>
 * Changes in version 1.1 (Return Time Infos When Opening Problems v1.0) :
 * <ol>
 *      <li>Add {@link #lastSavedTime} field and its setter and getter method.</li>
 *      <li>Add {@link #lastCompiledTime} field and its setter and getter method.</li>
 *      <li>Add {@link #lastSubmitTime} field and its setter and getter method.</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes for 1.2 (Web Socket Listener - Backend Logic Update To Generate and Return the image)
 * <ol>
 *     <li>Added codeImage property along with a getter.</li>
 *     <li>Added new constructor that takes the codeImage as parameter.</li>
 *     <li>Updated the old constructor to call the new one with <code>null</code> codeImage</li>
 *     <li>Updated the old toString method to include the codeImage</li>
 * </ol>
 * </p>
 *
 * <p>
 * Changes for 1.3 (TopCoder Competition Engine - Return Submission Information)
 * <ol>
 *     <li>Add {@link #openTime} field and its setter and getter method.</li>
 *     <li>Add {@link #submissionCount} field and its setter and getter method.</li>
 *     <li>Add {@link #score} field and its setter and getter method.</li>
 *     <li>Update {@link #customWriteObject(CSWriter writer)} method to write the new added fields.</li>
 *     <li>Update {@link #customReadObject(CSReader reader)} method to read the new added fields.</li>
 *     <li>Update {@link #toString} method to append the new added fields.</li>
 * </ol>
 * </p>
 * @author Lars Backstrom, xjtufreeman, TCSASSEMBLER
 * @version 1.3
 */
public class OpenComponentResponse extends WatchableResponse {
    /** Represents the handle of the user whose source code has been requested. */
    private String coderHandle;

    /** Represents the ID of the problem component. */
    private int componentID;

    /** Represents the source code. */
    private String code;

    /**
     * Represents a JPG image of the source code encoded in a data URI string. (data:jpg;base64,...)
     * @since 1.2
     */
    private final String codeImage;

    /** Represents the status of the source code. */
    private int editable;

    /** Represents the ID of the programming language. */
    private Integer languageID;
    /**
     * The last saved time.
     * @since 1.1
     */
    private long lastSavedTime;
    /**
     * The last compiled time.
     * @since 1.1
     */
    private long lastCompiledTime;
    /**
     * The last submit time.
     * @since 1.1
     */
    private long lastSubmitTime;
    /**
     * The open time.
     * @since 1.3
     */
    private long openTime;
    /**
     * The previous submission number.
     * @since 1.3
     */
    private int submissionCount;
    /**
     * The last solution score.
     * @since 1.3
     */
    private double score;
    /**
     * Creates a new instance of <code>OpenComponentResponse</code>. It is required by custom serialization.
     */
    public OpenComponentResponse() {
        super(-1, -1);
        this.codeImage = null;
    }

    /**
     * Creates a new instance of <code>OpenComponentResponse</code>.
     * 
     * @param coderHandle the handle of the user whose source code has been requested.
     * @param componentID the ID of the problem component.
     * @param code the source code.
     * @param codeImage the source code image encoded in the data URI format (data:jpg;base64,...)
     * @param editable the status of the source code.
     * @param roomType the type of the room where the user is assigned to.
     * @param roomID the ID of the room where the user is assigned to.
     * @param languageID the ID of the programming language.
     * @see #getEditable()
     */
    public OpenComponentResponse(String coderHandle, int componentID, String code, String codeImage, int editable, int roomType,
        int roomID, int languageID) {
        super(roomType, roomID);
        this.coderHandle = coderHandle;
        this.componentID = componentID;
        this.code = code;
        this.codeImage = codeImage;
        this.editable = editable;
        this.languageID = new Integer(languageID);
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
        this(coderHandle, componentID, code, null, editable, roomType, roomID, languageID);
    }

    /**
     * Custom writer object.
     * @param writer the write
     * @throws IOException if any IO error occur.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeString(coderHandle);
        writer.writeInt(componentID);
        writer.writeString(code);
        writer.writeInt(editable);
        writer.writeInt(languageID.intValue());
        writer.writeLong(lastSavedTime);
        writer.writeLong(lastCompiledTime);
        writer.writeLong(lastSubmitTime);
        writer.writeLong(openTime);
        writer.writeInt(submissionCount);
        writer.writeDouble(score);
    }
    /**
     * Custom read object.
     * @param reader the reader
     * @throws IOException if any IO error occur.
     * @throws ObjectStreamException if any object stream error occur.
     */
    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        coderHandle = reader.readString();
        componentID = reader.readInt();
        code = reader.readString();
        editable = reader.readInt();
        languageID = new Integer(reader.readInt());
        lastSavedTime = reader.readLong();
        lastCompiledTime = reader.readLong();
        lastSubmitTime = reader.readLong();
        openTime = reader.readLong();
        submissionCount = reader.readInt();
        score = reader.readDouble();
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
     * Gets a JPG image of the source code encoded in a data URI string. (data:jpg;base64,...)
     * @return a JPG image of the source code encoded in a data URI string. (data:jpg;base64,...).
     * @since 1.2
     */
    public String getCodeImage() {
        return codeImage;
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
    /**
     * Get the last saved time.
     * @return the last saved time.
     */
    public long getLastSavedTime() {
        return lastSavedTime;
    }
    /**
     * Set the last saved time.
     * @param lastSavedTime the last saved time.
     */
    public void setLastSavedTime(long lastSavedTime) {
    	this.lastSavedTime = lastSavedTime;
    }
    /**
     * Get the last compiled time.
     * @return the last compiled time.
     */
    public long getLastCompiledTime() {
        return lastCompiledTime;
    }
    /**
     * Set the last compiled time.
     * @param lastCompiledTime the last compiled time.
     */
    public void setLastCompiledTime(long lastCompiledTime) {
    	this.lastCompiledTime = lastCompiledTime;
    }
    /**
     * Get the last submit time.
     * @return the last submit time.
     */
    public long getLastSubmitTime() {
        return lastSubmitTime;
    }
    /**
     * Set the last submit time.
     * @param lastSubmitTime the last submit time.
     */
    public void setLastSubmitTime(long lastSubmitTime) {
    	this.lastSubmitTime = lastSubmitTime;
    }
    /**
     * Get open time.
     * @return open time.
     * @since 1.3
     */
    public long getOpenTime() {
        return openTime;
    }
    /**
     * Set the open time.
     * @param openTime the open time.
     * @since 1.3
     */
    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }
    /**
     * Get previous submission number.
     * @return previous submission number.
     * @since 1.3
     */
    public int getSubmissionCount() {
        return submissionCount;
    }
    /**
     * Set the previous submission number.
     * @param submissionCount the previous submission number.
     * @since 1.3
     */
    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }
    /**
     * Get last solution score.
     * @return last solution score.
     * @since 1.3
     */
    public double getScore() {
        return score;
    }
    /**
     * Set the last solution score.
     * @param score the last solution score
     * @since 1.3
     */
    public void setScore(double score) {
        this.score = score;
    }


    /**
     * The toString of object
     * @return the toString object
     */
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
        ret.append(", lastSavedTime = ");
        ret.append(lastSavedTime);
        ret.append(", lastCompiledTime = ");
        ret.append(lastCompiledTime);
        ret.append(", lastSubmitTime = ");
        ret.append(lastSubmitTime);
        ret.append(", openTime = ");
        ret.append(openTime);
        ret.append(", submissionCount = ");
        ret.append(submissionCount);
        ret.append(", score = ");
        ret.append(score);
        ret.append(", codeImage = ");
        ret.append(codeImage);
        ret.append("]");
        return ret.toString();
    }
}