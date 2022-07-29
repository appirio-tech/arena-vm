/*
 * InvalidArgumentValueException
 * 
 * Created 12/28/2006
 */
package com.topcoder.services.util.datatype;

/**
 * @author Diego Belfer (mural)
 * @version $Id: InvalidArgumentValueException.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class InvalidArgumentValueException extends Exception {

    public InvalidArgumentValueException() {
        super();
    }

    public InvalidArgumentValueException(String message) {
        super(message);
    }

}
