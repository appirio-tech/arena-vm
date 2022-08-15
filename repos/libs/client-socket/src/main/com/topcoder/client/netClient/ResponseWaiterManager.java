/*
 * ResponseWaiterManager
 * 
 * Created 03/23/2006
 */
package com.topcoder.client.netClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The ResponseWaiterManager is responsible for managing all the waiter objects 
 * registered on it.  
 * It provides helper methods to deal with concurrency issues  
 * eg. another thread already unregister the waiter for a request.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ResponseWaiterManager {
    private Map waiters = new HashMap(101, (float) 0.70);
    private int lastRequestIdStarted = -1;
    private long startTimeout;
    private long endTimeout;
    private long inactivityTimeout;
    
    
    /**
     * Creates a new ResponseWaiterManager with the default timeout values.
     * Default startTimeout is the value defined in the system property with 
     * key <code>ResponseToSyncRequestWaiter.START_KEY</code> or if the property
     * is not defined the constant value <code>ContestConstants.RESPONSE_START_TIMEOUT_MILLIS</code> 
     * Default endTimeout is the value defined in the system property with
     * key <code>ResponseToSyncRequestWaiter.END_KEY</code> or if the property
     * is not defined the constant value <code>ContestConstants.RESPONSE_END_TIMEOUT_MILLIS</code>
     * Default inactivityTimeout is the value defined in the system property with 
     * key <code>ResponseToSyncRequestWaiter.INACTIVITY_KEY</code> or if the property
     * is not defined the constant value <code>ContestConstants.RESPONSE_INACTIVITY_TIMEOUT_MILLIS</code>  
     */
    public ResponseWaiterManager() {
        this(ResponseToSyncRequestWaiter.getStartTimeoutDefault(), 
                ResponseToSyncRequestWaiter.getEndTimeoutDefault(), 
                ResponseToSyncRequestWaiter.getInactivityTimeoutDefault());
    }
    
   /**
    * Creates a new ResponseWaiterManager with the specified timeout values.
    * 
    * @param startTimeout Time to wait (in ms) for a start-response or end-response notification
    * @param endTimeout Time to wait (in ms) for an end-response notification after a 
    *                      start-response notification has been received 
    * @param inactivityTimeout Time to wait (in ms) for bytes received while waiting for a response-end
    */
    public ResponseWaiterManager(long startTimeout, long endTimeout, long inactivityTimeout) {
        this.startTimeout = startTimeout;
        this.endTimeout = endTimeout;
        this.inactivityTimeout = inactivityTimeout;
    }
   
    /**
     * Creates a new ResponseToSyncRequestWaiter object and registers it
     * for the request with id <code>requestId</code>.
     * The <code>unregisterWaiterFor</code> method must be called to unregister this object 
     * after from this ResponseWaiterManager.
     *  
     * @param requestId id of the request the waiter is created for.
     * @return a new ResponseToSyncRequestWaiter
     * @see ResponseToSyncRequestWaiter
     */
    public ResponseToSyncRequestWaiter registerWaiterFor(int requestId) {
        return registerWaiterFor(requestId, startTimeout, endTimeout, inactivityTimeout);
    }
    
    /**
     * Creates a new ResponseToSyncRequestWaiter object and registers it
     * for the request with id <code>requestId</code> using the given timeout values.<p>
     * 
     * The <code>unregisterWaiterFor</code> method must be called to unregister this object 
     * after from this ResponseWaiterManager.
     *  
     * @param requestId id of the request the waiter is created for.
     * @param startTimeout Time to wait (in ms) for a start-response or end-response notification
     * @param endTimeout Time to wait (in ms) for an end-response notification after a 
     *                      start-response notification has been received 
     * @param inactivityTimeout Time to wait (in ms) for bytes received while waiting for a response-end
     * @return a new ResponseToSyncRequestWaiter
     * @see ResponseToSyncRequestWaiter
     */
    public ResponseToSyncRequestWaiter registerWaiterFor(int requestId, long startTimeout, long endTimeout, long inactivityTimeout) {
        ResponseToSyncRequestWaiter w;
        synchronized (waiters) {
            w = new ResponseToSyncRequestWaiter(startTimeout, endTimeout, inactivityTimeout);
            this.waiters.put(new Integer(requestId), w);
        }
        return w;
    }
    
    
    /**
     * Unblocks all registered waiters of this ResponseWaiterManager and
     * unregisters all of them.
     */
    public void unblockAll() {
        synchronized (waiters) {
            for (Iterator it = waiters.values().iterator(); it.hasNext();) {
                ResponseToSyncRequestWaiter waiter =  (ResponseToSyncRequestWaiter) it.next();
                waiter.unblock();
            }
            waiters.clear();
        }
    }
    
    
    /**
     * Timeout on all registered waiters of this ResponseWaiterManager and
     * unregisters all of them.
     */
    public void timeOutAll() {
        synchronized (waiters) {
            for (Iterator it = waiters.values().iterator(); it.hasNext();) {
                ResponseToSyncRequestWaiter waiter =  (ResponseToSyncRequestWaiter) it.next();
                waiter.timeOut();
            }
            waiters.clear();
        }
    }
    /**
     * Invokes the method <code>startOfResponse</code> of the waiter 
     * registered for request with id requestId.
     * If no waiter has been registered for the requestId, or if it has been previously 
     * unregistered, ignores the call.
     * 
     * @param requestId Id of the request the waiter is registered for.
     */
    public void startOfSyncResponse(int requestId) {
        ResponseToSyncRequestWaiter waiter = getWaiter(requestId);
        if (waiter != null) {
            lastRequestIdStarted = requestId;
            waiter.startOfResponse();
        }
    }

    /**
     * Invokes the method <code>endOfResponse</code> of the waiter 
     * registered for request with id requestId.
     * If no waiter has been registered for the requestId, or if it has been previously 
     * unregistered, ignores the call.
     * 
     * @param requestId Id of the request the waiter is registered for.
     * @return true if the waiter was registered
     */
    public boolean endOfSyncResponse(int requestId) {
        ResponseToSyncRequestWaiter waiter = getWaiter(requestId);
        if (waiter != null) {
            waiter.endOfResponse();
            return true;
        }
        return false;
    }
    
    /**
     * Invokes the method <code>dataRead</code> of the waiter 
     * who received lastest start-response notification.
     */
    public void dataRead() {
        ResponseToSyncRequestWaiter waiter = getWaiter(lastRequestIdStarted);
        if (waiter != null) {
            waiter.dataRead();
        }
    }

    /**
     * Invokes the method <code>unblock</code> of the waiter 
     * registered for request with id requestId.
     * If no waiter has been registered for the requestId, or if it has been previously 
     * unregistered, ignores the call.
     *  
     * @param requestId Id of the request the waiter is registered for.
     */
    public void unblock(int requestId) {
        ResponseToSyncRequestWaiter waiter = getWaiter(requestId);
        if (waiter != null) {
            waiter.unblock();
        }
    }
    
    /**
     * Unregisters the waiter registered for request with id requestId.
     * 
     * @param requestId Id of the request the waiter is registered for.
     */
    public void unregisterWaiterFor(int requestId) {
        removeWaiter(requestId);
    }

    /**
     * Returns the number of registered waiters
     * 
     * @return the number of registered waiterss
     */
    public int size() {
        synchronized (waiters) {
            return waiters.size();
        }
        
    }
    /**
     * Gets the waiter registered for the request with id <code>requestId</code>
     * This method synchronizes on <code>waiters</code>
     */
    private ResponseToSyncRequestWaiter getWaiter(int requestId) {
        ResponseToSyncRequestWaiter waiter;
        synchronized (waiters) {
            waiter = (ResponseToSyncRequestWaiter) waiters.get(new Integer(requestId));
        }
        return waiter;
    }

    /**
     * Removes from the waiters map the waiter registered for the requestId
     * This method synchronizes on <code>waiters</code>
     */
    private ResponseToSyncRequestWaiter removeWaiter(int requestId) {
        ResponseToSyncRequestWaiter waiter;
        synchronized (waiters) {
            waiter = (ResponseToSyncRequestWaiter) waiters.remove(new Integer(requestId));
        }
        return waiter;
    }
}
