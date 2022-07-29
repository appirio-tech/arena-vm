/**
 * PointValueChangeEvent.java
 *
 * Description:		Contains information about a point value change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class PointValueChangeEvent extends java.util.EventObject {

    /** The row that Change */
    private int row;

    /** The column that Change */
    private int col;

    /** The new pointvalue */
    private int newValue;

    /**
     *  Constructor of a Event
     *
     *  @param source   the source of the event
     *  @param row      the row that Change
     *  @param col      the column that Change
     *  @param newValue the new value
     */
    public PointValueChangeEvent(Object source, int row, int col, int newValue) {
        super(source);
        this.row = row;
        this.col = col;
        this.newValue = newValue;
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
     * Returns the new point value
     * @returns the point value
     */
    public int getNewValue() {
        return newValue;
    }

    /**
     * Returns the string representation of this event
     *
     * @returns the string representation of this event
     */
    public String toString() {
        return new StringBuffer().append("(PointValueChangeEvent)[").append(row).append(", ").append(col).append(", ").append(newValue).append("]").toString();
    }
}


/* @(#)PointValueChangeEvent.java */
