package com.topcoder.server.services;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.log4j.Logger;

import com.topcoder.server.distCache.CacheClient;


public final class SimpleCache implements CacheClient {

    private static Logger trace = Logger.getLogger(SimpleCache.class);
    private static Map m_cache;
    private static HashSet m_locks = new HashSet();
    private static Map m_lockHolders = new HashMap();
    private static long lastDisplayTS = 0;
    private static Map strongRef = new HashMap();

    public SimpleCache() {
        this(new HashMap());
    }
    
    public SimpleCache(Map map) {
        m_cache = map;
    }
    
    public void set(String key, Object value) {
        synchronized (m_cache) {
            if (System.currentTimeMillis() > lastDisplayTS) {
                //After 30secs, log cache size
                trace.info("Cache size: "+m_cache.size()+" StrongRefs Size: "+strongRef.size());
                lastDisplayTS = System.currentTimeMillis() + 30000;
            }
            if (value != null) {
                m_cache.put(key, value);
            } else {
                m_cache.remove(key);
            }
            if (strongRef.containsKey(key)) {
                ((StrongReference) strongRef.get(key)).value = value;
            }
       }
    }

    public void removeRef(String key) {
        synchronized (m_cache) {
            StrongReference ref = (StrongReference) strongRef.get(key);
            if (ref == null) return;
            --ref.references;
            if (trace.isDebugEnabled()) {
                trace.debug("Remove strong reference " + key + ": references=" + ref.references);
            }
            if (ref.references == 0) {
                ref.value = null;
                strongRef.remove(key);
            }
        }
    }

    public void addRef(String key) {
        synchronized (m_cache) {
            StrongReference ref = (StrongReference) strongRef.get(key);
            if (ref == null) {
                ref = new StrongReference();
                strongRef.put(key, ref);
                ref.value = m_cache.get(key);
            }
            ++ref.references;
            if (trace.isDebugEnabled()) {
                trace.debug("Add strong reference " + key + ": references=" + ref.references);
            }
        }
    }

    /**
     *  set a key/value pair
     *  priority will not be affected
     *  @param key   the key for the cached value
     *  @param value the value to be stored
     *  @param prio  the priority of the cached value
     */
    public void set(String key, Object value, int prio) {
        set(key, value);
    }

    /**
     *  release the lock associated with a given key
     */
    public void releaseLock(String key) {
        synchronized (m_locks) {
            m_locks.remove(key);
            m_lockHolders.remove(key);
            m_locks.notifyAll();
        }
    }

    /**
     *  retrieve the value associated with a key.
     *  @param  key   the key to query on
     *  @returns the cached value, if exists, otherwise null
     */

    public Object get(String key) {
        synchronized (m_cache) {
            return m_cache.get(key);
        }
    }


    private static final int WAIT_TIME = 1000;
    private static final int MAX_WAIT_TIME = 10000;

    /**
     *  retrieve the value associated with a key, locking
     *  against other locked access
     *
     *  @param  key   the key to query on
     *  @returns the cached value, if exists, otherwise null
     */
    public Object getAndLock(String key) {
        long startTime = System.currentTimeMillis();
        boolean displayedTrace = false;
        synchronized (m_locks) {
            while (m_locks.contains(key)) {
                try {
                    m_locks.wait(WAIT_TIME);
                } catch (InterruptedException ie) {
                }
                if (!displayedTrace) {
                    long time = System.currentTimeMillis() - startTime;
                    if (time > MAX_WAIT_TIME) {
                        displayedTrace = true;
                        Throwable t = (Throwable) m_lockHolders.get(key);
                        trace.error("Failed to obtain lock on " + key + " after waiting " + time + " ms", t);
                        trace.error("Calling from:", new Throwable("Deadlocked Thread: " + Thread.currentThread().getName()));
                    }
                }
            }
            m_locks.add(key);
            m_lockHolders.put(key, new Throwable("Lock Holder: " + Thread.currentThread().getName()));
        }
        return get(key);
    }

    public void clearRef() {
        synchronized (m_cache) {
            strongRef.clear();
        }
    }

    private static class StrongReference {
        public Object value = null;
        public int references = 0;
    }
}
