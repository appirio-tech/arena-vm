/*
 * Queue
 * 
 * Created 07/21/2006
 */
package com.topcoder.farm.shared.util.queue;

/**
 * Represents a Queue.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public interface Queue {

    /**
     * Puts the element at the end of the queue
     * @param o Object to add to the queue
     * 
     * @return true if the object could be added to the queue
     */
    boolean offer(Object o);

    /**
     * Retrieves and remove the first element of this queue, waits up to 
     * the specified wait time if no elements are available on the queue  
     * 
     * @param wait Time to wait for an element to become available
     * @return the first element on the queue or null if after the wait time
     *          no element is still available
     * 
     * @throws InterruptedException If the thread is interrupted while waiting
     */
    Object poll(long wait) throws InterruptedException;
    
    /**
     * @return The number of elements on the queue
     */
    int size();
    
    /**
     * Removes all elements from the queue
     */
    void clear();
}
