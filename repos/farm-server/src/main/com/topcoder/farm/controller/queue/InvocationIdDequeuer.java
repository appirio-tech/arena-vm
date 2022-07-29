/*
 * InvocationIdDequeuer
 * 
 * Created 08/01/2006
 */
package com.topcoder.farm.controller.queue;

/**
 * Interface that allows to dequeue invocation ids from the queue manager.
 * 
 * The QueueManager provides instances of this interface to allow
 * invocation assignation
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface InvocationIdDequeuer {
    
    /**
     * Returns the next invocation id 
     * This method blocks the calling thread until an item is available on the queue  
     * 
     * @return The id of the invocation removed from queue
     * 
     * @throws InterruptedException If the thread is interrupted while waiting for
     *                              an item become available
     */
    public Long dequeueInvocationId() throws InterruptedException;
}
