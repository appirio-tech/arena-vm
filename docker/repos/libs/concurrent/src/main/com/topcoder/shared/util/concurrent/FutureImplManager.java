/*
 * FutureImplManager
 * 
 * Created Oct 24, 2007
 */
package com.topcoder.shared.util.concurrent;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.topcoder.shared.util.concurrent.ManagedFutureImpl.FutureHandler;
import com.topcoder.shared.util.logging.Logger;

/**
 * FutureImplManager is responsible for tracking futures, allowing processing threads to set values 
 * to futures which are being waited for resolution by other threads.<p>
 * 
 * @param <ID> the type of the future ID.
 * @param <T> The result type returned by {@link FutureImpl} gets method.
 * 
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class FutureImplManager<ID, T> {
    private static final long MAX_GAP = 2000;
    private Logger log = Logger.getLogger(getClass());
    private Object requestResponseLock = new Object();
    private Map<ID, TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>>> pendingFutures = new HashMap<ID, TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>>>();
    private Map<ID, TimedEntry<T>> pendingResults = new LinkedHashMap<ID, TimedEntry<T>>();
    private Map<ID, TimedEntry<Exception>> pendingExceptions = new LinkedHashMap<ID, TimedEntry<Exception>>();
    private FutureHandler<ID> futureReleaseHandler;

    /**
     * Creates a new FutureImplManager.
     */
    public FutureImplManager() {
        this.futureReleaseHandler = new ManagedFutureImpl.FutureHandler<ID>() {
            public void futureReady(ID id) {
                synchronized (FutureImplManager.this.requestResponseLock) {
                    pendingFutures.remove(id);
                }
            }
            public boolean cancel(ID id, boolean mayInterrupt) {
                return false;
            }
        };
    }


    /**
     * Gets the number of unprocessed asynchronous tasks.
     *
     * @return the number of unprocessed asynchronous tasks.
     */    
    public int getPendingFuturesSize() {
        synchronized (requestResponseLock) {
            return pendingFutures.size();
        }
    }
    
    /**
     * Retrieves a set of unprocessed asynchronous task IDs. A copy of such set is returned.
     *
     * @return a set of unprocessed asynchronous task IDs.
     */
    public Set<ID> getPendingFutureIds() {
        synchronized (requestResponseLock) {
            return new HashSet<ID>(pendingFutures.keySet());
        }
    }
    
    /**
     * Retrieves an unprocessed asynchronous task according to the given ID. If such task does not exist,
     * <code>null</code> is returned.
     *
     * @param id the ID of the unprocessed asynchronous task to be retrieved.
     * @return the unprocessed asynchronous task by the given ID.
     */
    public FutureImpl<T> getFuture(ID id) {
        synchronized (requestResponseLock) {
            TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>> timedEntry = pendingFutures.get(id);
            if (timedEntry != null) {
                return timedEntry.getValue().get();
            }
            return null;
        }
    }
    
    /**
     * Sets the value for a given future Id.
     * 
     * @param id The id of the future to set
     * @param result The value to set
     * @return true if the future was found and the value was set. false otherwise.
     */
    public boolean setValue(ID id, T result) {
        if (log.isTraceEnabled()) {
            log.trace("Setting result for: " + id + " result="+result);
        }
        synchronized (requestResponseLock) {
            TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>> futureResponse = pendingFutures.remove(id);
            if (futureResponse != null) {
                ManagedFutureImpl<T, ID> ManagedFutureImpl = futureResponse.getValue().get();
                if (ManagedFutureImpl != null) {
                    ManagedFutureImpl.setValue(result);
                    return true;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Future does not exists adding as pending result: " + id + " result="+result);
            }
            pendingResults.put(id, new TimedEntry<T>(result));
            return false;
        }
    }
    
    
    /**
     * Cancels the computational task by the given ID. A flag indicating processing interruption is also given. 
     * 
     * @param id The id of the future to be canceled.
     * @param mayInterrupt a flag indicating processing interruption.
     * @return true if the future was found and it was canceled. false otherwise.
     */
    public boolean cancel(ID id, boolean mayInterrupt) {
        if (log.isTraceEnabled()) {
            log.trace("Cancelling future for: " + id + " mayInterrupt=" + mayInterrupt);
        }
        synchronized (requestResponseLock) {
            TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>> futureResponse = pendingFutures.remove(id);
            if (futureResponse != null) {
                ManagedFutureImpl<T, ID> ManagedFutureImpl = futureResponse.getValue().get();
                if (ManagedFutureImpl != null) {
                    return ManagedFutureImpl.cancel(mayInterrupt);
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Future does not exist :" + id);
            }
            return false;
        }
    }

    /**
     * Sets an exception for a given future Id.
     * 
     * @param id The id of the future to set
     * @param ex The exception to set
     * @return <code>true</code> if the future was found and the exception was set. <code>false</code> otherwise.
     */
    public boolean setException(ID id, Exception ex) {
        if (log.isTraceEnabled()) {
            log.trace("Setting exception for: " + id + " ex="+ex);
        }
        synchronized (requestResponseLock) {
            TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>> futureResponse = pendingFutures.remove(id);
            if (futureResponse != null) {
                ManagedFutureImpl<T, ID> ManagedFutureImpl = futureResponse.getValue().get();
                if (ManagedFutureImpl != null) {
                    ManagedFutureImpl.setException(ex);
                    return true;
                }
            }
            if (log.isDebugEnabled()) {
                log.debug("Future does not exists adding as pending result: " + id + " ex="+ex);
            }
            pendingExceptions.put(id, new TimedEntry<Exception>(ex));
            return false;
        }
    }
    
    /**
     * Creates a new Future with the given ID.
     * 
     * The ID must be unique for this Manager, duplicated IDs will produce incorrect results.
     * 
     * @param id The unique id for the new future
     * @return The newly created future.
     */
    public ManagedFutureImpl<T, ID> newFuture(ID id) {
        if (log.isTraceEnabled()) {
            log.trace("Creating future for: " + id);
        }
        ManagedFutureImpl<T, ID> future = new ManagedFutureImpl<T, ID>(id, futureReleaseHandler);
        synchronized (requestResponseLock) {
            TimedEntry<T> response = pendingResults.remove(id);
            TimedEntry<Exception> exception = pendingExceptions.remove(id);
            cleanUp();
            if (response == null && exception == null) {
                pendingFutures.put(id, new TimedEntry<WeakReference<ManagedFutureImpl<T, ID>>>(new WeakReference<ManagedFutureImpl<T, ID>>(future)));
            } else if (response != null) {
                future.setValue(response.getValue());
            } else {
                future.setException(exception.getValue());
            }
            return future;
        }
    }

    private void cleanUp() {
        long minTS = System.currentTimeMillis() - MAX_GAP;
        cleanUp(pendingResults, minTS);
        cleanUp(pendingExceptions, minTS);
    }

    private <S> void cleanUp(Map<ID, TimedEntry<S>> sets, long minTS) {
        Iterator<Entry<ID, TimedEntry<S>>> iterator = sets.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<ID, TimedEntry<S>> timedEntry = iterator.next();
            if (timedEntry.getValue().getEntryTS() > minTS) {
                return;
            }
            if (log.isDebugEnabled()) { 
                log.debug("Removing pending result with ID=: "+timedEntry.getKey());
            }
            iterator.remove();
        }
    }

    /**
     * Disposes the <code>FutureImplManager</code> instance. All unprocessed asynchronous tasks are cleared.
     */    
    public void release() {
        if (log.isDebugEnabled()) {
            log.debug("Releasing Future manager");
        }
        synchronized (requestResponseLock) {
            pendingFutures.clear();
            pendingResults.clear();
            pendingExceptions.clear();
        }
    }
}
