/*
 * RemoteInvokerException
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.messagebus.invoker;


/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RemoteInvokerException extends Exception {

    public RemoteInvokerException() {
    }

    public RemoteInvokerException(String message, Throwable cause) {
        super(message, cause);
    }

    public RemoteInvokerException(String message) {
        super(message);
    }

    public RemoteInvokerException(Throwable cause) {
        super(cause);
    }

}
