/*
 * ServiceDefinitionException
 * 
 * Created 07/28/2007
 */
package com.topcoder.server.ejb.asyncservices;

/**
 * This exception is thrown by the Async Service when the service bean
 * , the method, or the arguments used during the invocation are not valid.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: ServiceDefinitionException.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class ServiceDefinitionException extends Exception {

    public ServiceDefinitionException() {
    }

    public ServiceDefinitionException(String message) {
        super(message);
    }

    public ServiceDefinitionException(Throwable cause) {
        super(cause);
    }

    public ServiceDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
