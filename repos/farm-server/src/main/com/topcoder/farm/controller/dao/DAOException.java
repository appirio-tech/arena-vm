/*
 * DAOException
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class DAOException extends Exception {

    public DAOException() {
        super();
    }

    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }

    public DAOException(String message) {
        super(message);
    }

    public DAOException(Throwable cause) {
        super(cause);
    }

}
