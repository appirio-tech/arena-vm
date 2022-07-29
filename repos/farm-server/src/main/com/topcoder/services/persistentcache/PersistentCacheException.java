/*
 * PersistentCacheException
 * 
 * Created 05/16/2007
 */
package com.topcoder.services.persistentcache;

/**
 * Exception thrown when a PersistentCache fails when trying to complete an
 * operation.
 * 
 * @author Diego Belfer (mural)
 * @version $Id: PersistentCacheException.java 65513 2007-09-24 19:31:43Z thefaxman $
 */
public class PersistentCacheException extends Exception {

    public PersistentCacheException() {
    }

    public PersistentCacheException(String message) {
        super(message);
    }

    public PersistentCacheException(Throwable cause) {
        super(cause);
    }

    public PersistentCacheException(String message, Throwable cause) {
        super(message, cause);
    }

}
