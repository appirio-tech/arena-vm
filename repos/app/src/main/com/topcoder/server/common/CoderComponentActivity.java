/*
* Copyright (C) 2014-2015 TopCoder Inc., All Rights Reserved.
*/
package com.topcoder.server.common;

import java.io.IOException;
import java.io.ObjectStreamException;
import java.io.Serializable;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;
import com.topcoder.shared.netCommon.CustomSerializable;

/**
 * The coder component activity
 *
 * <p>
 * Changes for 1.1 (TopCoder Competition Engine - Return Submission Information)
 * <ol>
 *     <li>Add {@link #openTime} field and its setter and getter method.</li>
 *     <li>Add {@link #submissionCount} field and its setter and getter method.</li>
 *     <li>Add {@link #score} field and its setter and getter method.</li>
 *     <li>Update {@link #customWriteObject(CSWriter writer)} method to write the new added fields.</li>
 *     <li>Update {@link #customReadObject(CSReader reader)} method to read the new added fields.</li>
 *     <li>Update {@link #toString} method to append the new added fields.</li>
 * </ol>
 * </p>
 *
 * @author xjtufreeman, TCSASSEMBLER
 * @version 1.1
 */
public class CoderComponentActivity implements Serializable, CustomSerializable {

    /**
     * The serial version UID.
     */
    private static final long serialVersionUID = 1941834251079308639L;
    /**
     * The component id.
     */
    private int componentId;
    /**
     * The round id.
     */
    private int roundId;
    /**
     * The coder id.
     */
    private int coderId;
    /**
     * The last saved time.
     */
    private long lastSavedTime;
    /**
     * The last compiled time.
     */
    private long lastCompiledTime;
    /**
     * The last submit time.
     */
    private long lastSubmitTime;
    /**
     * The open time.
     * @since 1.1
     */
    private long openTime;
    /**
     * The previous submission number.
     * @since 1.1
     */
    private int submissionCount;
    /**
     * The last solution score.
     * @since 1.1
     */
    private double score;
    /**
     * Creates a new instance of <code>SimpleComponent</code>. It is required by custom serialization.
     */
    public CoderComponentActivity() {
    }
    /**
     * The constructor.
     * @param componentId the component id.
     * @param roundId the round id.
     * @param coderId the coder id.
     */
    public CoderComponentActivity(int componentId, int roundId, int coderId) {
        this.coderId = coderId;
        this.componentId = componentId;
        this.roundId = roundId;
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
     * Get last compiled time.
     * @return last compiled time.
     */
    public long getLastCompiledTime() {
        return lastCompiledTime;
    }
    /**
     * Set last compiled time.
     * @param lastCompiledTime last compiled time.
     */
    public void setLastCompiledTime(long lastCompiledTime) {
        this.lastCompiledTime = lastCompiledTime;
    }
    /**
     * Get last submit time.
     * @return last submit time.
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
     * @since 1.1
     */
    public long getOpenTime() {
        return openTime;
    }
    /**
     * Set the open time.
     * @param openTime the open time.
     * @since 1.1
     */
    public void setOpenTime(long openTime) {
        this.openTime = openTime;
    }
    /**
     * Get previous submission number.
     * @return previous submission number.
     * @since 1.1
     */
    public int getSubmissionCount() {
        return submissionCount;
    }
    /**
     * Set the previous submission number.
     * @param submissionCount the previous submission number.
     * @since 1.1
     */
    public void setSubmissionCount(int submissionCount) {
        this.submissionCount = submissionCount;
    }
    /**
     * Get last solution score.
     * @return last solution score.
     * @since 1.1
     */
    public double getScore() {
        return score;
    }
    /**
     * Set the last solution score.
     * @param score the last solution score
     * @since 1.1
     */
    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Get the component id.
     * @return the component id.
     */
    public int getComponentId() {
        return componentId;
    }
    /**
     * Get the round id.
     * @return the round id.
     */
    public int getRoundId() {
        return roundId;
    }
    /**
     * Get the coder id.
     * @return the coder id.
     */
    public int getCoderId() {
        return coderId;
    }
    /**
     * Get the cache key
     * @param componentId the component id.
     * @param roundId the round id.
     * @param coderId the coder id.
     * @return the cache key.
     */
    public static String getCacheKey(int componentId, int roundId, int coderId) {
        return "coderComponentActivity_" + componentId + "," + roundId + "," + coderId;
    }
    /**
     * Get the cache key.
     * @return the cache key.
     */
    public String getCacheKey() {
        return "coderComponentActivity_" + componentId + "," + roundId + "," + coderId;
    }
    /**
     * The toString of object
     * @return the toString object
     */
    public String toString() {
        return "componentId=" + componentId + ", roundId=" + roundId + ", coderId=" + coderId +
                ", lastSavedTime=" + lastSavedTime + ", lastCompiledTime=" + lastCompiledTime +
                ", lastSubmitTime=" + lastSubmitTime + ", openTime=" + openTime +
                ", submissionCount=" + submissionCount + ", score=" + score;
    }
    /**
     * Custom writer object.
     * @param writer the writer
     * @throws IOException if any IO error occur.
     */
    public void customWriteObject(CSWriter writer) throws IOException {
        writer.writeLong(lastSavedTime);
        writer.writeLong(lastCompiledTime);
        writer.writeLong(lastSubmitTime);
        writer.writeLong(openTime);
        writer.writeInt(submissionCount);
        writer.writeDouble(score);
        writer.writeInt(componentId);
        writer.writeInt(roundId);
        writer.writeInt(coderId);
    }
    /**
     * Custom read object.
     * @param reader the reader
     * @throws IOException if any IO error occur.
     * @throws ObjectStreamException if any object stream error occur.
     */
    public void customReadObject(CSReader reader) throws IOException,
            ObjectStreamException {
        lastSavedTime = reader.readLong();
        lastCompiledTime = reader.readLong();
        lastSubmitTime = reader.readLong();
        openTime = reader.readLong();
        submissionCount = reader.readInt();
        score = reader.readDouble();
        componentId = reader.readInt();
        roundId = reader.readInt();
        coderId = reader.readInt();
    }
}
