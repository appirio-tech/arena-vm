/*
 * ReadWriteLockManager
 *
 * Created 03/23/2007
 */
package com.topcoder.farm.shared.util.concurrent;

import com.topcoder.farm.shared.util.SoftReferenceCache;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.ReadLock;
import com.topcoder.farm.shared.util.concurrent.ReadWriteLock.WriteLock;

/**
 * Container for ReadWriteLock. <p>
 *
 * Locks object are held in the manager while there are reachable.
 * This implementation provides a simple way to hold related locks, avoiding
 * the problem of simultaneous creation or disposal of the Locks. <p>
 *
 * Any lock obtained for a key is ensured to be the only existent lock for that key<p>
 *
 * Implementation: Locks are held in a SoftReferenceCache, and are only disposed when garbage collector
 * reclaims them. This means that no process is referencing it.
 *
 * @author Diego Belfer (mural)
 * @version $Id$
 */
public class ReadWriteLockManager {
    //FIXME mural test
    /**
     * Cache containing lock objects
     */
    private SoftReferenceCache locks = new SoftReferenceCache();

    /**
     * Obtains the ReadWriteLock for the given Key.<p>
     *
     * @param key The key whose lock is required.
     * @return The lock for the key.
     */
    public ReadWriteLock getLock(Object key) {
        synchronized (locks) {
            ReadWriteLock lock = (ReadWriteLock) locks.get(key);
            if (lock == null) {
                lock = new ReadWriteLock();
                locks.put(key, lock);
            }
            return lock;
        }
    }

    /**
     * Obtains a WriteLock for the ReadWriteLock object
     * associated to the given Key.<p>
     *
     * @param key The key whose lock is required.
     * @return The lock for the key.
     */
    public WriteLock getWriteLock(Object key) {
        return getLock(key).writeLock();
    }


    /**
     * Obtains a ReadLock for the ReadWriteLock object
     * associated to the given Key.<p>
     *
     * @param key The key whose lock is required.
     * @return The lock for the key.
     */
    public ReadLock getReadLock(Object key) {
        return getLock(key).readLock();
    }


    /**
     * Removes the ReadWriteLock associated to the given Key, if any.<p>
     *
     * This method should not be used, since removing the Lock object could
     * break Lock unicity. It is provided to allow manual clean up by the developer.
     *
     * @param key The key whose lock is being removed.
     */
    public void removeLock(Object key) {
        synchronized (locks) {
            locks.remove(key);
        }
    }
}
