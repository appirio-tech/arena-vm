package com.topcoder.shared.dataAccess.resultSet;

/**
 * This class serves as the ancestor class for a number of different wrapper
 * objects storing various database result types.
 *
 * @author  Dave Pecora
 * @version 1.00, 02/11/2002
 */

public abstract class TCResultItem implements Comparable, java.io.Serializable {
    /**
     *
     */
    public static final int INT = 1;
    /**
     *
     */
    public static final int LONG = 2;
    /**
     *
     */
    public static final int BIGINTEGER = 3;
    /**
     *
     */
    public static final int FLOAT = 4;
    /**
     *
     */
    public static final int DOUBLE = 5;
    /**
     *
     */
    public static final int BIGDECIMAL = 6;
    /**
     *
     */
    public static final int BOOLEAN = 7;
    /**
     *
     */
    public static final int STRING = 8;
    /**
     *
     */
    public static final int DATE = 9;
    /**
     *
     */
    public static final int TIME = 10;
    /**
     *
     */
    public static final int DATETIME = 11;

    /**
     * Compares this object with another object.
     *
     * @param   other The object against which this will be compared.
     * @return  -1, 0, or 1 depending on whether this object is less than,
     * equal to, or greater than <tt>other</tt>, respectively.
     */
    public abstract int compareTo(Object other);

    /**
     * Returns the type of the embedded object, as defined in the <tt>TCResultItem</tt>
     * class constants.
     *
     * @return  The embedded object type
     */
    public abstract int getType();

    /**
     * Returns the string representation of the embedded object
     *
     * @return  The embedded object's string
     */
    public abstract String toString();

    /**
     * Returns the embedded object
     *
     * @return  The embedded object
     */
    public abstract Object getResultData();
}

