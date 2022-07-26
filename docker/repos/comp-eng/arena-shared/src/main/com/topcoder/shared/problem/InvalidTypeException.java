package com.topcoder.shared.problem;

/**
 * Defines an error which is thrown when the data type cannot be found in the factory.
 * 
 * @author Qi Liu
 * @version $Id: InvalidTypeException.java 71757 2008-07-17 09:13:19Z qliu $
 */
public class InvalidTypeException extends Exception {
    /**
     * Creates a new instance of <code>InvalidTypeException</code>. The error message is given.
     * 
     * @param message the error message.
     */
    public InvalidTypeException(String message) {
        super(message);
    }
}
