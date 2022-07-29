/*
 * RemoteInvocationException
 * 
 * Created Oct 26, 2007
 */
package com.topcoder.shared.messagebus.invoker;

import com.topcoder.shared.i18n.Message;


/**
 * Exception received when the target destination of an invocation
 * thrown an Exception while processing the action request.
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class RemoteInvocationException extends Exception {
    private ExceptionData remoteExceptionData;
    
    public RemoteInvocationException(String message, ExceptionData remoteExceptionData) {
        super(message);
        this.remoteExceptionData = remoteExceptionData;
    }

    public Message getRemoteLocalizableMessage() {
        return remoteExceptionData.getLocalizableMessage();
    }

    public String getRemoteMessage() {
        return remoteExceptionData.getDetailMessage();
    }
    
    public String getRemoteStackTrace() {
        return remoteExceptionData.getStackTraceDump();
    }

    public String getRemoteExceptionClassName() {
        return remoteExceptionData.getExceptionClassName();
    }
    
    public String toString() {
        return super.toString() + "[" +
        		"remoteExceptionClass="+getRemoteExceptionClassName() + ", " +
        		"message="+getRemoteMessage()+", " +
        		"localizableMessage="+getRemoteLocalizableMessage()+ ", " +
        		"remoteStackTrace="+getRemoteStackTrace() + "]";
    }
}
