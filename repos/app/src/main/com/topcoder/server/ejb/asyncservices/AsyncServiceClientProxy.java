/*
 * AsyncServiceClientProxy
 * 
 * Created 07/30/2007
 */
package com.topcoder.server.ejb.asyncservices;


/**
 * This interface is implemented by the proxy object returned by {@link AsyncServiceProxyGenerator} class.
 * 
 * It allows to change proxy properties between service invocations.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: AsyncServiceClientProxy.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public interface AsyncServiceClientProxy {
    
    /**
     * Set the time to live for the invocations. After this time elapsed, a timeout will be notified to the handler.
     *  
     * @param timeToLive The time to live in ms. -1 for ever, never timeouts
     */
    void setTimeToLive(long timeToLive);
    /**
     * Set the response id used to notify results/failures for invocations.<p>
     * This value can be set as null. All service invocations made will be notified 
     * using the id set at the moment the invocation was made.<p>
     * 
     * @param responseId The id to use for invocation.
     */
    void setResponseId(Object responseId);
    
    /**
     * Set the response handler used to notify results/failures for invocations.<p>
     * This value can be set as null. All service invocations made will be notified 
     * using the the handler set at the moment the invocation was made.<p>
     * 
     * @param handler the Handler to set
     */
    void setHandler(AsyncServiceClientInvoker.AsyncResponseHandler handler);
}   
