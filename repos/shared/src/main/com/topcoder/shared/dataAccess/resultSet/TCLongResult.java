package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class stores a <tt>Long</tt>.  Instances of this class are
 * created by a <tt>ResultSetContainer</tt> object when it stores
 * results coming back from the database.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 * @see     TCResultItem
 * @see     ResultSetContainer
 */
public class TCLongResult extends TCResultItem {
    private Long value;

    /**
     * Contructs a <tt>TCLongResult</tt> object from the specified
     * long input.
     *
     * @param   value The long value to be stored.
     */
    public TCLongResult(long value) {
        this.value = new Long(value);
    }

    /**
     * Contructs a <tt>TCLongResult</tt> object from the specified
     * Long input.
     *
     * @param   value The Long value to be stored.
     */
    public TCLongResult(Long value) {
        this.value = value;
    }

    /**
     * Compares this object with another object.  If the other object
     * is also a <tt>TCLongResult</tt> then their embedded
     * <tt>Long</tt> objects will be compared directly, with a null object
     * considered less than any non-null object. <p>
     *
     * If the other object is of different type, a case-insensitive comparison
     * of the objects' <tt>toString()</tt> results will be applied.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public int compareTo(Object other) {
        if (other instanceof TCLongResult) {
            Long otherLong = ((Long) ((TCLongResult) other).getResultData());
            if (value == null && otherLong == null)
                return 0;
            if (value == null)
                return -1;
            if (otherLong == null)
                return 1;
            return value.compareTo(otherLong);
        } else
            return this.toString().compareToIgnoreCase(other.toString());
    }

    /**
     * Returns the type of the embedded object, as defined in <tt>TCResultItem</tt>
     *
     * @return  The embedded object type
     */
    public int getType() {
        return TCResultItem.LONG;
    }

    /**
     * Returns the string representation of the embedded <tt>Long</tt> object.
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
     * Returns the embedded <tt>Long</tt> object
     *
     * @return  The embedded object
     */
    public Object getResultData() {
        return value;
    }
}

