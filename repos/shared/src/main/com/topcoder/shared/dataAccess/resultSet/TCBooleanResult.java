package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class stores a <tt>Boolean</tt>.  Instances of this class are
 * created by a <tt>ResultSetContainer</tt> object when it stores
 * results coming back from the database.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 * @see     TCResultItem
 * @see     ResultSetContainer
 */

public class TCBooleanResult extends TCResultItem {
    private Boolean value;

    /**
     * Contructs a <tt>TCBooleanResult</tt> object from the specified
     * boolean input.
     *
     * @param   value The boolean value to be stored.
     */
    public TCBooleanResult(boolean value) {
        this.value = new Boolean(value);
    }

    /**
     * Contructs a <tt>TCBooleanResult</tt> object from the specified
     * Boolean input.
     *
     * @param   value The Boolean value to be stored.
     */
    public TCBooleanResult(Boolean value) {
        this.value = value;
    }

    /**
     * Compares this object with another object.  If the other object
     * is also a <tt>TCBooleanResult</tt> then their embedded
     * <tt>Boolean</tt> objects will be compared directly.  In the
     * comparison, false is considered to be less than true, and a
     * null object is considered less than false.  <p>

     * If the other object is of different type, a case-insensitive comparison
     * of the objects' <tt>toString()</tt> results will be applied.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public int compareTo(Object other) {
        if (other instanceof TCBooleanResult) {
            Boolean otherBoolean = ((Boolean) ((TCBooleanResult) other).getResultData());
            if (value == null && otherBoolean == null)
                return 0;
            if (value == null)
                return -1;
            if (otherBoolean == null)
                return 1;
            if (value.booleanValue() && !otherBoolean.booleanValue())
                return 1;
            else if (!value.booleanValue() && otherBoolean.booleanValue())
                return -1;
            return 0;
        } else
            return this.toString().compareToIgnoreCase(other.toString());
    }

    /**
     * Returns the type of the embedded object, as defined in <tt>TCResultItem</tt>
     *
     * @return  The embedded object type
     */
    public int getType() {
        return TCResultItem.BOOLEAN;
    }

    /**
     * Returns the string representation of the embedded <tt>Boolean</tt> object
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
     * Returns the embedded <tt>Boolean</tt> object
     *
     * @return  The embedded object
     */
    public Object getResultData() {
        return value;
    }
}

