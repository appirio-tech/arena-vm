/**
 * TotalChangeEvent.java
 *
 * Description:		Contains information about a total change
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class TotalChangeEvent extends java.util.EventObject {

    /** The row that Change */
    private int row;

    /** The new pointvalue */
    private int newValue;

    /**
     *  Constructor of a Event
     *
     *  @param source   the source of the event
     *  @param row      the row that Change
     *  @param newValue the new value
     */
    public TotalChangeEvent(Object source, int row, int newValue) {
        super(source);
        this.row = row;
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
        return new StringBuffer().append("(TotalChangeEvent)[").append(row).append(", ").append(newValue).append("]").toString();
    }
}


/* @(#)TotalChangeEvent.java */
