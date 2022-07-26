/*
 * LongCoderComponentItem Created 06/12/2007
 */
package com.topcoder.netCommon.contestantMessages.response.data;

import java.io.IOException;
import java.io.ObjectStreamException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines the status of a solution for a marathon problem component. It contains extra information about full
 * submissions and example submissions. The programming language ID in <code>CoderComponentItem</code> is the
 * programming language ID of the last full submission.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: LongCoderComponentItem.java 72424 2008-08-20 08:06:01Z qliu $
 */
public class LongCoderComponentItem extends CoderComponentItem {
    /** Represents the number of full submissions. */
    private int submissionCount;

    /** Represents the time of the last full submission. */
    private long lastSubmissionTime;

    /** Represents the number of example submissions. */
    private int exampleSubmissionCount;

    /** Represents the time of the last example submission. */
    private long exampleLastSubmissionTime;

    /** Represents the ID of the programming language of the last example submission. */
    private int exampleLastLanguage;

    /**
     * Creates a new instance of <code>LongCoderComponentItem</code>. It is required by custom serialization.
     */
    public LongCoderComponentItem() {
    }

    /**
     * Creates a new instance of <code>CoderComponentItem</code>. The programming language is set as Java, and the
     * number of passed system test cases is unset. The time is represented by the number of milliseconds since January
     * 1, 1970, 00:00:00 GMT.
     * 
     * @param componentID the ID of the problem component.
     * @param points the current score of the solution, multiplied by 100.
     * @param status the status of the solution.
     * @param language the ID of the programming language of the last full submission.
     * @param submissionCount the number of full submissions.
     * @param lastSubmissionDate the time of the last full submission.
     * @param exampleSubmissionCount the number of example submissions.
     * @param exampleLastSubmissionDate the time of the last example submission.
     * @param exampleLastLanguage the ID of the programming language of the last example submission.
     * @see #getStatus()
     * @see java.util.Date#getTime()
     */
    public LongCoderComponentItem(long componentID, int points, int status, int language, int submissionCount,
        long lastSubmissionDate, int exampleSubmissionCount, long exampleLastSubmissionDate, int exampleLastLanguage) {
        super(componentID, points, status, language);
        this.submissionCount = submissionCount;
        this.lastSubmissionTime = lastSubmissionDate;
        this.exampleSubmissionCount = exampleSubmissionCount;
        this.exampleLastSubmissionTime = exampleLastSubmissionDate;
        this.exampleLastLanguage = exampleLastLanguage;
    }

    /**
     * Gets the time of the last example submission. The time is represented by the number of milliseconds since January
     * 1, 1970, 00:00:00 GMT.
     * 
     * @return the time of the last example submission.
     * @see java.util.Date#getTime()
     */
    public long getExampleLastSubmissionTime() {
        return exampleLastSubmissionTime;
    }

    /**
     * Gets the number of example submissions.
     * 
     * @return the number of example submissions.
     */
    public int getExampleSubmissionCount() {
        return exampleSubmissionCount;
    }

    /**
     * Gets the time of the last full submission. The time is represented by the number of milliseconds since January 1,
     * 1970, 00:00:00 GMT.
     * 
     * @return the time of the last full submission.
     * @see java.util.Date#getTime()
     */
    public long getLastSubmissionTime() {
        return lastSubmissionTime;
    }

    /**
     * Gets the number of full submissions.
     * 
     * @return the number of full submissions.
     */
    public int getSubmissionCount() {
        return submissionCount;
    }

    /**
     * Gets the ID of the programming language of the last example submission.
     * 
     * @return the ID of the programming language of the last example submission.
     */
    public int getExampleLastLanguage() {
        return exampleLastLanguage;
    }

    public void customReadObject(CSReader reader) throws IOException, ObjectStreamException {
        super.customReadObject(reader);
        this.submissionCount = reader.readInt();
        this.lastSubmissionTime = reader.readLong();
        this.exampleSubmissionCount = reader.readInt();
        this.exampleLastSubmissionTime = reader.readLong();
        this.exampleLastLanguage = reader.readInt();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(this.submissionCount);
        writer.writeLong(this.lastSubmissionTime);
        writer.writeInt(this.exampleSubmissionCount);
        writer.writeLong(this.exampleLastSubmissionTime);
        writer.writeInt(this.exampleLastLanguage);
    }
}
