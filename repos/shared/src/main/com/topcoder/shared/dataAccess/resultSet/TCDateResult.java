package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class stores a <tt>java.sql.Date</tt>.  Instances of this class are
 * created by a <tt>ResultSetContainer</tt> object when it stores
 * results coming back from the database.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 * @see     TCResultItem
 * @see     ResultSetContainer
 */

public class TCDateResult extends TCResultItem {
    private java.sql.Date value;

    /**
     * Contructs a <tt>TCDateResult</tt> object from the specified
     * <tt>java.sql.Date</tt> input.
     *
     * @param   value The <tt>java.sql.Date</tt> to be stored.
     */
    public TCDateResult(java.sql.Date value) {
        this.value = value;
    }

    /**
     * Compares this object with another object.  If the other object
     * is also a <tt>TCDateResult</tt> then their embedded
     * <tt>java.sql.Date</tt> objects will be compared directly, with
     * a null object considered less than any non-null object.  Otherwise,
     * a case-insensitive comparison of the objects' <tt>toString()</tt>
     * results will be applied.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public int compareTo(Object other) {
        if (other instanceof TCDateResult) {
            java.sql.Date otherDate = ((java.sql.Date) ((TCDateResult) other).getResultData());
            if (value == null && otherDate == null)
                return 0;
            if (value == null)
                return -1;
            if (otherDate == null)
                return 1;
            return value.compareTo(otherDate);
        } else
            return this.toString().compareToIgnoreCase(other.toString());
    }

    /**
     * Returns the type of the embedded object, as defined in <tt>TCResultItem</tt>
     *
     * @return  The embedded object type
     */
    public int getType() {
        return TCResultItem.DATE;
    }

    /**
     * Returns the string representation of the embedded <tt>java.sql.Date</tt> object.
     * The string format is yyyy-mm-dd as per <tt>java.sql.Date.toString()</tt>.  If
     * the embedded object is null, this method returns an empty string.
     *
     * @return  The embedded object's string representation
     */
    public String toString() {
        if (value == null)
            return "";
        return value.toString();
    }

    /**
     * Returns the embedded <tt>java.sql.Date</tt> object
     *
     * @return  The embedded object
     */
    public Object getResultData() {
        return value;
    }
}

