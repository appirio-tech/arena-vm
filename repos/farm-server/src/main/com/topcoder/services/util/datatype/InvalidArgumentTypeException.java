/*
 * InvalidArgumentTypeException
 * 
 * Created 12/28/2006
 */
package com.topcoder.services.util.datatype;

/**
 * @author Diego Belfer (mural)
 * @version $Id: InvalidArgumentTypeException.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class InvalidArgumentTypeException extends Exception {

    public InvalidArgumentTypeException() {
        super();
    }

    public InvalidArgumentTypeException(String message) {
        super(message);
    }

}
