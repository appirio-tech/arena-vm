package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class stores a <tt>String</tt>.  Instances of this class are
 * created by a <tt>ResultSetContainer</tt> object when it stores
 * results coming back from the database.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 * @see     TCResultItem
 * @see     ResultSetContainer
 */
public class TCStringResult extends TCResultItem {
    private String value;

    /**
     * Contructs a <tt>TCStringResult</tt> object from the specified
     * String input.
     *
     * @param   value The <tt>String</tt> value to be stored.
     */
    public TCStringResult(String value) {
        this.value = value;
    }

    /**
     * Compares this object with another object.  If the other object
     * is also a <tt>TCStringResult</tt> then their embedded
     * <tt>String</tt> objects will be compared directly via a
     * case-insensitive comparison, with a null String object always
     * considered less than a non-null object, and two null String objects
     * considered equal.  <p>
     *
     * If the other obejct is not a TCStringResult, a case-insensitive
     * comparison of the objects' <tt>toString()</tt> results is used to
     * determine the result.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public int compareTo(Object other) {
        if (other instanceof TCStringResult) {
            String otherStr = ((String) ((TCStringResult) other).getResultData());
            if (value == null && otherStr == null)
                return 0;
            if (value == null)
                return -1;
            if (otherStr == null)
                return 1;
        }
        return this.toString().compareToIgnoreCase(other.toString());
    }

    /**
     * Returns the type of the embedded object, as defined in <tt>TCResultItem</tt>
     *
     * @return  The embedded object type
     */
    public int getType() {
        return TCResultItem.STRING;
    }

    /**
     * Returns the string representation of the embedded <tt>String</tt> object.
     * If it is null, this method returns an empty string.
     *
     * @return  The embedded object's string representation
     */
    public String toString() {
        if (value == null)
            return "";
        return value;
    }

    /**
     * Returns the embedded <tt>String</tt> object
     *
     * @return  The embedded object
     */
    public Object getResultData() {
        return value;
    }
}

