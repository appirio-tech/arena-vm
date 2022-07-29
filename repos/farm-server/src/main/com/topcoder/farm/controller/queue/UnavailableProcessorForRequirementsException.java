/*
 * UnavailableProcessorForRequirementsException
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

/**
 * Exception thrown when an invocation is tried to be enqueued in the Queuemanager
 * but neither queues created for the actives processor match the invocation's requirements 
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class UnavailableProcessorForRequirementsException extends Exception {
    public UnavailableProcessorForRequirementsException() {
    }
    
    public UnavailableProcessorForRequirementsException(String message) {
        super(message);
    }
}
