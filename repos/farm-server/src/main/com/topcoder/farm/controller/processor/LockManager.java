/*
 * LockManager
 * 
 * Created 08/09/2006
 */
package com.topcoder.farm.controller.processor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.topcoder.farm.shared.util.concurrent.Lock;

/**
 * The lock manager allows to obtain a lock associated with an object
 *  
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class LockManager<T> {
    private Log log = LogFactory.getLog(LockManager.class);
    private ConcurrentMap<T, Lock> locks = new ConcurrentHashMap<T, Lock>();
   
    /**
     * Returns the lock acquired. 
     * This method will block until the lock be obtained.
     * 
     * @param object The object whose lock want to be acquired
     * @return The lock object
     */
    public Lock lock(T object) {
        Lock lock = locks.get(object);
        if (lock == null) {
            Lock newLock = new Lock();
            lock = locks.putIfAbsent(object, newLock);
            lock = (lock == null ? newLock : lock);
        }
        lock.lock();
        if (log.isDebugEnabled()) {
            log.debug("Got lock for: "+object);
        }
        return lock;
    }
}
