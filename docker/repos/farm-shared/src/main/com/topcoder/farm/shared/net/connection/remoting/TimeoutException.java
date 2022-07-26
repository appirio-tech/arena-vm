/*
 * TimeoutException
 * 
 * Created 07/19/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class TimeoutException extends RemotingException {

    public TimeoutException() {
        super();
    }

    public TimeoutException(String message) {
        super(message);
    }

}
