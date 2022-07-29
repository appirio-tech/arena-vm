package com.topcoder.client;

/**
 * Defines a sorting column, which contains the column index and an ascending/descending flag. Two sorting columns are
 * considered as the same if the indices of the columns to be sorted are the same.
 * 
 * @author Qi Liu
 * @version $Id: SortElement.java 71772 2008-07-18 07:46:22Z qliu $
 */
public final class SortElement {
    /** Represents the column index. */
    private int column;

    /** Represents the flag indicating if the column should be sorted in descending order. */
    private boolean opposite;

    /**
     * Creates a new instance of <code>SortElement</code>. The column index and the ascending/descending flag are
     * given.
     * 
     * @param column the column index.
     * @param opposite a flag indicating if the column should be sorted in descending order.
     */
    public SortElement(int column, boolean opposite) {
        this.column = column;
        this.opposite = opposite;
    }

    /**
     * Gets the column index to be sorted.
     * 
     * @return the column index.
     */
    public int getColumn() {
        return column;
    }

    /**
     * Gets a flag indicating if the column should be sorted in descending order.
     * 
     * @return <code>true</code> if the column should be sorted in descending order; <code>false</code> otherwise.
     */
    public boolean isOpposite() {
        return opposite;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SortElement)) {
            return false;
        }
        SortElement elem = (SortElement) obj;
        return column == elem.column;
    }
    
    public int hashCode() {
        return column;
    }

    public String toString() {
        return column + (opposite ? "F" : "");
    }

}
