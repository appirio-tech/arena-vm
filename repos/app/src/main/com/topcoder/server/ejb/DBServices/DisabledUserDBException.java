/*
 * DisabledUserDBException
 * 
 * Created 12/19/2006
 */
package com.topcoder.server.ejb.DBServices;

/**
 * Exception thrown by DBServices when the user is disabled
 * 
 * @author Diego Belfer (mural)
 * @version $Id: DisabledUserDBException.java 56700 2007-01-29 21:13:11Z thefaxman $
 */
public class DisabledUserDBException extends DBServicesException {

    public DisabledUserDBException() {
        super();
    }

    public DisabledUserDBException(String message) {
        super(message);
    }

}
