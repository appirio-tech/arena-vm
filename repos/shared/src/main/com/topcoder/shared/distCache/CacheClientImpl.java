package com.topcoder.shared.distCache;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * @author orb
 * @version  $Revision$
 */
public class CacheClientImpl
        extends UnicastRemoteObject
        implements CacheClient {
    Cache _cache;

    CacheClientImpl(Cache cache)
            throws RemoteException {
        _cache = cache;
    }


    /**
     *
     * @param key
     * @param value
     * @param expire
     * @throws RemoteException
     */
    public void set(String key, Object value, long expire)
            throws RemoteException {
        set(key, value, Cache.DEFAULT_PRIORITY, expire);
    }

    /**
     *
     * @param key
     * @param value
     * @param prio
     * @param expire
     * @throws RemoteException
     */
    public void set(String key, Object value, int prio, long expire)
            throws RemoteException {
        _cache.update(key, value, prio, System.currentTimeMillis(), expire);
    }

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    public Object get(String key)
            throws RemoteException {
        try {
            return _cache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    public Object getAndLock(String key)
            throws RemoteException {
        try {
            _cache.lock(key);
            return _cache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     *
     * @param key
     * @throws RemoteException
     */
    public void releaseLock(String key)
            throws RemoteException {
        _cache.releaseLock(key);
    }

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    public CachedValue remove(String key)
            throws RemoteException {
        CachedValue cv = _cache.remove(key);
        if (cv != null) return cv;
        return null;
    }

    /**
     *
     * @throws RemoteException
     */
    public void clearCache()
            throws RemoteException {
        _cache.clearCache();
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getEntries()
            throws RemoteException {
        return _cache.getEntries();
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getKeys()
            throws RemoteException {
        return _cache.getKeys();
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getValues()
            throws RemoteException {
        return _cache.getValues();
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public int size()
            throws RemoteException {
        return _cache.size();
    }

    public int getSize(String key)
            throws RemoteException {
        return _cache.getSize(key);
    }

    public boolean containsKey(String key)
            throws RemoteException {
        return _cache.exists(key);
    }
}
