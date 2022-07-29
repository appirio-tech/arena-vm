/**
 * CoderMoveEvent.java
 *
 * Description:		Contains information about a coder moving from one problem to the next
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class CoderMoveEvent extends java.util.EventObject {

    /** The row that Change */
    private int row;

    /** The column that Change */
    private int col;

    /** The coder that moved */
    private String coderHandle;

    /**
     *  Constructor of a Event
     *
     *  @param source      the source of the event
     *  @param row         the row that Change
     *  @param col         the column that Change
     *  @param coderHandle the handle of the coder
     */
    public CoderMoveEvent(Object source, int row, int col, String coderHandle) {
        super(source);
        this.row = row;
        this.col = col;
        this.coderHandle = coderHandle;
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
     * Returns the coder handle
     * @returns the coder handle
     */
    public String getCoderHandle() {
        return coderHandle;
    }

    /**
     * Returns the string representation of this event
     *
     * @returns the string representation of this event
     */
    public String toString() {
        return new StringBuffer().append("(CoderMoveEvent)[").append(row).append(", ").append(col).append(", ").append(coderHandle).append("]").toString();
    }
}


/* @(#)CoderMoveEvent.java */
