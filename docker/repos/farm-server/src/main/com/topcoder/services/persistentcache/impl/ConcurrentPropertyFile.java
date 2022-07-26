/*
 * ConcurrentPropertyFile
 * 
 * Created 05/18/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Properties like class backed by a property file that supports concurrent access.<p>
 * 
 * No information is held in memory, storage and retrieval is done through 
 * the associated property file. <p>
 * 
 * This class allows multiple processes (Different VMs) and threads to access the same
 * property file in a consistent way as long as they use the same file as lock object.
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: ConcurrentPropertyFile.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class ConcurrentPropertyFile {
    /**
     * The property file
     */
    private File file;
    
    /**
     * The File system lock used for mutual exclusion
     */
    private FSLock lock;
    
    /**
     * Creates a new ConcurrentPropertyFile using the fiven <code>file</code> as 
     * properties repository and the given <code>lock\</code> for mutual exclusion
     * 
     * @param file The property file to use
     * @param lock The lock
     */
    public ConcurrentPropertyFile(File file, FSLock lock) {
        this.file = file;
        this.lock = lock;
    }
    
    /**
     * Returns the value associated to the given key.
     *  
     * @param key The key 
     * @return The value, or null if no value is associated to the key
     * 
     * @throws IOException If an IO exception occurs while accessing the property file
     */
    public String getProperty(String key) throws IOException {
        Properties p = loadFile();
        return p.getProperty(key);
    }
    
    /**
     * Associates the value to the key.
     *  
     * @param key The key 
     * @param newValue The value to associate to the key
     * @return The previous value associated to the key if any
     * 
     * @throws IOException If an IO exception occurs while accessing the property file
     */
    public String setProperty(String key, String newValue) throws IOException {
        boolean mustUnlock = lockIfNotLocked();
        try {
            Properties p = nonLockLoadFile();
            String v = (String) p.setProperty(key, newValue);
            nonLockWriteFile(p);
            return v;
        } finally {
            if (mustUnlock) lock.unlock();
        }
    }

    /**
     * Returns the number of associations defined.
     * 
     * @return A integer >= 0
     * @throws IOException If an IO exception occurs while accessing the property file
     */
    public int size() throws IOException {
        return loadFile().size();
    }
    
    /**
     * Removes the association for a given key
     * 
     * @param key The key to remove
     * @return The value associated to the key, if any
     * @throws IOException If an IO exception occurs while accessing the property file
     */
    public String removeProperty(String key) throws IOException  {
        boolean mustUnlock = lockIfNotLocked();
        try {
            Properties p = nonLockLoadFile();
            String v = (String) p.remove(key);
            nonLockWriteFile(p);
            return v;
        } finally {
            if (mustUnlock) lock.unlock();
        }
    }
    
    /**
     * Returns a copy of the properties defined in this ConcurrentPropertyFile
     * 
     * @return the Properties
     * @throws IOException If an IO exception occurs while accessing the property file
     */
    public Properties getProperties() throws IOException {
        return loadFile();
    }
    
    /**
     * Clear all properties defined in this ConcurrentPropertyFile
     *  
     * @return a copy Properties containing all the property definitions before clearing the instance
     */
    public Properties clear() {
        Properties p = null;
        boolean mustUnlock = lockIfNotLocked();
        try {
            p = nonLockLoadFile();
            nonLockWriteFile(new Properties());
        } catch (IOException e) {
            p = new Properties();
        } finally {
            if (mustUnlock) lock.unlock();
        }
        return p;
    }
    
    
    
    private void nonLockWriteFile(Properties p) throws IOException {
        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        p.store(os, "");
        os.close();
    }
    
    private Properties loadFile() throws IOException {
        boolean mustUnlock = lockIfNotLocked();
        try {
            return nonLockLoadFile();
        } finally {
            if (mustUnlock) lock.unlock();
        }
    }

    private Properties nonLockLoadFile() throws IOException {
        Properties p = new Properties();
        if (file.exists()) {
            BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
            p.load(is);
            is.close();
        }
        return p;
    }

    
    
    private boolean lockIfNotLocked() {
        boolean locked = lock.haveLocked();
        if (!locked) lock.lock();
        return !locked;
    }
}
