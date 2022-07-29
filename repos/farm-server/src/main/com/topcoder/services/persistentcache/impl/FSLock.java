/*
 * FSLock
 * 
 * Created 05/17/2007
 */
package com.topcoder.services.persistentcache.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * File system Lock implementation.<p>
 * 
 * This lock implementation uses a File system functionality to implement a lock 
 * that can be used from different VMs.<p>
 * 
 * This lock implementation is non Reentrant. Trying to acquire the lock more than once by 
 * the same thread will result in an IllegalStateException throw. This will occur even when 2
 * instances of FSLock using the same File are used 
 * 
 * 
 * @autor Diego Belfer (Mural)
 * @version $Id: FSLock.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class FSLock {
    private Logger log = Logger.getLogger(getClass());
    private static Map mutexes = Collections.synchronizedMap(new HashMap());
    private static Map threads = Collections.synchronizedMap(new HashMap());
    
    /**
     * The file used for locking
     */
    private File lockFile;
    /**
     * When the lock is acquired, contains the FileChannel
     */
    private FileChannel channel;
    /**
     * When the lock is acquired, contains the FileLock
     */
    private FileLock lock;
    
    /**
     * Creates a new FSLock, using the given file as underluying lock object.
     *  
     * @param lockFile The file to use as lock.
     */
    public FSLock(File lockFile) {
        this.lockFile = lockFile;
        if (log.isDebugEnabled()) {
            log.debug("Creating lock ["+this.hashCode()+"] for file: "+lockFile.getAbsolutePath());
        }
        createMutex();
    }
    

    protected void finalize() throws Throwable {
        releaseMutex();
    }

    /**
     * Acquires the lock.<p>
     * 
     * If the lock is not held by any thread, the calling thread acquired the lock and returns
     * immediately. If the lock is taken by another thread, the calling thread becomes disabled
     * until the lock can be acquired. If the lock is already taken by the calling thread
     * an IllegalStateException is thrown. 
     */
    public void lock() {
        setThread();
        
        if (lock != null) {
            throw new IllegalStateException("Lock already taken");
        }
        if (log.isDebugEnabled()) {
            log.debug("Locking ["+this.hashCode()+"]");
        }
        try {
            ensureChannel();
            lock = channel.lock();
        } catch (Exception e) {
            clearThread();
            throw new RuntimeException("Unexpected Exception",e);
        }
    }

    private void ensureChannel() throws FileNotFoundException {
        if (channel == null) {
            RandomAccessFile file = new RandomAccessFile(lockFile, "rws");
            channel = file.getChannel();
        }
    }

    /**
     * Releases the lock.<p>
     * 
     * If the current thread holds the lock, it is released; in other case
     * it simply returns.
     */
    public void unlock() {
        if (!checkThread()) {
            return;
        }
        try {
            FileChannel c = channel;
            if (c == null) {
                return;
            }
            if (log.isDebugEnabled()) {
                log.debug("Unlocking ["+this.hashCode()+"]");
            }
            try {
                c.close();
            } catch (IOException e) {
                log.error("Could not unlock ["+this.hashCode()+"]. Releasing channel.", e);
            }
            channel = null;
            lock = null;
        } finally {
            clearThread();
        }
    }
    
    /**
     * Returns true if the current thread holds the lock.
     * 
     * @return true if the thread holds the lock
     */
    public boolean haveLocked() {
        return checkThread();
    }

    private void setThread() {
        Thread currentThread = Thread.currentThread();
        Object mutex = getMutex();
        synchronized (mutex) {
            while (getThread() != null && getThread() != currentThread) {
                try {
                    mutex.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            setThread(currentThread);
        }
    }

    private void clearThread() {
        Object mutex = getMutex();
        synchronized (mutex) {
            setThread(null);
            mutex.notify();
        }
    }
    
    private boolean checkThread() {
        synchronized (getMutex()) {
            return getThread() == Thread.currentThread();
        }
    }
    
    private Thread getThread() {
        return (Thread) threads.get(lockFile);
    }
    
    private void setThread(Thread thread) {
        if (thread != null) {
            threads.put(lockFile, thread);
        } else {
            threads.remove(lockFile);
        }
    }
    
    private Object getMutex() {
        return ((MutexEntry) mutexes.get(lockFile)).mutex;
    }
    
    private void createMutex() {
        synchronized (mutexes) {
            MutexEntry mutexEntry = (MutexEntry) mutexes.get(lockFile);
            if (mutexEntry == null) {
                mutexes.put(lockFile, new MutexEntry());
            } else {
                mutexEntry.count ++;
            }
        }
    }

    private void releaseMutex() {
        synchronized (mutexes) {
            MutexEntry mutexEntry = (MutexEntry) mutexes.get(lockFile);
            if (mutexEntry.count == 1) {
                mutexes.remove(lockFile);
            } else {
                mutexEntry.count--;
            }
        }
    }
    
    class MutexEntry {
        int count = 1;
        Object mutex = new Object();
    };
}
