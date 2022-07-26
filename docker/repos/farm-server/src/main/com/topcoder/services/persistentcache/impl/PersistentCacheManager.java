/*
 * PersistentCacheManager
 * 
 * Created 05/18/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.topcoder.server.util.FileUtil;
import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;


/**
 * PersistentCacheManager class manages all cache instances stored inside a given 
 * root folder.<p>
 * 
 * It is reponsible for associanting instance ids to cache folders, creating new instances 
 * and deleting them when required.<p>
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: PersistentCacheManager.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PersistentCacheManager {
    public static final String ROOT_KEY = PersistentCacheManager.class.getPackage().getName()+".cache.rootFolder";
    private Logger log = Logger.getLogger(this.getClass());
    private ConcurrentPropertyFile entries;
    private File folder;
    private FSLock lock;

    /**
     * Creates a new PersistentCacheManager using as rootFolder the one specified in the system property
     * com.topcoder.services.persistentcache.impl.cache.rootFolder, if the value was not set, ./cache is used as rootFolder
     * 
     * @throws PersistentCacheException If the Manager could not be created
     */
    public PersistentCacheManager() throws PersistentCacheException {
        this(new File(System.getProperty(ROOT_KEY, "./cache")));
    }
    
    
    /**
     * Creates a new PersistentCacheManager using the given folder as rootFolder.
     * 
     * @param rootFolder The rootFolder where cache information is/will be stored
     * 
     * @throws PersistentCacheException If the Manager could not be created
     */
    public PersistentCacheManager(File rootFolder) throws PersistentCacheException {
        this.folder = rootFolder;
        if (log.isDebugEnabled()) {
            log.debug("Creating PersistentCacheManager in rootFolder"+folder.getAbsolutePath());
        }
        init();
        this.lock = new FSLock(new File(rootFolder, "lock"));
        this.entries = new ConcurrentPropertyFile(new File(folder, "ids"), lock);
    }

    /**
     * Returns a PersistentCache for the given cache Id.<p>
     * 
     * If the Cache instance has not been created yet, it creates a new one.
     * 
     * @param cacheId The id of the cache instance
     * @return The new PersistentCache for the given id
     * 
     * @throws PersistentCacheException If the PersistentCache could not be created for any reason
     */
    public PersistentCache getCache(String cacheId) throws PersistentCacheException {
        if (log.isDebugEnabled()) {
            log.debug("Obtaining instance :" + cacheId);
        }
        lock.lock();
        try {
            String f = null;
            f = entries.getProperty(cacheId);
            if (f == null) {
                if (log.isDebugEnabled()) {
                    log.debug("Not found creating new one");
                }
                f = FileUtil.generateNewFolder(folder, cacheId);
                entries.setProperty(cacheId, f);
                log.info("Cache instance "+cacheId+" created in folder "+f);
            }
            return new PersistentCacheImpl(new File(folder, f), cacheId);
        } catch (PersistentCacheException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistentCacheException("Could not obtaint entry for cacheId="+cacheId, e);
        } finally {
            lock.unlock();
        }
    }
    
    /**
     * Deletes the cache instance with the given id. This method completely deletes all information 
     * related to the specified cache instance.<p> 
     * 
     * @param cacheId The id of the cache to delete
     * 
     * @throws PersistentCacheException if the cache instance could not be deleted.
     */
    public void delete(String cacheId) throws PersistentCacheException {
        lock.lock();
        try {
            String f = null;
            f = entries.removeProperty(cacheId);
            if (f != null) {
                FileUtil.deleteRecursive(new File(folder, f));
            }
        } catch (Exception e) {
            throw new PersistentCacheException("Could not obtaint entry for cacheId="+cacheId, e);
        } finally {
            lock.unlock();
        }
    }

    
    /**
     * Returns summary information of all the cache managed by this PersistentCacheManager
     * 
     * @return A List of {@link CacheInfo} object, one for each cache instance managed
     * @throws PersistentCacheException If the summary could not be obtained
     */
    public List getCacheSummary() throws PersistentCacheException {
        try {
            Properties p = entries.getProperties();
            List results = new ArrayList(p.size());
            Enumeration e = p.propertyNames();
            while (e.hasMoreElements()) {
                String id = (String) e.nextElement();
                String path = p.getProperty(id);
                PersistentCache cache = getCache(id);
                int size = cache.size();
                long version = cache.getVersion();
                results.add(new CacheInfo(id, path, version, size));
            }
            Collections.sort(results, new Comparator() {
                public int compare(Object arg0, Object arg1) {
                    return ((CacheInfo) arg0).getId().compareTo(((CacheInfo) arg1).getId());
                }
            
            });
            return results;
        } catch (IOException e) {
            throw new PersistentCacheException("Could not obtain cache info",e);
        }
    }
    
    /**
     * Sets the given cacheFolder as the cache instance folder to use for the cache with the given Id.
     * 
     * The cacheFolder must be relative to the rootPath of this PersistentCacheManager and must follow
     * {@link PersistentCacheImpl} storage rules.
     * 
     * @param id The cache instance id
     * @param cacheFolder The cache folder to set 
     * @return The number of cache items found in the cache folder.
     * 
     * @throws PersistentCacheException If the folder could not be set.
     */
    public int setInstanceFolder(String id, String cacheFolder) throws PersistentCacheException {
        File file = new File(folder, cacheFolder);
        if (!file.exists()) {
            throw new PersistentCacheException("Folder does not exists: "+file.getAbsolutePath());
        }
        int size = new PersistentCacheImpl(file, id).size();
        try {
            entries.setProperty(id, cacheFolder);
        } catch (IOException e) {
            throw new PersistentCacheException("Could not set the folder as cache folder", e);
        }
        return size;
    }

    
    private void init() throws PersistentCacheException {
        if (!folder.mkdirs() && !folder.exists()) {
            throw new PersistentCacheException("Could not create root cache folder");
        }
    }
    
    public static class CacheInfo {
        private String id;
        private String path;
        private long version;
        private int size;
        
        public CacheInfo(String id, String path, long version, int size) {
            this.id = id;
            this.path = path;
            this.version = version;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public String getPath() {
            return path;
        }

        public int getSize() {
            return size;
        }

        public long getVersion() {
            return version;
        }
    }
}
