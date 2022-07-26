/**
 * StatusChangeEvent.java
 *
 * Description:		Contains information about a problem status change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class StatusChangeEvent extends java.util.EventObject {

    /** The row that Change */
    private int row;

    /** The column that Change */
    private int col;

    /** The new pointvalue */
    private int newStatus;

    /** The related coder handle */
    private String relatedCoderHandle;

    /**
     *  Constructor of a Event
     *
     *  @param source             the source of the event
     *  @param row                the row that Change
     *  @param col                the column that Change
     *  @param newStatus          the new problem status
     *  @param relatedCoderHandle the coder responsibile for the status change
     */
    public StatusChangeEvent(Object source, int row, int col, int newStatus, String relatedCoderHandle) {
        super(source);
        this.row = row;
        this.col = col;
        this.newStatus = newStatus;
        this.relatedCoderHandle = relatedCoderHandle;
    }

    /**
     * Returns the row that Change
     * @returns the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column that Change
     * @returns the column
     */
    public int getCol() {
        return col;
    }

    /**
     * Returns the new problem status
     * @returns the new problem status
     */
    public int getNewStatus() {
        return newStatus;
    }

    /**
     * Returns the coder handle that caused the status change
     * @returns the coder handle
     */
    public String getRelatedCoderHandle() {
        return relatedCoderHandle;
    }

    /**
     * Returns the string representation of this event
     *
     * @returns the string representation of this event
     */
    public String toString() {
        return new StringBuffer().append("(StatusChangeEvent)[").append(row).append(", ").append(col).append(", ").append(newStatus).append("]").toString();
    }
}


/* @(#)StatusChangeEvent.java */
