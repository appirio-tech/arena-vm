/*
 * PersistentCache
 * 
 * Created 05/16/2007
 */
package com.topcoder.services.persistentcache;

import java.io.Serializable;

import com.topcoder.shared.netCommon.CustomSerializable;


/**
 * An object that associated keys to values. The associations are persistent and 
 * must be removed explicitly. A PersistentCache cannot contains duplicate keys and 
 * a key can be associated to only one value.<p>
 * 
 * Implementators of this interface will ensure persistence of the associations even
 * after an application restart. In addition, implementations will ensure thread safety 
 * and will allow simultaneous access by multiples VMs.<p>
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: PersistentCache.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public interface PersistentCache {
    /**
     * Obtains the current version of this cache instance.
     * 
     * @return the version number.
     * @throws PersistentCacheException  if the version could not be obtained
     */
    long getVersion() throws PersistentCacheException;
    
    /**
     * Sets the minimal version of this cache instace.<p>
     * 
     * If <code>version</code> value is greater than the one returned by {@link PersistentCache#getVersion() getVersion}, 
     * contents of this cache instance will be cleared and cache version will be changed to the specified version.
     * 
     * If <code>version</code> is lower than the one returned by {@link PersistentCache#getVersion() getVersion}, 
     * invoking this method has no effect.
     * 
     * @param version The minimal version to ensure
     * 
     * @throws PersistentCacheException If set minimal version coould not be set
     */
    void setMinimalVersion(long version) throws PersistentCacheException;
    
    
    /**
     * Associates the specified value with the specified key in this cache instance, 
     * deleting any previous value associated to the key.
     *
     * @param key key with which the specified value is to be associated.
     * @param value value to be associated with the specified key. Value must implement either {@link Serializable} or
     *              {@link CustomSerializable} in order be successfuly stored in the cache.
     *              
     * @throws PersistentCacheException if the value could not be associated for any reason
     */
    void put(String key, Object object) throws PersistentCacheException;
    
    /**
     * Returns the value associated with the given key on this cache instance.  
     *
     * @param key key whose associated value is to be returned.
     * @return the value associated to the given key, <code>null</code> if no value is associated to 
     *         the given key.
     * 
     * @throws PersistentCacheException if the value could not be obtained for any reason
     */
    Object get(String key) throws PersistentCacheException;
    
    /**
     * Removes the association between the given key and its associated value
     * if any.
     * 
     * @param key The whose association want to be remove.
     * 
     * @throws PersistentCacheException if the association coul not be removed for any for any reason
     */
    void remove(String key) throws PersistentCacheException;
    
    /**
     * Removes all associations contained in this cache instance.
     * 
     * @throws PersistentCacheException if associations could not be deleted for any reason
     */
    void clear() throws PersistentCacheException;
    
    /**
     * Returns the number of elements stored in this cache instance.<p>
     * 
     * @return the size
     * 
     * @throws PersistentCacheException  if the size could not be obtained for any reason.
     */
    int size() throws PersistentCacheException;
}
