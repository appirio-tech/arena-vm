/**
 * TotalChangeEvent.java
 *
 * Description:		Contains information about a total change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class SubmissionCountEvent extends java.util.EventObject {

    /** The row that Change */
    private int row;

    /** The new pointvalue */
    private int submissionCount;
    private int submissionTime;
    private int exampleCount;
    private int exampleTime;

    /**
     *  Constructor of a Event
     *
     *  @param source   the source of the event
     *  @param row      the row that Change
     *  @param newValue the new value
     */
    public SubmissionCountEvent(Object source, int row, int submissionCount, int submissionTime, int exampleCount, int exampleTime) {
        super(source);
        this.row = row;
        this.submissionCount = submissionCount;
        this.submissionTime = submissionTime;
        this.exampleCount = exampleCount;
        this.exampleTime = exampleTime;
    }

    /**
     * Returns the row that Change
     * @returns the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the new point value
     * @returns the point value
     */
    public int getSubmissionCount() {
        return submissionCount;
    }
    
    public int getSubmissionTime() {
        return submissionTime;
    }
    
    public int getExampleCount() {
        return exampleCount;
    }
    
    public int getExampleTime() {
        return exampleTime;
    }

    /**
     * Returns the string representation of this event
     *
     * @returns the string representation of this event
     */
    public String toString() {
        return new StringBuffer().append("(SubmissionCountEvent)[").append(row).append(", ").append(submissionCount).append("]").toString();
    }
}


/* @(#)TotalChangeEvent.java */
