package com.topcoder.shared.distCache;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author orb
 * @version  $Revision$
 */
public class CacheServerSyncImpl
        extends UnicastRemoteObject
        implements CacheServerSync {
    CacheServer _server;
    ListSyncImpl _listener = new ListSyncImpl();

    /**
     *
     * @param server
     * @throws RemoteException
     */
    public CacheServerSyncImpl(CacheServer server)
            throws RemoteException {
        _server = server;
        getCache().setUpdateListener(_listener);
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public Cache getCache()
            throws RemoteException {
        return _server.cache();
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public CachedValue[] synchronize()
            throws RemoteException {
        CachedValue[] result = _listener.getChanged();
        return result;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean getCleared()
            throws RemoteException {
        return _listener.getCleared();
    }
}
