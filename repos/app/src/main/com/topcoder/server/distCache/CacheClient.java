package com.topcoder.server.distCache;

import java.rmi.Remote;
import java.rmi.RemoteException;

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
 */

public interface CacheClient
        extends Remote {

    /**
     *  set a key/value pair (priority will be set to default value for
     *  new values)
     *  @param key   the key for the cached value
     *  @param value the value to be stored
     */
    public void set(String key, Object value)
            throws RemoteException;

    /**
     *  set a key/value pair
     *  priority will not be affected
     *  @param key   the key for the cached value
     *  @param value the value to be stored
     *  @param prio  the priority of the cached value
     */
    public void set(String key, Object value, int prio)
            throws RemoteException;

    /**
     *  release the lock associated with a given key
     */
    public void releaseLock(String key)
            throws RemoteException;


    /**
     *  retrieve the value associated with a key.
     *  @param  key   the key to query on
     *  @returns the cached value, if exists, otherwise null
     */

    public Object get(String key)
            throws RemoteException;

    /**
     *  retrieve the value associated with a key, locking
     *  against other locked access
     *
     *  @param  key   the key to query on
     *  @returns the cached value, if exists, otherwise null
     */

    public Object getAndLock(String key)
            throws RemoteException;
}
