/*
 * ExceptionInvocationResponse
 * 
 * Created 07/18/2006
 */
package com.topcoder.farm.shared.net.connection.remoting;

/**
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ExceptionInvocationResponse extends InvocationResponseMessage {

    public ExceptionInvocationResponse() {
    }
    
    public ExceptionInvocationResponse(int id, Exception ex) {
        super(id, ex);
    }
    
    public Exception getException() {
        return (Exception) getResponseObject();
    }
}
