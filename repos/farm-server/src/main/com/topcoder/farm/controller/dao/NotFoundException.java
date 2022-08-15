/*
 * NotFoundException
 * 
 * Created 08/30/2006
 */
package com.topcoder.farm.controller.dao;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class NotFoundException extends DAOException {

    public NotFoundException() {
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
