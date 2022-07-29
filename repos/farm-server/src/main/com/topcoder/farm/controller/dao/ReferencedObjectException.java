/*
 * ReferencedObjectException
 * 
 * Created 19/09/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReferencedObjectException extends DAOException {

    public ReferencedObjectException() {
        super();
    }

    public ReferencedObjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public ReferencedObjectException(String message) {
        super(message);
    }

    public ReferencedObjectException(Throwable cause) {
        super(cause);
    }

}
