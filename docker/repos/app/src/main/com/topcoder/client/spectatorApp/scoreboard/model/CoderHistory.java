/**
 * CoderHistory.java
 *
 * Description:		Represents information about a coder history event
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class CoderHistory {

    /** History type of OPEN */
    public static final int OPENED = 1;

    /** History type of CHALLENGED */
    public static final int CHALLENGED = 2;

    /** The type of history */
    private int type;

    /** The problem affected */
    private int col;

    /** The coder associated with the history */
    private String coderHandle;

    /** The points in relation to a challenge */
    private int points;

    /**
     *  Constructor of a Event
     *
     *  @param type        the type of history event
     *  @param col         the column that Change
     *  @param coderHandle the handle of the coder
     *  @param points      the points assigned to the event
     */
    public CoderHistory(int type, int col, String coderHandle, int points) {
        this.type = type;
        this.col = col;
        this.coderHandle = coderHandle;
        this.points = points;
    }

    /**
     * Returns the type of historical event
     * @returns the type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the column representing the problem
     * @returns the column
     */
    public int getColumn() {
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
     * Returns the points associated with the event
     * @returns the points
     */
    public int getPoints() {
        return points;
    }
}


/* @(#)CoderHistory.java */
