package com.topcoder.shared.distCache;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *  the interface used for server to server synchronization.  This
 *  interface is subject to change, but is only exposed to other parts
 *  of the cache service so impact will be minimal.
 *
 *  I expect behaviour to look like this:
 *
 *  At startup, server initially tries to connect to peer.  If the
 *  peer is already up, transfer the remote cache locally.  If the
 *  peer is not yet up, periodically retry until it is up.
 *
 *  At a synchronization point, call the remote synchronize() method.
 *  The remote side will queue up changes for transfer and pass them
 *  at this point.  (if the remote server is not connected, we don't
 *  need to queue)  The change list will be merged locally according
 *  to version number.  The if the remote side has a higher version
 *  of the object, that version is used.  If the versions are identical,
 *  the value from the primary will be used.
 *
 *  The interface and behaviours will be refined during the development
 *  process as I get a better understanding of the expected cache sizes
 *  and performance/reliability needs.
 *  @author orb
 *  @version  $Revision$
 */
public interface CacheServerSync
        extends Remote {
    /**
     *  return the entire cache
     *
     * @return the current remote cache
     * @throws RemoteException
     */
    public Cache
            getCache()
            throws RemoteException;

    /**
     *  return a list of cached values representing changes since the
     *  last synchronize event.  (or since the initial getCache()
     *  query.
     *  @return an array of CachedValue objects
     *  @throws RemoteException
     */
    public CachedValue[] synchronize()
            throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean getCleared()
            throws RemoteException;
}
