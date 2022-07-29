package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class stores an <tt>Integer</tt>.  Instances of this class are
 * created by a <tt>ResultSetContainer</tt> object when it stores
 * results coming back from the database.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 * @see     TCResultItem
 * @see     ResultSetContainer
 */
public class TCIntResult extends TCResultItem {
    private Integer value;

    /**
     * Contructs a <tt>TCIntResult</tt> object from the specified
     * integer input.
     *
     * @param   value The int value to be stored.
     */
    public TCIntResult(int value) {
        this.value = new Integer(value);
    }

    /**
     * Contructs a <tt>TCIntResult</tt> object from the specified
     * Integer input.
     *
     * @param   value The Integer value to be stored.
     */
    public TCIntResult(Integer value) {
        this.value = value;
    }

    /**
     * Compares this object with another object.  If the other object
     * is also a <tt>TCIntResult</tt> then their embedded
     * <tt>Integer</tt> objects will be compared directly, with a null
     * object considered less than any non-null object.  If the other
     * object is of different type, a case-insensitive comparison
     * of the objects' <tt>toString()</tt> results will be applied.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public int compareTo(Object other) {
        if (other instanceof TCIntResult) {
            Integer otherInt = ((Integer) ((TCIntResult) other).getResultData());
            if (value == null && otherInt == null)
                return 0;
            if (value == null)
                return -1;
            if (otherInt == null)
                return 1;
            return value.compareTo(otherInt);
        } else
            return this.toString().compareToIgnoreCase(other.toString());
    }

    /**
     * Returns the type of the embedded object, as defined in <tt>TCResultItem</tt>
     *
     * @return  The embedded object type
     */
    public int getType() {
        return TCResultItem.INT;
    }

    /**
     * Returns the string representation of the embedded <tt>Integer</tt> object.
     * If it is null, this method returns an empty string.
     *
     * @return  The embedded object's string representation
     */
    public String toString() {
        if (value == null)
            return "";
        return value.toString();
    }

    /**
     * Returns the embedded <tt>Integer</tt> object
     *
     * @return  The embedded object
     */
    public Object getResultData() {
        return value;
    }
}

