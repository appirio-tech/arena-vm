/*
 * SubmissionHistoryResponse Created 06/14/2007
 */
package com.topcoder.netCommon.contestantMessages.response;

import java.io.IOException;

import com.topcoder.shared.netCommon.CSReader;
import com.topcoder.shared.netCommon.CSWriter;

/**
 * Defines a response to notify the receiver the submission/system testing history of a coder in a marathon round.<br>
 * Use: This response is specific to <code>CoderHistoryRequest</code>. When receiving this response, the client
 * should show a window containing the history data of a coder. This response will only be sent when the request is
 * specific to example or full submission. When there is no pending test, the pending test submission number is 0.<br>
 * Note: This response is specific for marathon rounds.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: SubmissionHistoryResponse.java 72343 2008-08-15 06:09:22Z qliu $
 */
public class SubmissionHistoryResponse extends BaseResponse {
    /** Represents the ID of the marathon round. */
    private int roundId;

    /** Represents the ID of the marathon problem component. */
    private int componentId;

    /** Represents the handle of the user whose history is in the response. */
    private String handle;

    /** Represents the submission number of one test-pending submission, if any. */
    private int pendingSubmissionNumber;

    /** Represents a flag indicating if the history is about example submissions. */
    private boolean exampleHistory;

    /** Represents the submission numbers. */
    private int[] numbers;

    /** Represents the submission time of submissions. */
    private long[] times;

    /** Represents the IDs of programming languages of submissions. */
    private int[] langs;

    /** Represents the scores of submissions. */
    private double[] scores;

    /**
     * Creates a new instance of <code>SubmissionHistoryResponse</code>. The arrays are not copied. The time is
     * represented as the number of milliseconds since January 1, 1970, 00:00:00 GMT.
     * 
     * @param roundId the ID of the marathon round.
     * @param handle the handle of the user whose history is in the response.
     * @param componentId the ID of the marathon problem component.
     * @param exampleHistory <code>true</code> if the history is about example submissions; <code>false</code> if
     *            the history is about full submissions.
     * @param pendingSubmissionNumber the submission number of one test-pending submission, or 0 if no pending test
     *            submission.
     * @param numbers the submission numbers.
     * @param times the submission time of submissions.
     * @param langs the IDs of programming languages of submissions.
     * @param scores the scores of submissions.
     * @see java.util.Date#getTime()
     */
    public SubmissionHistoryResponse(int roundId, String handle, int componentId, boolean exampleHistory,
        int pendingSubmissionNumber, int[] numbers, long[] times, int[] langs, double[] scores) {
        this.roundId = roundId;
        this.handle = handle;
        this.componentId = componentId;
        this.exampleHistory = exampleHistory;
        this.pendingSubmissionNumber = pendingSubmissionNumber;
        this.numbers = numbers;
        this.times = times;
        this.langs = langs;
        this.scores = scores;
    }

    /**
     * Creates a new instance of <code>SubmissionHistoryResponse</code>. It is required by custom serialization.
     */
    public SubmissionHistoryResponse() {
    }

    public void customReadObject(CSReader reader) throws IOException {
        super.customReadObject(reader);
        this.roundId = reader.readInt();
        this.handle = reader.readString();
        this.componentId = reader.readInt();
        this.exampleHistory = reader.readBoolean();
        this.pendingSubmissionNumber = reader.readInt();
        this.numbers = (int[]) reader.readObject();
        this.times = (long[]) reader.readObject();
        this.langs = (int[]) reader.readObject();
        this.scores = (double[]) reader.readObject();
    }

    public void customWriteObject(CSWriter writer) throws IOException {
        super.customWriteObject(writer);
        writer.writeInt(roundId);
        writer.writeString(handle);
        writer.writeInt(componentId);
        writer.writeBoolean(exampleHistory);
        writer.writeInt(pendingSubmissionNumber);
        writer.writeObject(numbers);
        writer.writeObject(times);
        writer.writeObject(langs);
        writer.writeObject(scores);
    }

    /**
     * Gets a flag indicating if one submission at the given index has any pending tests.
     * 
     * @param index the index of the submission.
     * @return <code>true</code> if the submission has pending tests; <code>false</code> otherwise.
     */
    public boolean hasPendingTest(int index) {
        return numbers[index] == pendingSubmissionNumber;
    }

    /**
     * Gets the submission number of one submission at the given index.
     * 
     * @param index the index of the submission.
     * @return the submission number.
     */
    public int getNumber(int index) {
        return numbers[index];
    }

    /**
     * Gets the submission time of one submission at the given index. The time is represented as the number of
     * milliseconds since January 1, 1970, 00:00:00 GMT.
     * 
     * @param index the index of the submission.
     * @return the submission time.
     */
    public long getTime(int index) {
        return times[index];
    }

    /**
     * Gets the ID of the programming language of one submission at the given index.
     * 
     * @param index the index of the submission.
     * @return the programming language ID.
     */
    public int getLanguageId(int index) {
        return langs[index];
    }

    /**
     * Gets the score of one submission at the given index.
     * 
     * @param index the index of the submission.
     * @return the score of the submission.
     */
    public double getScore(int index) {
        return scores[index];
    }

    /**
     * Gets the number of submissions in the history.
     * 
     * @return the number of submissions.
     */
    public int getCount() {
        return numbers == null ? 0 : numbers.length;
    }

    public String toString() {
        return "SubmissionHistoryResponse[count=" + getCount() + " ]";
    }

    /**
     * Gets a flag indicating if the history is about example submissions.
     * 
     * @return <code>true</code> if the history is about example submissions; <code>false</code> if the history is
     *         about full submissions.
     */
    public boolean isExampleHistory() {
        return exampleHistory;
    }

    /**
     * Gets the IDs of programming languages of submissions. There is no copy.
     * 
     * @return the programming language IDs.
     */
    public int[] getLangs() {
        return langs;
    }

    /**
     * Gets the submission numbers. There is no copy.
     * 
     * @return the submission numbers.
     */
    public int[] getNumbers() {
        return numbers;
    }

    /**
     * Gets the scores of submissions. There is no copy.
     * 
     * @return the scores of submissions.
     */
    public double[] getScores() {
        return scores;
    }

    /**
     * Gets the submission time of submissions. There is no copy. The time is represented as the number of milliseconds
     * since January 1, 1970, 00:00:00 GMT.
     * 
     * @return the submission time of submissions.
     */
    public long[] getTimes() {
        return times;
    }

    /**
     * Gets the ID of the marathon problem component.
     * 
     * @return the ID of the marathon problem component.
     */
    public int getComponentId() {
        return componentId;
    }

    /**
     * Gets the handle of the user whose history is in the response.
     * 
     * @return the handle of the user.
     */
    public String getHandle() {
        return handle;
    }

    /**
     * Gets the ID of the marathon round.
     * 
     * @return the ID of the marathon round.
     */
    public int getRoundId() {
        return roundId;
    }
}
