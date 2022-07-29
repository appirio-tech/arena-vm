/*
 * PersistentCacheProvider
 * 
 * Created 05/21/2007
 */
package com.topcoder.services.persistentcache.impl;

import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;


/**
 * Simple cache PersistentCacheProvider that hides cache creation
 * complexity from users of a PersistentCache.<p>
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: PersistentCacheProvider.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PersistentCacheProvider {
    private static PersistentCacheManager manager;
    
    /**
     * Returns a new PersistentCache instance for the given cache Id
     * 
     * @param id The id of the cache obtain
     * @return The PersistentCache instance
     * 
     * @throws PersistentCacheException If the PersistentCache could not be created
     */
    public static PersistentCache newInstance(String id) throws PersistentCacheException {
        try {
            return getManager().getCache(id);
        } catch (RuntimeException e) {
            throw new PersistentCacheException(e);
        }
    }

    private static PersistentCacheManager getManager() throws PersistentCacheException {
        if (manager == null) {
            manager = new PersistentCacheManager();
        }
        return manager;
    }
}
