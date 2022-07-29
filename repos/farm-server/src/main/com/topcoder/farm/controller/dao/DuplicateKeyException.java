/*
 * DuplicateKeyException
 * 
 * Created 21/09/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DuplicateKeyException extends DAOException {

    /**
     * 
     */
    public DuplicateKeyException() {
    }

    /**
     * @param message
     * @param cause
     */
    public DuplicateKeyException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public DuplicateKeyException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DuplicateKeyException(Throwable cause) {
        super(cause);
    }

}
