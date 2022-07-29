/*
 * InvocationResponse
 * 
 * Created Jul 26, 2007
 */
package com.topcoder.server.ejb.asyncservices;

import java.io.Serializable;

/**
 * The response for an async invocation
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: InvocationResponse.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class InvocationResponse implements Serializable {
    /**
     * The response contains the returned value.
     */
    public static final int TYPE_RETURN_VALUE = 0;
    
    /**
     * The target service thrown an exception, the result is the exception
     */
    public static final int TYPE_TARGET_EXCEPTION = 1;
    
    /**
     * The service invocation is completed, result is null. No exception was thrown
     */
    public static final int TYPE_ACK = 2;
    
    /**
     * The target service definition included in the request was wrong. Or the service could not be found, 
     * or the method signature was not found, or the argument where invalid
     */
    public static final int TYPE_SEVICE_DEFINITION_ERROR = 3;
    
    /**
     * The async service failed to process the invocation. result may contain and exception. 
     * Failure is due to the async service itself.
     */
    public static final int TYPE_ASYNC_SERVICE_EXCEPTION = -1;
    
    private int resultType;
    private Object result;
    
    public InvocationResponse() {
    }
    
    public InvocationResponse(int resultType, Object result) {
        this.resultType = resultType;
        this.result = result;
    }

    public Object getResult() {
        return result;
    }

    public int getResultType() {
        return resultType;
    }

}