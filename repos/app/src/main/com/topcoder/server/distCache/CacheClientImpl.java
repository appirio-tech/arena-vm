package com.topcoder.server.distCache;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CacheClientImpl
        extends UnicastRemoteObject
        implements CacheClient {

    Cache _cache;

    CacheClientImpl(Cache cache)
            throws RemoteException {
        _cache = cache;
    }


    public void set(String key, Object value)
            throws RemoteException {
        set(key, value, Cache.DEFAULT_PRIORITY);
    }

    public void set(String key, Object value, int prio)
            throws RemoteException {
        _cache.update(key, value, prio, System.currentTimeMillis());
    }

    public Object get(String key)
            throws RemoteException {
        System.out.println("GET: " + key);
        try {
            return _cache.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


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


    public void releaseLock(String key)
            throws RemoteException {
        _cache.releaseLock(key);
    }

}
