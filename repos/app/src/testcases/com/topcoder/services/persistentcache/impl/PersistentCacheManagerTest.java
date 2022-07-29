/*
 * PersistentCacheManagerTest
 * 
 * Created 05/21/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;

import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;
import com.topcoder.services.persistentcache.impl.PersistentCacheManager;


/**
 * Test case for PersistentCacheManager
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PersistentCacheManagerTest.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PersistentCacheManagerTest extends PersistentCacheImplTest {
    private PersistentCacheManager manager;

    public PersistentCacheManager getManager() throws PersistentCacheException {
        if (manager == null) {
            manager = new PersistentCacheManager(rootFolder);
        }
        return manager;
    }

    public void testDeleteRemovesEverything() throws Exception {
        removeCache();
        PersistentCache cache = buildCacheInstance();
        cache.setMinimalVersion(10);
        cache.put("Key1", "Value1");
        manager.delete("ID1");
        assertFalse(new File(rootFolder, " ID1").exists());
    }
    
    public void testDeleteRemovesEverythingStored() throws Exception {
        PersistentCache cache = buildCacheInstance();
        assertNull(cache.get("Key1"));
        assertEquals(0,cache.size());
        assertEquals(0, cache.getVersion());
    }
    
    public void testSetFolder() throws Exception {
        PersistentCache cache = buildCacheInstance();
        cache.setMinimalVersion(1);
        cache.put("Key1", "Value1");
        PersistentCacheManager manager = new PersistentCacheManager(rootFolder);
        manager.setInstanceFolder("ID2", "ID1");
        cache = manager.getCache("ID2");
        assertEquals("Value1" , cache.get("Key1"));
        assertEquals(1,cache.size());
        assertEquals(1,cache.getVersion());
        removeCache();
    }
    
    
    protected PersistentCache buildCacheInstance() throws PersistentCacheException {
        return getManager().getCache("ID1");
    }
}
