/**
 * Location.java
 *
 * Description:		Generic class that hold row/col
 * @author			Tim "Pops" Roberts
 * @version			1.0
 */

package com.topcoder.client.spectatorApp.scoreboard.model;

public class Location {

    /** The row */
    private int row;

    /** The column */
    private int col;

    /**
     *  Constructor of a location
     *
     *  @param row                the row that Change
     *  @param col                the column that Change
     */
    public Location(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * Returns the row
     * @returns the row
     */
    public int getRow() {
        return row;
    }

    /**
     * Returns the column
     * @returns the column
     */
    public int getCol() {
        return col;
    }

}


/* @(#)Location.java */
