package com.topcoder.shared.distCache;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *  The cache client is the interface by which clients connect to
 *  the cache.  There are three basic parameters used when talking
 *  to the CacheClient.
 *
 *  key - a String representing the key value.  While a key can be any
 *  value, I expect to recommend using a "." notation for key values.
 *  ("users.user1", for example) This will allow the cache server to
 *  use different cache size and expiration policies for different key
 *  spaces.
 *
 *  value - a value to be stored in the remote server.  Values should
 *  be limited to Serializable objects to ensure that they can be
 *  passed by value over rmi.
 *
 *  priority - an arbitrary integer representing the priority of the
 *  cached value.  Lower priority values will be expired be expired
 *  before higher priority values.  Priority can be any integer,
 *  but I expect the values 1..10, with 5 being the the default
 *  priority.
 *
 *  @author orb
 *  @version  $Revision$
 */

public interface CacheClient
        extends Remote {
    /**
     * set a key/value pair (priority will be set to default value for
     * new values)
     * @param key   the key for the cached value
     * @param value the value to be stored
     * @param expire
     * @throws RemoteException
     */
    public void set(String key, Object value, long expire)
            throws RemoteException;

    /**
     * set a key/value pair
     * priority will not be affected
     * @param key   the key for the cached value
     * @param value the value to be stored
     * @param prio  the priority of the cached value
     * @param expire
     * @throws RemoteException
     */
    public void set(String key, Object value, int prio, long expire)
            throws RemoteException;

    /**
     *  release the lock associated with a given key
     * @param key
     * @throws RemoteException
     */
    public void releaseLock(String key)
            throws RemoteException;


    /**
     * retrieve the value associated with a key.
     * @param  key   the key to query on
     * @return
     * @throws RemoteException
     */
    public Object get(String key)
            throws RemoteException;

    /**
     * retrieve the value associated with a key, locking
     * against other locked access
     *
     * @param  key   the key to query on
     * @return the cached value, if exists, otherwise null
     * @throws RemoteException
     */
    public Object getAndLock(String key)
            throws RemoteException;

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    public CachedValue remove(String key)
            throws RemoteException;

    /**
     *
     * @throws RemoteException
     */
    public void clearCache()
            throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getEntries()
            throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getKeys()
            throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public ArrayList getValues()
            throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public int size()
            throws RemoteException;

    public int getSize(String key)
            throws RemoteException;

    public boolean containsKey(String key)
            throws RemoteException;
}
