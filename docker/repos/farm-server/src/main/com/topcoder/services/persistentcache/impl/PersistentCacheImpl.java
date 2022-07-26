/*
 * PersistentCacheImpl
 * 
 * Created May 16, 2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

import com.topcoder.io.serialization.basictype.impl.BasicTypeDataInputImpl;
import com.topcoder.io.serialization.basictype.impl.BasicTypeDataOutputImpl;
import com.topcoder.server.util.FileUtil;
import com.topcoder.shared.util.logging.Logger;
import com.topcoder.services.persistentcache.PersistentCache;
import com.topcoder.services.persistentcache.PersistentCacheException;

/**
 * PersistentCache implementation.<p>
 * 
 * This class implements persistence using the file system. All information for a given instance  
 * is stored using files. In addition, the file system is used to ensure mutual exclusion when 
 * a new cache version is created or deleted.<p>
 * 
 * Files stored in the cache folder:
 *  <li>version Contains the current version of the cache
 *  <li>objects Properties file containing the object key as property key and the filename as value.
 *  A given filename must not be associated to more than one key.
 *  <li>...     One file for each filename included as a value in the objects properties file. 
 *  Each file must contain a serialized version of the Object stored. Object must be serialized
 *  using {@link CacheCSHandler} serializer.
 *             
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PersistentCacheImpl.java 72723 2008-09-08 04:15:31Z qliu $
 */
class PersistentCacheImpl implements PersistentCache {
    private Logger log = Logger.getLogger(PersistentCacheImpl.class);
    private CacheCSHandler csHandler = new CacheCSHandler();
    private Object outputMutex = new Object();
    private Object inputMutex  = new Object();
    private File folder;
    private String cacheId;
    /**
     * Contains associations between keys and filenames
     */
    private ConcurrentPropertyFile objectMap;
    /**
     * Filesystem lock to handle concurrent access.
     */
    private FSLock lock;
    
    /**
     * Creates a new PersistentCacheImpl with the given cacheId,
     * This new instance will store all cache information in the given folder.<p>
     * 
     * @param folder The folder to use for storage
     * @param cacheId The id of the Cache
     * 
     * @throws PersistentCacheException If the cache instance could not be created
     */
    public PersistentCacheImpl(File folder, String cacheId) throws PersistentCacheException {
        this.cacheId = cacheId;
        this.folder = folder;
        this.lock = new FSLock(new File(folder.getAbsolutePath()+".lck"));
        init();
    }
    
    public void clear() {
        log.info("Clearing cache instance: "+cacheId);
        Collection fileNames = objectMap.clear().values();
        for (Iterator it = fileNames.iterator(); it.hasNext();) {
            String filename = (String) it.next();
            File file = new File(folder, filename);
            try {
                file.delete();
            } catch (Exception e) {
                log.error("Could not delete file: "+file.getAbsolutePath(), e);
            }
        }
    }

    public Object get(String key) throws PersistentCacheException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Get key: "+key);
            }
            String f = objectMap.getProperty(key);
            if (f == null) {
                log.debug("Cache miss");
                return null;
            } else {
                log.debug("Cache hit");
                return loadObjectFromFile(new File(folder, f));
            }
        } catch (Exception e) {
            throw new PersistentCacheException("Could not obtain object with key=" + key, e) ;
        }
    }

    public void put(String key, Object object) throws PersistentCacheException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Storing object for key="+key);
            }
            String f = generateNewFile(key);
            File file = new File(folder, f);
            storeObjectInFile(file, object);
            f = objectMap.setProperty(key, f);
            if (f != null) {
                FileUtil.deleteRecursive(new File(folder, f));
            }
        } catch (Exception e) {
            throw new PersistentCacheException("Could not store object with key=" + key + " object=" + object, e) ;
        }
    }
    
    public void remove(String key) throws PersistentCacheException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Removing key="+key);
            }
            String f = objectMap.removeProperty(key);
            File file = new File(folder, f);
            if (!FileUtil.deleteRecursive(file)) {
                log.error("Could not delete the file: "+file.getAbsolutePath());
            }
        } catch (Exception e) {
            throw new PersistentCacheException("Count not remove the cache entry: "+ key, e);
        }
    }

    public long getVersion() throws PersistentCacheException {
        return readVersion();
    }

    public void setMinimalVersion(long version) throws PersistentCacheException {
        if (version > readVersion()) {
            lock.lock();
            try {
                if (version > readVersion()) {
                    log.info("Setting minimal version to: "+ version);
                    reset(version);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public int size() throws PersistentCacheException {
        try {
            return objectMap.size();
        } catch (IOException e) {
            throw new PersistentCacheException("Could not obtain cache size", e);
        }
    }    
    
    private void init() throws PersistentCacheException {
        try {
            if (log.isDebugEnabled()) {
                log.debug("Initializing cache instance: "+cacheId);
            }
            folder.getParentFile().mkdirs();
            lock.lock();
            try {
                if (getVersionFile().exists()) {
                    initFromFolder();
                } else {
                    createCacheFolder();
                }
                objectMap = new ConcurrentPropertyFile(new File(folder,"objects"), lock);
                if (log.isDebugEnabled()) {
                    log.debug("Cache instance: "+cacheId+" initialized version="+getVersion());
                }
            } finally {
                lock.unlock();
            }
        } catch (PersistentCacheException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistentCacheException(e);
        }
    }

    private void initFromFolder() throws PersistentCacheException {
        //Nothing to do for now
    }
    
    private void storeObjectInFile(File file, Object object) throws IOException {
        synchronized (outputMutex) {
            BasicTypeDataOutputImpl os = new BasicTypeDataOutputImpl(new FileOutputStream(file));
            csHandler.setDataOutput(os);
            csHandler.writeObjectForCache(object);
            os.close();
        }
    }
    
    private Object loadObjectFromFile(File f) throws IOException {
        synchronized (inputMutex) {
            BasicTypeDataInputImpl is = new BasicTypeDataInputImpl(new FileInputStream(f), f.length());
            csHandler.setDataInput(is);
            Object object = csHandler.readObject();
            is.close();
            return object;
        }
    }

    private String generateNewFile(String bestName) throws IOException {
        return FileUtil.generateNewFile(folder, bestName, "obj");
    }

    private void reset(long version) throws PersistentCacheException {
        log.info("Resetting cache to version: "+version);
        clear();
        saveVersion(version);
        log.info("Reset succeeded");
    }

    private void createCacheFolder() throws PersistentCacheException {
        if (!folder.mkdirs() && !folder.exists()) {
            throw new PersistentCacheException("Could not created folder: "+folder.getAbsolutePath());
        }
        saveVersion(0);
    }

    private void saveVersion(long version) throws PersistentCacheException {
        try {
            FileUtil.writeContents(getVersionFile(), String.valueOf(version));
        } catch (IOException e) {
            throw new PersistentCacheException("Could not store cache version",e);
        }
    }
    
    private long readVersion() throws PersistentCacheException {
        try {
            return Integer.parseInt(FileUtil.getStringContents(getVersionFile()));
        } catch (Exception e) {
            throw new PersistentCacheException("Could not read cache version", e);
        }
    }
    
    private File getVersionFile() {
        return new File(folder, "version");
    }
}
