/*
 * RemotingException
 * 
 * Created 07/19/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RemotingException extends RuntimeException {

    public RemotingException() {
        super();
    }

    public RemotingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(Throwable cause) {
        super(cause);
    }
}
